package pds.cep;

import java.util.Map;
import java.util.HashMap;

/**
 * SparseMatrix
 */
abstract class SparseMatrix {

  public final int rowSize;
  public final int colmunSize;
  protected Map<Integer, Map<Integer, Double>> matrix;

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param colmunSize
   */
  public SparseMatrix(int rowSize, int colmunSize) {
    this.rowSize = rowSize;
    this.colmunSize = colmunSize;
    this.matrix = new HashMap<>();
  }

  abstract public void add(int row, int column, double value);

  abstract public double get(int row, int column);

  protected void rangeCheckForAddGet(int row, int column) {
    if (row < 0 || row >= this.rowSize || column < 0 || column >= this.colmunSize) {
      throw new IndexOutOfBoundsException(this.outOfBoundsMsg(row, column));
    }
  }

  private String outOfBoundsMsg(int row, int column) {
    return "Index: (" + row + ", " + column + "), Size: " + this.rowSize + " x " + this.colmunSize + "";
  }

  protected void rangeCheckForProduct(SparseMatrix rightMatrix) {
    if (rightMatrix.rowSize != this.colmunSize) {
      throw new IllegalArgumentException(this.illegalMatricesForProductMsg(rightMatrix));
    }
  }

  private String illegalMatricesForProductMsg(SparseMatrix rightMatrix) {
    return "Left matrix: " + this.rowSize + " x " + this.colmunSize + ", Right matrix: " + rightMatrix.rowSize + " x "
        + rightMatrix.colmunSize + "";
  }
}