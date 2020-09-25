package pds.cep;

/**
 * SparseMatrix
 */
abstract class SparseMatrix {

  protected final int rowSize;

  protected final int columnSize;

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param columnSize
   */
  public SparseMatrix(int rowSize, int columnSize) {
    this.rowSize = rowSize;
    this.columnSize = columnSize;
  }

  abstract public void add(int row, int column, double value);

  abstract public void set(int row, int column, double value);

  abstract public double get(int row, int column);

  protected void rangeCheckForAddGet(int row, int column) {
    if (row < 0 || row >= this.rowSize || column < 0 || column >= this.columnSize) {
      throw new IndexOutOfBoundsException(this.outOfBoundsMsg(row, column));
    }
  }

  private String outOfBoundsMsg(int row, int column) {
    return "Index: (" + row + ", " + column + "), Size: " + this.toSizeStr(this);
  }

  protected void rangeCheckForProduct(SparseMatrix rightMatrix) {
    if (rightMatrix.rowSize != this.columnSize) {
      throw new IllegalArgumentException(this.illegalMatricesForProductMsg(rightMatrix));
    }
  }

  private String illegalMatricesForProductMsg(SparseMatrix rightMatrix) {
    return "Left matrix: " + this.toSizeStr(this) + ", Right matrix: " + this.toSizeStr(rightMatrix);
  }

  protected String toSizeStr(SparseMatrix matrix) {
    return matrix.rowSize + " x " + matrix.columnSize;
  }
}