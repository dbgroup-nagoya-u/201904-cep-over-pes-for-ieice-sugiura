package pds.cep;

import java.util.LinkedList;

/**
 * SlidingWndowEngine
 */
public class SlidingWndowEngine implements Engine {

  private Stream stream;

  private Dfa dfa;

  private int windowSize;

  private int slidingSize;

  private LinkedList<SparseRowVector> windowedProbVectors;

  /** 処理したイベント数を数える．スライド後に初期化． */
  private int countForSlide;

  /** 処理したイベント数を数える．処理結果を出力するたびに更新． */
  private int countForOutput;

  public SlidingWndowEngine(Stream stream, Dfa dfa, int windowSize, int slidingSize) {
    this.stream = stream;
    this.dfa = dfa;
    this.windowSize = windowSize;
    this.slidingSize = slidingSize;
    this.windowedProbVectors = new LinkedList<>();
    this.countForSlide = slidingSize;
    this.countForOutput = 1;
  }

  @Override
  public void run() {
    while (this.stream.hasNext()) {
      Event e = this.stream.pull();

      // スライド条件を満たしているならば新しい時間窓を生成
      if (++this.countForSlide > this.slidingSize) {
        SparseRowVector newProbVec = new SparseRowVector(this.dfa.size());
        newProbVec.set(this.dfa.getInitialState(), 1.0);
        this.windowedProbVectors.add(newProbVec);
        this.countForSlide = 1;
      }

      // 遷移確率行列を用意し，各時間窓の確率ベクトルを更新
      SparseColumnMatrix tranMatrix = new SparseColumnMatrix(this.dfa.size(), this.dfa.size());
      this.dfa.genTransitionMatrix(tranMatrix, e);
      var iter = this.windowedProbVectors.iterator();
      int count = 0;
      while (iter.hasNext()) {
        SparseRowVector newProbVec = tranMatrix.product(iter.next());
        this.windowedProbVectors.set(count++, newProbVec);
      }

      // 時間窓内のイベントが全て処理されたなら出力
      if (++this.countForOutput > this.windowSize) {
        SparseRowVector resultVec = this.windowedProbVectors.poll();
        if (Constants.outputEnabled) {
          String output = this.genWindowStr(e.getTimeStamp()) + ": "
              + String.format("%.5f", this.dfa.sumAcceptProb(resultVec)) + "\n";
          System.out.print(output);
        }
        this.countForOutput -= this.slidingSize;
      }
    }
  }

  private String genWindowStr(int timestamp) {
    return "[" + (timestamp - this.windowSize + 1) + " : " + timestamp + "]";
  }
}