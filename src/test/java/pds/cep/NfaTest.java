package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pds.cep.Constants.*;

import org.junit.jupiter.api.Test;

/**
 * testNfa
 */
class NfaTest {

  @Test
  public void testBuildNfa() {
    Nfa nfa = new Nfa.Builder()//
        .setInitialState(0)//
        .addTransition(0, 1, EPSILON)//
        .addTransition(1, 2, "a")//
        .addTransition(2, 2, WILDCARD)//
        .addTransition(2, 3, "c")//
        .addTransition(0, 4, EPSILON)//
        .addTransition(4, 5, "b")//
        .addTransition(5, 5, WILDCARD)//
        .addTransition(5, 3, "c")//
        .setFinalState(3)//
        .build();

    assertEquals(true, nfa.getStates().contains(0));
    assertEquals(false, nfa.getStates().contains(1));
    assertEquals(true, nfa.getStates().contains(2));
    assertEquals(true, nfa.getStates().contains(3));
    assertEquals(false, nfa.getStates().contains(4));
    assertEquals(true, nfa.getStates().contains(5));
    assertEquals(false, nfa.isFinalState(0));
    assertEquals(false, nfa.isFinalState(2));
    assertEquals(false, nfa.isFinalState(5));
    assertEquals(true, nfa.isFinalState(3));
  }

}