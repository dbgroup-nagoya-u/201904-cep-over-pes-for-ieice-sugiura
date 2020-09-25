package pds.cep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Stream
 */
public class SimulationStream extends Stream {

  public SimulationStream(int streamSize, int eventSymbolSize) {
    super(streamSize);
    this.genSimulationStream(streamSize, eventSymbolSize);
  }

  /**
   * 指定した数のイベントを持つストリームを生成する．各イベントは指定された数のイベントシンボルを持ち，確率はランダムに付与される．
   *
   * @param streamSize
   * @param eventSymbolSize
   */
  private void genSimulationStream(int streamSize, int eventSymbolSize) {
    this.stream.clear();
    for (int i = 0; i < streamSize; ++i) {
      List<Double> probabilities = new Random().doubles(eventSymbolSize).boxed().collect(Collectors.toList());
      double sum = probabilities.stream().mapToDouble(Double::doubleValue).sum();
      probabilities = probabilities.stream().map(v -> v / sum).collect(Collectors.toList());
      Map<String, Double> probDist = new HashMap<>();
      for (int j = 0; j < eventSymbolSize; ++j) {
        probDist.put(String.valueOf(j), probabilities.get(j));
      }
      this.stream.add(new Event(i, probDist));
    }
  }
}