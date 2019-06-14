package pds.cep;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Automaton
 */
public abstract class Automaton {

  public static final String EPSILON = "EPSILON";

  public static final String REJECT = "REJECT";

  public static final String WILDCARD = "WILDCARD";

  public static final int INITIALSTATE = 0;

  protected Set<Integer> states;

  protected Set<Integer> finalStates;

  @Nonnull
  public Set<Integer> getStates() {
    return this.states;
  }

  @Nonnull
  public boolean isFinalState(int stateId) {
    return this.finalStates.contains(stateId);
  }

  @Nonnull
  public Set<Integer> getFinalStates() {
    return this.finalStates;
  }

  protected void rangeCheckForGet(int stateId) {
    if (!this.states.contains(stateId)) {
      throw new IndexOutOfBoundsException(this.outOfBoundsMsg(stateId));
    }
  }

  private String outOfBoundsMsg(int stateId) {
    return "Index: " + stateId + ", States: " + this.states.toString();
  }

}