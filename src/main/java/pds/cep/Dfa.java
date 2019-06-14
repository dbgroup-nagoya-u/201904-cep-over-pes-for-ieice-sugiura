package pds.cep;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Dfa
 */
public class Dfa extends Automaton {

  private Map<Integer, Map<String, Integer>> tranFunc;

  public Dfa(Builder builder) {
    this.states = Collections.unmodifiableSet(builder.states);
    this.finalStates = Collections.unmodifiableSet(builder.finalStates);
    this.tranFunc = new HashMap<>();
    builder.tranFunc.forEach((eventSymbol, nextId) -> this.tranFunc.put(eventSymbol, nextId));
  }

  /**
   * 指定された状態・イベントシンボルに対応する遷移先を返す．ただし，状態が存在しない場合は例外を返す．
   *
   * @param stateId
   * @param eventSymbol
   * @return
   */
  @Nonnull
  public int getNextState(int stateId, String eventSymbol) {
    this.rangeCheckForGet(stateId);
    Map<String, Integer> transitions = this.tranFunc.get(stateId);
    if (transitions.containsKey(eventSymbol)) {
      return transitions.get(eventSymbol);
    } else if (transitions.containsKey(WILDCARD)) {
      return transitions.get(WILDCARD);
    } else {
      return transitions.get(REJECT);
    }
  }

  public static class Builder {
    private Set<Integer> states;
    private Map<Integer, Map<String, Integer>> tranFunc;
    private Set<Integer> finalStates;

    public Builder() {
    }

    public Builder subsetConstruction(Nfa nfa) {
      // 初期化
      this.states = new HashSet<>();
      this.states.add(INITIALSTATE);
      this.finalStates = new HashSet<>();
      this.tranFunc = new HashMap<>();

      // 部分集合から状態番号への写像
      Map<Set<Integer>, Integer> subset2Id = new HashMap<>();
      subset2Id.put(Set.of(INITIALSTATE), INITIALSTATE);

      // 到達可能な全ての部分集合を走査
      Set<Set<Integer>> unCheckedSubsets = new HashSet<>();
      unCheckedSubsets.add(Set.of(INITIALSTATE));
      while (!unCheckedSubsets.isEmpty()) {
        Set<Set<Integer>> currentSubsets = new HashSet<>(unCheckedSubsets);
        unCheckedSubsets.clear();
        currentSubsets.forEach(subset -> {
          // 各イベントシンボルで遷移可能な状態集合を列挙
          Map<String, Set<Integer>> transitions = new HashMap<>();
          subset.forEach(nfaId -> {
            nfa.getEventSymbols(nfaId).forEach(eventSymbol -> {
              transitions.putIfAbsent(eventSymbol, new HashSet<Integer>());
              transitions.get(eventSymbol).addAll(nfa.getNextStates(nfaId, eventSymbol));
            });
          });
          // ワイルドカードによる遷移が存在するならば，破棄による遷移と入れ替える
          if (transitions.containsKey(WILDCARD)) {
            Set<Integer> wildcardIds = transitions.get(WILDCARD);
            transitions.remove(WILDCARD);
            transitions.put(REJECT, wildcardIds);
          }
          // 列挙した部分集合をDFAに変換する
          transitions.forEach((eventSymbol, nextIds) -> {
            // 未知の部分集合があれば写像に加える
            if (!subset2Id.containsKey(nextIds)) {
              int newId = this.states.size();
              this.states.add(newId);
              subset2Id.put(nextIds, newId);
              unCheckedSubsets.add(nextIds);
            }
            int startId = subset2Id.get(subset), endId = subset2Id.get(nextIds);
            this.tranFunc.putIfAbsent(startId, new HashMap<>());
            this.tranFunc.get(startId).put(eventSymbol, endId);
          });
          // 部分集合に最終状態が含まれて入れば対応するDFAの状態を最終状態に
          if (subset.stream().anyMatch(stateId -> nfa.isFinalState(stateId))) {
            this.finalStates.add(subset2Id.get(subset));
          }
        });
      }

      return this;
    }

