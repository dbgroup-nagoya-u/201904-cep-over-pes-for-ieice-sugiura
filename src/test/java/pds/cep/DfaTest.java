package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pds.cep.Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * testDfa
 */
class DfaTest {

  @Test
  public void testBuildDfa() {
    Dfa dfa = this.genSampleDfa(this.genSampleNfa());

    assertEquals(0, dfa.getInitialState());
    assertEquals(4, dfa.getStates().size());
    assertEquals(1, dfa.getFinalStates().size());
  }

  @Test
  public void testProbCalculation() {
    Dfa dfa = this.genSampleDfa(this.genSampleNfa());
    SparseMatrix matrix = new SparseRightMatrix(4, 4);
    Event e = this.genSampleEvent();

    dfa.genTransitionMatrix(matrix, e);
    assertEquals(0, matrix.get(0, 0), 1E-5);
    assertEquals(0.3, matrix.get(0, 1), 1E-5);
    assertEquals(0.7, matrix.get(0, 2), 1E-5);
    assertEquals(0, matrix.get(0, 3), 1E-5);
    assertEquals(0, matrix.get(1, 0), 1E-5);
    assertEquals(0.7, matrix.get(1, 1), 1E-5);
    assertEquals(0, matrix.get(1, 2), 1E-5);
    assertEquals(0.3, matrix.get(1, 3), 1E-5);
    assertEquals(0, matrix.get(2, 0), 1E-5);
    assertEquals(0, matrix.get(2, 1), 1E-5);
    assertEquals(1.0, matrix.get(2, 2), 1E-5);
    assertEquals(0, matrix.get(2, 3), 1E-5);
    assertEquals(0, matrix.get(3, 0), 1E-5);
    assertEquals(0.7, matrix.get(3, 1), 1E-5);
    assertEquals(0, matrix.get(3, 2), 1E-5);
    assertEquals(0.3, matrix.get(3, 3), 1E-5);
  }

  private Nfa genSampleNfa() {
    return new Nfa.Builder()//
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
  }

  private Dfa genSampleDfa(Nfa nfa) {
    return new Dfa.Builder()//
        .subsetConstruction(nfa)//
        .minimize()//
        .build();
  }

  private Event genSampleEvent() {
    Map<String, Double> probs = new HashMap<>();
    probs.put("a", 0.1);
    probs.put("b", 0.2);
    probs.put("c", 0.3);
    probs.put("d", 0.4);
    return new Event(0, probs);
  }
}