package pds.experiment;

import static pds.cep.Constants.*;

import pds.cep.Constants;
import pds.cep.Dfa;
import pds.cep.Nfa;
import pds.cep.SimulationStream;
import pds.cep.SlidingWndowEngine;
import pds.cep.Stream;

/**
 * MeasureRuntime
 */
public class MeasureRuntime {

  public static void main(String[] args) {
    Constants.outputEnabled = false;
    Dfa dfa = genSampleDfa(genSampleNfa());
    Stream stream = new SimulationStream(1000000, 30);
    SlidingWndowEngine engine = new SlidingWndowEngine(stream, dfa, 3000, 50);

    long currentTime = System.nanoTime();
    engine.run();
    long runTime = System.nanoTime() - currentTime;

    System.out.println(String.format("%,d", runTime));
  }

  private static Nfa genSampleNfa() {
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

  private static Dfa genSampleDfa(Nfa nfa) {
    return new Dfa.Builder()//
        .subsetConstruction(nfa)//
        .minimize()//
        .build();
  }
}