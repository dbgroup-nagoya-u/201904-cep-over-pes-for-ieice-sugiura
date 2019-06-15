package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pds.cep.Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * EventTest
 */
class EventTest {

  @Test
  public void testGetProb() {
    Event e = this.generateTestEvent();
    assertEquals(0.3, e.getProb("a"));
    assertEquals(0.5, e.getProb("b"));
    assertEquals(0.2, e.getProb("c"));
    assertEquals(0.0, e.getProb("d"));
    assertEquals(1.0, e.getProb(WILDCARD));
  }

  @Test
  public void testHasSymbol() {
    Event e = this.generateTestEvent();
    assertEquals(true, e.hasSymbol("a"));
    assertEquals(true, e.hasSymbol("b"));
    assertEquals(true, e.hasSymbol("c"));
    assertEquals(false, e.hasSymbol("d"));
    assertEquals(true, e.hasSymbol(WILDCARD));
  }

  private Event generateTestEvent() {
    Map<String, Double> probDist = new HashMap<>();
    probDist.put("a", 0.3);
    probDist.put("b", 0.5);
    probDist.put("c", 0.2);
    return new Event(0, probDist);
  }
}