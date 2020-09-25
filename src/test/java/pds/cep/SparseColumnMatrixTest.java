package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * SparseMatrixTest
 */
class SparseColumnMatrixTest {

  @Test
  public void testIndexOutOfBoundsException() {
    SparseMatrix classUnderTest = new SparseColumnMatrix(4, 4);

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
    SparseMatrix classUnderTest = new SparseColumnMatrix(4, 4);

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
}