    /**
     * 穴埋めアルゴリズムに基づきDFAを最小化する．
     *
     * @return
     */
    public Builder minimize() {
      // Filling table を生成
      Map<Integer, Map<Integer, Boolean>> fillingTable = this.genFillingTable();

      // 更新前の状態を保持
      Set<Integer> tempStates = new HashSet<>(this.states);
      Map<Integer, Map<String, Integer>> tempTranFunc = new HashMap<>();
      this.tranFunc.forEach((startId, transitions) -> {
        tempTranFunc.putIfAbsent(startId, new HashMap<>());
        transitions.forEach((eventSymbol, endId) -> {
          tempTranFunc.get(startId).put(eventSymbol, endId);
        });
      });
      Set<Integer> tempFinalStates = new HashSet<>(this.finalStates);
      this.states.clear();
      this.tranFunc.clear();
      this.finalStates.clear();

      // 状態を更新
      Set<Set<Integer>> subsets = new HashSet<>();
      for (int i = 0; i < tempStates.size(); ++i) {
        Set<Integer> subset = new HashSet<>(Set.of(i));
        for (int j = i + 1; j < tempStates.size(); ++j) {
          if (!fillingTable.get(i).get(j)) {
            subset.add(j);
          }
        }
        subsets.add(subset);
      }
      Map<Set<Integer>, Integer> subset2Id = new HashMap<>();
      int count = 1;
      for (Set<Integer> subset : subsets) {
        if (subset.contains(INITIALSTATE)) {
          subset2Id.put(subset, INITIALSTATE);
        } else {
          subset2Id.put(subset, count++);
        }
        int newId = subset2Id.get(subset);
        this.states.add(newId);
        subset.forEach(stateId -> {
          if (tempFinalStates.contains(stateId)) {
            this.finalStates.add(newId);
          }
        });
      }

      // 遷移関数を更新
      for (Set<Integer> subset : subsets) {
        int startId = subset2Id.get(subset);
        this.tranFunc.putIfAbsent(startId, new HashMap<>());
        subset.forEach(stateId -> {
          tempTranFunc.get(stateId).forEach((eventSymbol, nextId) -> {
            subset2Id.forEach((keyIds, newNextId) -> {
              if (keyIds.contains(nextId)) {
                this.tranFunc.get(startId).put(eventSymbol, newNextId);
              }
            });
          });
        });
      }

      return this;
    }

    /**
     * 穴埋めアルゴリズムにより状態の等価性を示す filling table を作成する．
     * 
     * @return
     */
    private Map<Integer, Map<Integer, Boolean>> genFillingTable() {
      Map<Integer, Map<Integer, Boolean>> fillingTable = new HashMap<>();
      this.states.forEach(stateId -> fillingTable.put(stateId, new HashMap<>()));
      for (int i = 0; i < this.states.size(); ++i) {
        for (int j = 0; j < this.states.size(); ++j) {
          if ((this.finalStates.contains(i) && this.finalStates.contains(j))
              || (!this.finalStates.contains(i) && !this.finalStates.contains(j))) {
            fillingTable.get(i).put(j, false);
          } else {
            fillingTable.get(i).put(j, true);
          }
        }
      }

      // 収束するまで filling table を更新
      boolean modified = true;
      while (modified) {
        modified = false;
        for (int i = 0; i < this.states.size(); ++i) {
          for (int j = i + 1; j < this.states.size(); ++j) {
            if (fillingTable.get(i).get(j))
              continue;
            Set<String> eventSymbols = new HashSet<>();
            this.tranFunc.get(i).keySet().forEach(e -> eventSymbols.add(e));
            this.tranFunc.get(j).keySet().forEach(e -> eventSymbols.add(e));
            for (String eventSymbol : eventSymbols) {
              int nextIdI = this.getNextState(i, eventSymbol), //
                  nextIdJ = this.getNextState(j, eventSymbol);
              if (fillingTable.get(nextIdI).get(nextIdJ)) {
                fillingTable.get(i).put(j, true);
                fillingTable.get(j).put(i, true);
                modified |= true;
              }
            }
          }
        }
      }

      return fillingTable;
    }

    /**
     * 初期状態から遷移不可能な状態を削除する．
     *
     * @return
     */
    private Builder removeUnreachableState() {
      // 初期状態から到達可能な状態を列挙する
      Set<Integer> reachableIds = new HashSet<>();
      boolean modified = reachableIds.add(INITIALSTATE);
      while (modified) {
        modified = false;
        Set<Integer> currentIds = new HashSet<>(reachableIds);
        for (int currentId : currentIds) {
          for (int nextId : this.tranFunc.get(currentId).values()) {
            modified |= reachableIds.add(nextId);
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

    /**
     * DFA を構築する．
     *
     * @return
     */
    public Dfa build() {
      this.removeUnreachableState();
      return new Dfa(this);
    }

    @Nonnull
    private int getNextState(int stateId, String eventSymbol) {
      Map<String, Integer> transitions = this.tranFunc.get(stateId);
      if (transitions.containsKey(eventSymbol)) {
        return transitions.get(eventSymbol);
      } else if (transitions.containsKey(WILDCARD)) {
        return transitions.get(WILDCARD);
      } else {
        return transitions.get(REJECT);
      }
    }
  }
}