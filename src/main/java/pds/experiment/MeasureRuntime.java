package pds.experiment;

import static pds.cep.Constants.*;

import java.util.List;

import pds.cep.Constants;
import pds.cep.Dfa;
import pds.cep.Engine;
import pds.cep.Nfa;
import pds.cep.SimulationStream;
import pds.cep.SlicedSlidingWndowEngine;
import pds.cep.SlidingWndowEngine;
import pds.cep.Stream;

/**
 * MeasureRuntime
 */
public class MeasureRuntime {

  public static final int STREMAM_SIZE = 1000000;
  public static final int EVENT_SIZE = 100;
  public static final int QUERY_SIZE = 30;
  public static final int WINDOW_SIZE = 3000;
  public static final int SLIDE_SIZE = 50;
  public static final int LOOP_NUM = 5;

  public static final List<Integer> WINDOWS = List.of(500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000);
  public static final List<Integer> SLIDES = List.of(25, 50, 75, 100, 125, 150, 175, 200, 225, 250);
  public static final List<Integer> QUERIES = List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

  public static void main(String[] args) {
    Constants.outputEnabled = false;
    Stream stream = new SimulationStream(STREMAM_SIZE, EVENT_SIZE);
    Dfa dfa = genSampleDfa(genSampleNfa(QUERY_SIZE));
    Engine engine = new SlidingWndowEngine(stream, dfa, WINDOW_SIZE, SLIDE_SIZE);

    // test run for JIT compile
    System.err.println("wait JIT compiling...");
    for (int i = 0; i < LOOP_NUM; ++i) {
      long runtime = measureRuntime(stream, engine);
      System.err.println(i + "-th run: " + runtime);
    }
    System.out.println();
    System.err.println();

    // measure runtime over window sizes
    stdOutErrPrintln("Measure runtime over window sizes.");
    System.out.println();
    dfa = genSampleDfa(genSampleNfa(QUERY_SIZE));
    stdOutErrPrintln("Without Slicing");
    System.out.println("window size, runtime");
    for (int windowSize : WINDOWS) {
      engine = new SlidingWndowEngine(stream, dfa, windowSize, SLIDE_SIZE);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(windowSize + ", " + avg);
    }
    stdOutErrPrintln("");
    stdOutErrPrintln("With Slicing");
    System.out.println("window size, runtime");
    for (int windowSize : WINDOWS) {
      engine = new SlicedSlidingWndowEngine(stream, dfa, windowSize, SLIDE_SIZE);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(windowSize + ", " + avg);
    }
    System.out.println();
    System.err.println();

    // measure runtime over slide sizes
    stdOutErrPrintln("Measure runtime over slide sizes.");
    System.out.println();
    stdOutErrPrintln("Without Slicing");
    System.out.println("slide size, runtime");
    dfa = genSampleDfa(genSampleNfa(QUERY_SIZE));
    for (int slideSize : SLIDES) {
      engine = new SlidingWndowEngine(stream, dfa, WINDOW_SIZE, slideSize);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(slideSize + ", " + avg);
    }
    stdOutErrPrintln("");
    stdOutErrPrintln("With Slicing");
    System.out.println("slide size, runtime");
    dfa = genSampleDfa(genSampleNfa(QUERY_SIZE));
    for (int slideSize : SLIDES) {
      engine = new SlicedSlidingWndowEngine(stream, dfa, WINDOW_SIZE, slideSize);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(slideSize + ", " + avg);
    }
    System.out.println();
    System.err.println();

    // measure runtime over window sizes
    stdOutErrPrintln("Measure runtime over DFA sizes.");
    System.out.println();
    stdOutErrPrintln("Without Slicing");
    System.out.println("DFA size, runtime");
    for (int querySize : QUERIES) {
      dfa = genSampleDfa(genSampleNfa(querySize));
      engine = new SlidingWndowEngine(stream, dfa, WINDOW_SIZE, SLIDE_SIZE);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(dfa.size() + ", " + avg);
    }
    stdOutErrPrintln("");
    stdOutErrPrintln("With Slicing");
    System.out.println("DFA size, runtime");
    for (int querySize : QUERIES) {
      dfa = genSampleDfa(genSampleNfa(querySize));
      engine = new SlicedSlidingWndowEngine(stream, dfa, WINDOW_SIZE, SLIDE_SIZE);
      double avg = measureAvgRuntime(stream, engine);
      System.out.println(dfa.size() + ", " + avg);
    }
    System.out.println();
    System.err.println();
  }

  private static void stdOutErrPrintln(String print) {
    System.out.println(print);
    System.err.println(print);
  }

  private static long measureRuntime(Stream stream, Engine engine) {
    stream.resetOffset();
    long currentTime = System.nanoTime();
    engine.run();
    return System.nanoTime() - currentTime;
  }

  private static double measureAvgRuntime(Stream stream, Engine engine) {
    long sum = 0;
    for (int i = 0; i < LOOP_NUM; ++i) {
      long runtime = measureRuntime(stream, engine);
      sum += runtime;
      System.err.println(i + "-th run: " + runtime);
    }
    return sum / LOOP_NUM;
  }

  private static Nfa genSampleNfa(int length) {
    Nfa.Builder builder = new Nfa.Builder()//
        .setInitialState(0)//
        .addTransition(0, 0, WILDCARD);
    for (int i = 0; i < length; ++i) {
      builder.addTransition(i, i + 1, String.valueOf(i))//
          .addTransition(i + 1, i + 1, WILDCARD);
    }
    return builder.addTransition(length, length, WILDCARD)//
        .setFinalState(length)//
        .build();
  }

  private static Dfa genSampleDfa(Nfa nfa) {
    return new Dfa.Builder()//
        .subsetConstruction(nfa)//
        .minimize()//
        .build();
  }
}