package pds.cep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pds.cep.Constants.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

/**
 * SlidingWindowEngineTest
 */
class SlidingWindowEngineTest {

  private final ByteArrayOutputStream out = new ByteArrayOutputStream();

  @Test
  public void testRun() {
    System.setOut(new PrintStream(out));
    Dfa dfa = this.genSampleDfa(this.genSampleNfa());

    Stream stream = new SampleStream(3);
    SlidingWndowEngine engine = new SlidingWndowEngine(stream, dfa, 3, 2);
    engine.run();
    assertEquals("[0 : 2]: 0.09000\n", out.toString());

    out.reset();
    stream = new SampleStream(8);
    engine = new SlidingWndowEngine(stream, dfa, 4, 3);
    engine.run();
    assertEquals("[0 : 3]: 0.09000\n[3 : 6]: 0.09000\n", out.toString());

    System.setOut(null);
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
}