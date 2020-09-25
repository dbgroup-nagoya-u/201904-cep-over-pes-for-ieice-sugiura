package pds.cep;

import java.util.LinkedList;
import java.util.Queue;

/**
 * SlidingWndowEngine
 */
public class SlicedSlidingWndowEngine implements Engine {

  private Stream stream;

  private Dfa dfa;

  private int windowSize;

  private int slidingSize;

  private Queue<SparseColumnMatrix> slicedTranMatrices;

  private SparseRowMatrix updatingTranMatrix;

  /** 処理したイベント数を数える．スライド後に初期化． */
  private int countForSlide;

  /** 処理したイベント数を数える．処理結果を出力するたびに更新． */
  private int countForOutput;

  public SlicedSlidingWndowEngine(Stream stream, Dfa dfa, int windowSize, int slidingSize) {
    this.stream = stream;
    this.dfa = dfa;
    this.windowSize = windowSize;
    this.slidingSize = slidingSize;
    this.slicedTranMatrices = new LinkedList<>();
    this.countForSlide = 0;
    this.countForOutput = 0;
    this.updatingTranMatrix = this.genIdentityMatrix(this.dfa.size());
  }

  @Override
  public void run() {
    while (this.stream.hasNext()) {
      Event e = this.stream.pull();

      // スライド条件を満たしているならば新しいチャンクを生成
      if (++this.countForSlide >= this.slidingSize) {
        // 確率計算のために，列指向の行列に変換してから保持
        this.slicedTranMatrices.add(new SparseColumnMatrix(this.updatingTranMatrix));
        this.updatingTranMatrix = this.genIdentityMatrix(this.dfa.size());
        this.countForSlide = 0;
      }

      // 遷移確率行列を用意し，チャンクの遷移確率を更新
      SparseColumnMatrix tranMatrix = new SparseColumnMatrix(this.dfa.size(), this.dfa.size());
      this.dfa.genTransitionMatrix(tranMatrix, e);
      this.updatingTranMatrix = this.updatingTranMatrix.product(tranMatrix);

      // 時間窓内のイベントが全て処理されたなら出力
      if (++this.countForOutput >= this.windowSize) {
        SparseRowVector resultVec = new SparseRowVector(this.dfa.size());
        resultVec.set(this.dfa.getInitialState(), 1.0);
        for (SparseColumnMatrix tranMat : this.slicedTranMatrices) {
          resultVec = tranMat.product(resultVec);
        }
        resultVec = new SparseColumnMatrix(this.updatingTranMatrix).product(resultVec);

        if (Constants.outputEnabled) {
          String output = this.genWindowStr(e.getTimeStamp()) + ": "
              + String.format("%.5f", this.dfa.sumAcceptProb(resultVec)) + "\n";
          System.out.print(output);
        }
        this.slicedTranMatrices.poll();
        this.countForOutput -= this.slidingSize;
      }
    }
  }

  private SparseRowMatrix genIdentityMatrix(int size) {
    SparseRowMatrix identityMatrix = new SparseRowMatrix(size, size);
    for (int i = 0; i < size; ++i) {
      identityMatrix.set(i, i, 1.0);
    }
    return identityMatrix;
  }

  private String genWindowStr(int timestamp) {
    return "[" + (timestamp - this.windowSize + 1) + " : " + timestamp + "]";
  }
}