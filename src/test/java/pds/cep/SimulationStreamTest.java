package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * SimulationStreamTest
 */
class SimulationStreamTest {

  @Test
  public void testHasNext() {
    Stream classUnderTest = new SimulationStream(10, 3);
    IntStream.range(0, 10).forEach(i -> {
      assertEquals(true, classUnderTest.hasNext());
      classUnderTest.pull();
    });
    assertEquals(false, classUnderTest.hasNext());
    classUnderTest.resetOffset();
    IntStream.range(0, 10).forEach(i -> {
      assertEquals(true, classUnderTest.hasNext());
      classUnderTest.pull();
    });
    assertEquals(false, classUnderTest.hasNext());
  }
}