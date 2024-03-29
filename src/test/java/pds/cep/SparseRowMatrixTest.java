package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * SparseMatrixTest
 */
class SparseRowMatrixTest {

  @Test
  public void testIndexOutOfBoundsException() {
    SparseMatrix classUnderTest = new SparseRowMatrix(4, 4);

    Throwable exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.set(-1, 0, 1.0));
    assertEquals("Index: (-1, 0), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.set(0, -1, 1.0));
    assertEquals("Index: (0, -1), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.set(4, 3, 1.0));
    assertEquals("Index: (4, 3), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.set(3, 4, 1.0));
    assertEquals("Index: (3, 4), Size: 4 x 4", exception.getMessage());

    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.get(-1, 0));
    assertEquals("Index: (-1, 0), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.get(0, -1));
    assertEquals("Index: (0, -1), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.get(4, 3));
    assertEquals("Index: (4, 3), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.get(3, 4));
    assertEquals("Index: (3, 4), Size: 4 x 4", exception.getMessage());
  }

  @Test
  public void testSetterGetter() {
    SparseMatrix classUnderTest = new SparseRowMatrix(4, 4);

    classUnderTest.set(0, 0, 1.0);
    classUnderTest.set(0, 3, 2.0);
    classUnderTest.set(3, 0, 3.0);
    classUnderTest.set(3, 3, 4.0);

    assertEquals(1.0, classUnderTest.get(0, 0));
    assertEquals(2.0, classUnderTest.get(0, 3));
    assertEquals(3.0, classUnderTest.get(3, 0));
    assertEquals(4.0, classUnderTest.get(3, 3));

    assertEquals(0.0, classUnderTest.get(0, 1));
    assertEquals(0.0, classUnderTest.get(1, 1));
  }

  @Test
  public void testProduct() {
    double[][] productArrays = { { 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0, 0.0 },
        { 0.0, 0.0, 0.0, 0.0 } },
        leftArrays = { { 1.0, 2.0, 0.0 }, { 0.0, 2.0, 4.0 }, { 3.0, 4.0, 5.0 }, { 0.0, 0.0, 1.0 } },
        rightArrays = { { 1.0, 3.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0, 4.0 }, { 0.0, 4.0, 0.0, 0.0 } };
    for (int row = 0; row < 4; row++) {
      for (int column = 0; column < 4; column++) {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
          sum += leftArrays[row][i] * rightArrays[i][column];
        }
        productArrays[row][column] = sum;
      }
    }

    SparseRowMatrix leftMatrix = new SparseRowMatrix(4, 3);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 3; j++) {
        leftMatrix.set(i, j, leftArrays[i][j]);
      }
    }
    SparseColumnMatrix rightMatrix = new SparseColumnMatrix(3, 4);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 4; j++) {
        rightMatrix.set(i, j, rightArrays[i][j]);
      }
    }
    SparseRowMatrix productMat = leftMatrix.product(rightMatrix);

    IntStream.range(0, 4).forEach(i -> {
      IntStream.range(0, 4).forEach(j -> {
        assertEquals(productArrays[i][j], productMat.get(i, j));
      });
    });
  }

  @Test
  public void testMismatchingMatricesForProduct() {
    SparseRowMatrix leftMatrix = new SparseRowMatrix(4, 3);
    SparseColumnMatrix rightMatrix = new SparseColumnMatrix(4, 4);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> leftMatrix.product(rightMatrix));
    assertEquals("Left matrix: 4 x 3, Right matrix: 4 x 4", exception.getMessage());
  }
}