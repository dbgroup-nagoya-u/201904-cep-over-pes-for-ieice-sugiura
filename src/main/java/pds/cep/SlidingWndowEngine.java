package pds.cep;

import java.util.LinkedList;
import java.util.Queue;

/**
 * SlidingWndowEngine
 */
public class SlidingWndowEngine {

  private Stream stream;

  private Dfa dfa;

  private int windowSize;

  private int slidingSize;

  private Queue<SparseLeftMatrix> windowedProbVectors;

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

  public void run() {
    while (this.stream.hasNext()) {
      Event e = this.stream.pull();

      // スライド条件を満たしているならば新しい時間窓を生成
      if (++this.countForSlide > this.slidingSize) {
        SparseLeftMatrix newProbVec = new SparseLeftMatrix(1, this.dfa.size());
        newProbVec.set(0, this.dfa.getInitialState(), 1.0);
        this.windowedProbVectors.add(newProbVec);
        this.countForSlide = 1;
      }

      // 遷移確率行列を用意し，各時間窓の確率ベクトルを更新
      SparseRightMatrix tranMatrix = new SparseRightMatrix(this.dfa.size(), this.dfa.size());
      this.dfa.genTransitionMatrix(tranMatrix, e);
      this.windowedProbVectors.forEach(vec -> vec.product(tranMatrix));

      // 時間窓内のイベントが全て処理されたなら出力
      if (++this.countForOutput > this.windowSize) {
        SparseLeftMatrix resultVec = this.windowedProbVectors.poll();
        String output = this.genWindowStr(e.getTimeStamp()) + ": "
            + String.format("%.5f", this.dfa.sumAcceptProb(resultVec)) + "\n";
        System.out.print(output);
        this.countForOutput -= this.slidingSize;
      }
    }
  }

  private String genWindowStr(int timestamp) {
    return "[" + (timestamp - this.windowSize + 1) + " : " + timestamp + "]";
  }
}