package pds.cep;

import java.util.HashMap;
import java.util.Map;

/**
 * SampleStream
 */
class SampleStream extends Stream {

  public SampleStream(int streamSize) {
    super(streamSize);
    this.genSampleStream(streamSize);
  }

  private void genSampleStream(int streamSize) {
    this.stream.clear();
    Map<String, Double> probs = new HashMap<>();
    probs.put("a", 0.1);
    probs.put("b", 0.2);
    probs.put("c", 0.3);
    probs.put("d", 0.4);

    for (int i = 0; i < streamSize; ++i) {
      this.stream.add(new Event(i, probs));
    }
  }

}