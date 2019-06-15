package pds.cep;

import static pds.cep.Constants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * NFA
 */
class Nfa extends Automaton {

  private Map<Integer, Map<String, Set<Integer>>> tranFunc;

  /**
   * Builder から NFA を生成する．
   *
   * @param builder
   */
  private Nfa(final Builder builder) {
    this.states = Collections.unmodifiableSet(builder.states);
    this.tranFunc = new HashMap<>();
    for (var state : builder.tranFunc.keySet()) {
      this.tranFunc.put(state, new HashMap<>());
      builder.tranFunc.get(state).forEach((symbol, nexts) -> {
        this.tranFunc.get(state).put(symbol, Collections.unmodifiableSet(nexts));
      });
    }
    this.finalStates = Collections.unmodifiableSet(builder.finalStates);
  }

  /**
   * 指定された状態・イベントシンボルに対応する遷移先を返す．ただし，状態が存在しない場合は例外を返す．
   *
   * @param stateId
   * @param eventSymbol
   * @return
   */
  @Nonnull
  public Set<Integer> getNextStates(int stateId, String eventSymbol) {
    this.rangeCheckForGet(stateId);

    Map<String, Set<Integer>> transitions = this.tranFunc.get(stateId);
    Set<Integer> nextIds = new HashSet<>();
    if (transitions.containsKey(eventSymbol)) {
      nextIds.addAll(this.tranFunc.get(stateId).get(eventSymbol));
    }
    if (transitions.containsKey(WILDCARD)) {
      nextIds.addAll(this.tranFunc.get(stateId).get(WILDCARD));
    }

    if (nextIds.isEmpty()) {
      return transitions.get(REJECT);
    } else {
      return nextIds;
    }
  }

  @Nonnull
  public Set<String> getEventSymbols(int stateId) {
    this.rangeCheckForGet(stateId);
    return this.tranFunc.get(stateId).keySet();
  }

  public static class Builder {
    private int initialState;
    private Set<Integer> states;
    private Map<Integer, Map<String, Set<Integer>>> tranFunc;
    private Set<Integer> finalStates;
    private static final int REJECTSTATE = Integer.MAX_VALUE;

    public Builder() {
      this.states = new HashSet<>();
      this.states.add(REJECTSTATE);
      this.tranFunc = new HashMap<>();
      this.finalStates = new HashSet<>();
    }

    /**
     * 与えられたNFAのdeep copyを作成する．
     *
     * @param origin
     */
    public Builder(Nfa origin) {
      this.initialState = origin.initialState;
      this.states = new HashSet<>(origin.states);
      this.tranFunc = new HashMap<>();
      for (var state : origin.tranFunc.keySet()) {
        this.tranFunc.put(state, new HashMap<>());
        origin.tranFunc.get(state).forEach((symbol, nexts) -> {
          this.tranFunc.get(state).put(symbol, new HashSet<>(nexts));
        });
      }
      this.finalStates = new HashSet<>(origin.finalStates);
    }

    /**
     * 指定された遷移や最終状態から NFA を構築する．なお，破棄による遷移，epsilon遷移の削除，到達不可能な状態の削除も行う．
     *
     * @return
     */
    public Nfa build() {
      this.addTransitionsForReject();
      this.removeEpsilonTransitions();
      this.removeUnreachableState();
      return new Nfa(this);
    }

    public Builder setInitialState(int stateId) {
      this.initialState = stateId;
      return this;
    }

    /**
     * 指定した状態間に遷移を加える．
     *
     * @param startId
     * @param endId
     * @param symbol
     */
    public Builder addTransition(int startId, int endId, String symbol) {
      this.states.add(startId);
      this.states.add(endId);
      this.tranFunc.putIfAbsent(startId, new HashMap<>());
      this.tranFunc.get(startId).putIfAbsent(symbol, new HashSet<>());
      this.tranFunc.get(startId).get(symbol).add(endId);
      return this;
    }

    /**
     * 最終状態の集合を追加する．
     *
     * @param finalStates
     */
    public Builder setFinalState(int stateId) {
      this.states.add(stateId);
      this.finalStates.add(stateId);
      return this;
    }

    /**
     * 遷移が不十分な状態に破棄による遷移を追加する．
     *
     * @return
     */
    private Builder addTransitionsForReject() {
      this.states.forEach(stateId -> {
        this.tranFunc.putIfAbsent(stateId, new HashMap<>());
        if (!this.tranFunc.get(stateId).containsKey(WILDCARD)//
            && !this.tranFunc.get(stateId).containsKey(REJECT)) {
          Set<Integer> rejectIds = new HashSet<>();
          rejectIds.add(REJECTSTATE);
          this.tranFunc.get(stateId).put(REJECT, rejectIds);
        }
      });
      return this;
    }

    /**
     * epsilon 遷移をNFAから削除する．
     *
     * @return
     */
    private Builder removeEpsilonTransitions() {
      // 構造の変化（i.e., epsilon 遷移の削除）がある間は反復する
      boolean modified = true;
      while (modified) {
        modified = false;
        for (int startId : this.states) {
          // epsilon 遷移で遷移可能な状態を列挙
          Set<Integer> epsilonStates = this.tranFunc.get(startId)//
              .getOrDefault(EPSILON, Collections.emptySet());
          for (int epsilonState : epsilonStates) {
            // epsilon 遷移を経由して到達可能な状態を現状態に追加
            for (var entry : this.tranFunc.get(epsilonState).entrySet()) {
              String eventSymbol = entry.getKey();
              Set<Integer> endIds = entry.getValue();
              this.tranFunc.get(startId).putIfAbsent(eventSymbol, new HashSet<>());
              modified |= this.tranFunc.get(startId).get(eventSymbol).addAll(endIds);
            }
            // epsilon 遷移で最終状態に到達する場合は，現状態も最終状態に
            if (this.finalStates.contains(epsilonState)) {
              modified |= this.finalStates.add(startId);
            }
          }
        }
      }
      this.tranFunc.values().forEach(transitions -> transitions.remove(EPSILON));
      return this;
    }

    /**
     * 初期状態から遷移不可能な状態を削除する．
     *
     * @return
     */
    private Builder removeUnreachableState() {
      // 初期状態から到達可能な状態を列挙する
      Set<Integer> reachableIds = new HashSet<>();
      boolean modified = reachableIds.add(this.initialState);
      while (modified) {
        modified = false;
        Set<Integer> currentIds = new HashSet<>(reachableIds);
        for (int currentId : currentIds) {
          for (Set<Integer> nextIds : this.tranFunc.get(currentId).values()) {
            modified |= reachableIds.addAll(nextIds);
          }
        }
      }
      // 到達不可能な状態を状態集合及び遷移関数から削除
      Set<Integer> unReachableIds = new HashSet<>(this.states);
      unReachableIds.removeAll(reachableIds);
      this.states.removeAll(unReachableIds);
      unReachableIds.forEach(id -> this.tranFunc.remove(id));
      this.finalStates.removeAll(unReachableIds);
      return this;
    }
  }
}