package dfa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class RDFA {
    private Set<String> stateSet; // 상태 집합
    private Set<Character> terminalSet; // 터미널 집합
    private Map<String, Map<Character, Set<String>>> deltaFunctions; // 델타 함수
    private String startState; // 시작 상태
    private Set<String> finalStateSet; // 최종 상태 집합

    public RDFA() {
        stateSet = null;
        terminalSet = null;
        deltaFunctions = null;
        startState = null;
        finalStateSet = null;
    }

    public Set<String> getStateSet() {
        return stateSet;
    }

    public void setStateSet(Set<String> stateSet) {
        this.stateSet = stateSet;
    }

    public Set<Character> getTerminalSet() {
        return terminalSet;
    }

    public void setTerminalSet(Set<Character> terminalSet) {
        this.terminalSet = terminalSet;
    }

    public Map<String, Map<Character, Set<String>>> getDeltaFunctions() {
        return deltaFunctions;
    }

    public void setDeltaFunctions(Map<String, Map<Character, Set<String>>> deltaFunctions) {
        this.deltaFunctions = deltaFunctions;
    }

    public String getStartState() {
        return startState;
    }

    public void setStartState(String startState) {
        this.startState = startState;
    }

    public Set<String> getFinalStateSet() {
        return finalStateSet;
    }

    public void setFinalStateSet(Set<String> finalStateSet) {
        this.finalStateSet = finalStateSet;
    }

    public void saveRDFAToFile() {
        try {
            FileWriter writer = new FileWriter("reduced_dfa.txt");

            // 상태 집합 출력
            writer.write("StateSet = {");
            boolean isFirstState = true;
            for (String state : stateSet) {
                if (!isFirstState) {
                    writer.write(", ");
                }
                writer.write(state);
                isFirstState = false;
            }
            writer.write("}\n\n");

            // 터미널 집합 출력
            writer.write("TerminalSet = {");
            boolean isFirstTerminal = true;
            for (Character terminal : terminalSet) {
                if (!isFirstTerminal) {
                    writer.write(", ");
                }
                writer.write(terminal.toString());
                isFirstTerminal = false;
            }
            writer.write("}\n\n");

            // 델타 함수 출력
            writer.write("DeltaFunctions = {\n");
            for (Map.Entry<String, Map<Character, Set<String>>> entry : deltaFunctions.entrySet()) {
                String currentState = entry.getKey();
                Map<Character, Set<String>> transitions = entry.getValue();
                for (Map.Entry<Character, Set<String>> transitionEntry : transitions.entrySet()) {
                    Character inputSymbol = transitionEntry.getKey();
                    Set<String> nextStateSet = transitionEntry.getValue();
                    writer.write("\t(" + currentState + ", " + inputSymbol + ") = {");
                    boolean isFirstNextState = true;
                    for (String nextState : nextStateSet) {
                        if (!isFirstNextState) {
                            writer.write(", ");
                        }
                        writer.write(nextState);
                        isFirstNextState = false;
                    }
                    writer.write("}\n");
                }
            }
            writer.write("}\n\n");

            // 시작 상태 출력
            writer.write("StartState = " + startState + "\n\n");

            // 최종 상태 집합 출력
            writer.write("FinalStateSet = {");
            boolean isFirstFinalState = true;
            for (String state : finalStateSet) {
                if (!isFirstFinalState) {
                    writer.write(", ");
                }
                writer.write(state);
                isFirstFinalState = false;
            }
            writer.write("}\n");

            writer.close();
            System.out.println("Reduced DFA 저장이 완료되었습니다.");

        } catch (IOException e) {
            System.out.println("Reduced DFA 저장 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
