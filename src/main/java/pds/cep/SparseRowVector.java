package pds.cep;

/**
 * SparseRowVector
 */
class SparseRowVector extends SparseVector {

  public SparseRowVector(int size) {
    super(size);
  }

  public double product(SparseColumnVector rightVector) {
    this.sizeCheckForProduct(rightVector);

    double sum = 0.0;
    if (this.vector.keySet().size() < rightVector.vector.keySet().size()) {
      for (var leftEntry : this.vector.entrySet()) {
        int index = leftEntry.getKey();
        if (rightVector.vector.containsKey(index)) {
          sum += leftEntry.getValue() * rightVector.vector.get(index);
        }
      }
    } else {
      for (var rightEntry : rightVector.vector.entrySet()) {
        int index = rightEntry.getKey();
        if (this.vector.containsKey(index)) {
          sum += this.vector.get(index) * rightEntry.getValue();
        }
      }
    }

    return sum;
  }
}