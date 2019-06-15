package pds.cep;

import java.util.HashMap;
import java.util.Map;

/**
 * SparseLeftMatrix
 */
class SparseLeftMatrix extends SparseMatrix {

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param colmunSize
   */
  public SparseLeftMatrix(int rowSize, int colmunSize) {
    super(rowSize, colmunSize);
  }

  @Override
  public void set(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(row, new HashMap<>());
    this.matrix.get(row).put(column, value);
  }

  @Override
  public double get(int row, int column) {
    this.rangeCheckForAddGet(row, column);
    if (this.matrix.containsKey(row)) {
      return this.matrix.get(row).getOrDefault(column, 0.0);
    } else {
      return 0.0;
    }
  }

  /**
   * 疎行列同士の積を計算する．ただし，実装は naive な手法を使用している．
   *
   * @param rightMatrix
   */
  public void product(SparseRightMatrix rightMatrix) {
    this.rangeCheckForProduct(rightMatrix);

    SparseLeftMatrix product = new SparseLeftMatrix(this.rowSize, rightMatrix.colmunSize);

    for (Map.Entry<Integer, Map<Integer, Double>> leftEntry : this.matrix.entrySet()) {
      Map<Integer, Double> verticalVec = leftEntry.getValue();
      for (Map.Entry<Integer, Map<Integer, Double>> rightEntry : rightMatrix.matrix.entrySet()) {
        double sum = 0.0;
        for (Map.Entry<Integer, Double> horizontalVec : rightEntry.getValue().entrySet()) {
          int index = horizontalVec.getKey();
          if (verticalVec.containsKey(index)) {
            sum += verticalVec.get(index) * horizontalVec.getValue();
          }
        }
        int row = leftEntry.getKey();
        int column = rightEntry.getKey();
        product.set(row, column, sum);
      }
    }

    this.rowSize = product.rowSize;
    this.colmunSize = product.colmunSize;
    this.matrix = product.matrix;
  }
}