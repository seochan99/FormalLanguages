package dfa;

import java.util.*;

public class DFAToRDFAConverter {

    public static RDFA convertToRDFA(DFA dfa) {
        // RDFA 객체 생성
        RDFA rdfa = new RDFA();

        // 상태 집합 초기화
        Set<String> stateSet = dfa.getStateSet();
        rdfa.setStateSet(stateSet);

        // 터미널 집합 초기화
        Set<Character> terminalSet = dfa.getTerminalSet();
        rdfa.setTerminalSet(terminalSet);

        // 시작 상태 설정
        String startState = dfa.getStartState();
        rdfa.setStartState(startState);

        // 최종 상태 집합 초기화
        Set<String> finalStateSet = dfa.getFinalStateSet();
        rdfa.setFinalStateSet(finalStateSet);

        // 델타 함수 변환
        Map<String, Map<Character, Set<String>>> deltaFunctions = new HashMap<>();
        Map<String, Map<Character, String>> dfaDeltaFunctions = dfa.getDeltaFunctions();

        for (Map.Entry<String, Map<Character, String>> entry : dfaDeltaFunctions.entrySet()) {
            String currentState = entry.getKey();
            Map<Character, String> transitions = entry.getValue();
            Map<Character, Set<String>> rdfaTransitions = new HashMap<>();

            for (Map.Entry<Character, String> transitionEntry : transitions.entrySet()) {
                Character inputSymbol = transitionEntry.getKey();
                String nextState = transitionEntry.getValue();
                Set<String> nextStateSet = deltaFunctions.getOrDefault(nextState, new HashMap<>()).get(inputSymbol);

                if (nextStateSet == null) {
                    nextStateSet = new HashSet<>();
                }

                nextStateSet.add(nextState);
                rdfaTransitions.put(inputSymbol, nextStateSet);
            }

            deltaFunctions.put(currentState, rdfaTransitions);
        }

        // 델타 함수 설정
        rdfa.setDeltaFunctions(deltaFunctions);

        return rdfa;
    }
}
