package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pds.cep.Automaton.WILDCARD;
import static pds.cep.Automaton.EPSILON;

import org.junit.jupiter.api.Test;

/**
 * testDfa
 */
class TestDfa {

  @Test
  public void testBuildDfa() {
    Nfa nfa = new Nfa.Builder()//
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

    Dfa dfa = new Dfa.Builder()//
        .subsetConstruction(nfa)//
        .minimize()//
        .build();

    assertEquals(4, dfa.getStates().size());
    assertEquals(1, dfa.getFinalStates().size());
  }
}