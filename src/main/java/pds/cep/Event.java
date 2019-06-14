package pds.cep;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static pds.cep.Automaton.WILDCARD;

/**
 * Event
 */
class Event {

  public final int timestamp;

  private Map<String, Double> probs;

  public Event(int timestamp, Map<String, Double> probs) {
    Objects.requireNonNull(probs);
    this.timestamp = timestamp;
    this.probs = Collections.unmodifiableMap(probs);
  }

  public boolean hasSymbol(String symbol) {
    if (symbol.equals(WILDCARD))
      return true;
    else
      return this.probs.keySet().contains(symbol);
  }

  public double getProb(String symbol) {
    if (symbol.equals(WILDCARD))
      return 1;
    else
      return this.probs.getOrDefault(symbol, 0.0);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    this.probs.keySet().forEach(symbol -> {
      sb.append(symbol).append(":\t").append(probs.get(symbol)).append("\n");
    });
    return sb.toString();
  }
}