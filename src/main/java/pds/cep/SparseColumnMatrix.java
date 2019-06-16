package pds.cep;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * SparseRightMatrix
 */
class SparseColumnMatrix extends SparseMatrix {

  private Map<Integer, SparseColumnVector> matrix;

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param colmunSize
   */
  public SparseColumnMatrix(int rowSize, int colmunSize) {
    super(rowSize, colmunSize);
    this.matrix = new HashMap<>();
  }

  public SparseColumnMatrix(SparseRowMatrix rowMatrix) {
    super(rowMatrix.rowSize, rowMatrix.columnSize);
    this.matrix = new HashMap<>();
    rowMatrix.copyMatrixInto(this);
  }

  @Override
  public void set(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(column, new SparseColumnVector(this.rowSize));
    this.matrix.get(column).set(row, value);
  }

  @Override
  public void add(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(column, new SparseColumnVector(this.rowSize));
    this.matrix.get(column).add(row, value);
  }

  @Override
  public double get(int row, int column) {
    this.rangeCheckForAddGet(row, column);
    if (this.matrix.containsKey(column)) {
      return this.matrix.get(column).get(row);
    } else {
      return 0.0;
    }
  }

  public SparseRowVector product(SparseRowVector leftVec) {
    SparseRowVector productVec = new SparseRowVector(this.columnSize);
    this.matrix.forEach((column, rightVec) -> {
      productVec.set(column, leftVec.product(rightVec));
    });
    return productVec;
  }
}