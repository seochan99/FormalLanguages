package dfa;

import e_nfa.ENFA;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DFA {
    private Set<String> stateSet; // 상태 집합
    private Set<Character> terminalSet; // 터미널 집합
    private Map<String, Map<Character, String>> deltaFunctions; // 델타 함수
    private String startState; // 시작 상태
    private Set<String> finalStateSet; // 최종 상태 집합

    public DFA() {
        stateSet = new HashSet<>();
        terminalSet = new HashSet<>();
        deltaFunctions = new HashMap<>();
        startState = "";
        finalStateSet = new HashSet<>();
    }

    public Set<String> getStateSet() {
        return stateSet;
    }

    public Set<Character> getTerminalSet() {
        return terminalSet;
    }

    public Map<String, Map<Character, String>> getDeltaFunctions() {
        return deltaFunctions;
    }

    public String getStartState() {
        return startState;
    }

    public Set<String> getFinalStateSet() {
        return finalStateSet;
    }

    public void setStateSet(Set<String> stateSet) {
        this.stateSet = stateSet;
    }

    public void setTerminalSet(Set<Character> terminalSet) {
        this.terminalSet = terminalSet;
    }

    public void setDeltaFunctions(Map<String, Map<Character, String>> deltaFunctions) {
        this.deltaFunctions = deltaFunctions;
    }

    public void setStartState(String startState) {
        this.startState = startState;
    }

    public void setFinalStateSet(Set<String> finalStateSet) {
        this.finalStateSet = finalStateSet;
    }

    /**
     * 상태를 상태 집합에 추가합니다.
     * @param state 추가할 상태
     */
    public void addState(String state) {
        stateSet.add(state);
    }

    /**
     * 터미널을 터미널 집합에 추가합니다.
     * @param terminal 추가할 터미널
     */
    public void addTerminal(Character terminal) {
        terminalSet.add(terminal);
    }

    /**
     * 델타 함수에 상태 전이를 추가합니다.
     * @param currentState 현재 상태
     * @param inputSymbol 입력 심볼
     * @param nextState 다음 상태
     */
    public void addDeltaFunction(String currentState, Character inputSymbol, String nextState) {
        if (!deltaFunctions.containsKey(currentState)) {
            deltaFunctions.put(currentState, new HashMap<>());
        }
        deltaFunctions.get(currentState).put(inputSymbol, nextState);
    }

    /**
     * 최종 상태를 최종 상태 집합에 추가합니다.
     * @param finalState 최종 상태
     */
    public void addFinalState(String finalState) {
        finalStateSet.add(finalState);
    }


    /**
     * DFA를 파일로 저장합니다.
     * 파일명: DFA_output.txt
     */
    public void saveDFAToFile() {
        try {
            FileWriter writer = new FileWriter("DFA_output.txt");

            // 상태 집합 출력
            writer.write("StateSet = { " + String.join(", ", stateSet) + " }\n");

            // 터미널 집합 출력
            writer.write("TerminalSet = { " + terminalSet.toString().replaceAll("[\\[\\]]", "") + " }\n");

            // 델타 함수 출력
            writer.write("DeltaFunctions = {\n");
            for (String state : stateSet) {
                Map<Character, String> transitions = deltaFunctions.getOrDefault(state, new HashMap<>());
                for (Map.Entry<Character, String> entry : transitions.entrySet()) {
                    char terminal = entry.getKey();
                    String nextState = entry.getValue();
                    writer.write("(" + state + ", " + terminal + ") = { " + nextState + " }\n");
                }
            }
            writer.write("}\n");

            // 시작 상태 출력
            writer.write("StartState = " + startState + "\n");

            // 최종 상태 집합 출력
            writer.write("FinalStateSet = { " + String.join(", ", finalStateSet) + " }\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
