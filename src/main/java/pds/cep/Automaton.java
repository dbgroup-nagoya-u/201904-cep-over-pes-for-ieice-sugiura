package pds.cep;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Automaton
 */
abstract class Automaton {

  protected int initialState;

  protected Set<Integer> states;

  protected Set<Integer> finalStates;

  @Nonnull
  public int getInitialState() {
    return this.initialState;
  }

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

  @Nonnull
  public int size() {
    return this.states.size();
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