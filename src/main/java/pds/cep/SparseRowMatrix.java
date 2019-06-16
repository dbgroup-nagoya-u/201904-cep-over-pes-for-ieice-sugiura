package pds.cep;

import java.util.HashMap;
import java.util.Map;

/**
 * SparseLeftMatrix
 */
class SparseRowMatrix extends SparseMatrix {

  private Map<Integer, SparseRowVector> matrix;

  /**
   * Default constructor.
   *
   * @param rowSize
   * @param columnSize
   */
  public SparseRowMatrix(int rowSize, int columnSize) {
    super(rowSize, columnSize);
    this.matrix = new HashMap<>();
  }

  @Override
  public void set(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(row, new SparseRowVector(this.columnSize));
    this.matrix.get(row).set(column, value);
  }

  @Override
  public void add(int row, int column, double value) {
    this.rangeCheckForAddGet(row, column);
    if (Double.compare(value, 0.0) == 0) {
      return;
    }

    this.matrix.putIfAbsent(row, new SparseRowVector(this.columnSize));
    this.matrix.get(row).add(column, value);
  }

  @Override
  public double get(int row, int column) {
    this.rangeCheckForAddGet(row, column);
    if (this.matrix.containsKey(row)) {
      return this.matrix.get(row).get(column);
    } else {
      return 0.0;
    }
  }

  public void copyMatrixInto(SparseColumnMatrix copied) {
    this.sizeCheckForCopy(copied);
    this.matrix.forEach((row, rowVec) -> {
      rowVec.vector.forEach((column, value) -> copied.set(row, column, value));
    });
  }

  /**
   * 疎行列同士の積を計算する．ただし，実装は naive な手法を使用している．
   *
   * @param rightMatrix
   */
  public SparseRowMatrix product(SparseColumnMatrix rightMatrix) {
    this.rangeCheckForProduct(rightMatrix);

    SparseRowMatrix productMat = new SparseRowMatrix(this.rowSize, rightMatrix.columnSize);
    this.matrix.forEach((row, rowVec) -> {
      SparseRowVector productVec = rightMatrix.product(rowVec);
      productMat.matrix.put(row, productVec);
    });

    return productMat;
  }

  private void sizeCheckForCopy(SparseMatrix matrix) {
    if (this.rowSize != matrix.rowSize || this.columnSize != matrix.columnSize) {
      throw new IllegalArgumentException(this.illegalMatrixForCopyMsg(matrix));
    }
  }

  private String illegalMatrixForCopyMsg(SparseMatrix matrix) {
    return "Origin size: " + this.toSizeStr(this) + ", Objective size: " + this.toSizeStr(matrix);
  }

}