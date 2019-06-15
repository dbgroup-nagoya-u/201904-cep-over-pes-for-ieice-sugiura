package pds.cep;

import java.util.HashMap;

/**
 * SparseRightMatrix
 */
class SparseRightMatrix extends SparseMatrix {

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param colmunSize
   */
  public SparseRightMatrix(int rowSize, int colmunSize) {
    super(rowSize, colmunSize);
  }

  @Override
  public void set(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(column, new HashMap<>());
    this.matrix.get(column).put(row, value);
  }

  @Override
  public void add(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(column, new HashMap<>());
    this.matrix.get(column).merge(row, value, (v1, v2) -> v1 + v2);
  }

  @Override
  public double get(int row, int column) {
    this.rangeCheckForAddGet(row, column);
    if (this.matrix.containsKey(column)) {
      return this.matrix.get(column).getOrDefault(row, 0.0);
    } else {
      return 0.0;
    }
  }
}