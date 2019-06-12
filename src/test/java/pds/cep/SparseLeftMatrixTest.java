package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * SparseMatrixTest
 */
class SparseLeftMatrixTest {

  @Test
  public void testIndexOutOfBoundsException() {
    SparseMatrix classUnderTest = new SparseLeftMatrix(4, 4);

    Throwable exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.add(-1, 0, 1.0));
    assertEquals("Index: (-1, 0), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.add(0, -1, 1.0));
    assertEquals("Index: (0, -1), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.add(4, 3, 1.0));
    assertEquals("Index: (4, 3), Size: 4 x 4", exception.getMessage());
    exception = assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.add(3, 4, 1.0));
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
    SparseMatrix classUnderTest = new SparseLeftMatrix(4, 4);

    classUnderTest.add(0, 0, 1.0);
    classUnderTest.add(0, 3, 2.0);
    classUnderTest.add(3, 0, 3.0);
    classUnderTest.add(3, 3, 4.0);

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

    SparseLeftMatrix leftMatrix = new SparseLeftMatrix(4, 3);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 3; j++) {
        leftMatrix.add(i, j, leftArrays[i][j]);
      }
    }
    SparseRightMatrix rightMatrix = new SparseRightMatrix(3, 4);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 4; j++) {
        rightMatrix.add(i, j, rightArrays[i][j]);
      }
    }
    SparseMatrix productMatrix = leftMatrix.product(rightMatrix);

    IntStream.range(0, 4).forEach(i -> {
      IntStream.range(0, 4).forEach(j -> {
        assertEquals(productArrays[i][j], productMatrix.get(i, j));
      });
    });
  }

  @Test
  public void testMismatchingMatricesForProduct() {
    SparseLeftMatrix leftMatrix = new SparseLeftMatrix(4, 3);
    SparseRightMatrix rightMatrix = new SparseRightMatrix(4, 4);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> leftMatrix.product(rightMatrix));
    assertEquals("Left matrix: 4 x 3, Right matrix: 4 x 4", exception.getMessage());
  }
}