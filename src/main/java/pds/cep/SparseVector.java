package pds.cep;

import java.util.HashMap;
import java.util.Map;

/**
 * SparseVector
 */
abstract class SparseVector {

  protected int size;

  protected Map<Integer, Double> vector;

  /**
   * Default constructor.
   *
   * @param size
   */
  public SparseVector(int size) {
    this.size = size;
    this.vector = new HashMap<>();
  }

  public void add(int index, double value) {
    this.rangeCheckForAddGet(index);
    this.vector.merge(index, value, (v1, v2) -> v1 + v2);
  }

  public void set(int index, double value) {
    this.rangeCheckForAddGet(index);
    this.vector.put(index, value);
  }

  public double get(int index) {
    this.rangeCheckForAddGet(index);
    return this.vector.getOrDefault(index, 0.0);
  }

  private void rangeCheckForAddGet(int index) {
    if (index < 0 || index >= this.size) {
      throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
    }
  }

  private String outOfBoundsMsg(int index) {
    return "Index: " + index + ", Size: " + this.size;
  }

  protected void sizeCheckForProduct(SparseVector rightVector) {
    if (this.size != rightVector.size) {
      throw new IllegalArgumentException(this.illegalVectorsForProductMsg(rightVector));
    }
  }

  private String illegalVectorsForProductMsg(SparseVector rightVector) {
    return "Left vector: " + this.size + ", Right vector: " + rightVector.size;
  }

}