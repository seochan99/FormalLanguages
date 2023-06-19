package dfa;

import e_nfa.ENFA;

import java.util.*;

public class ENFAToDFAConverter {

    public static DFA convertToDFA(ENFA enfa) {
        // DFA 객체 생성
        DFA dfa = new DFA();

        // 상태 집합 초기화
        Set<String> stateSet = new HashSet<>();
        stateSet.add(enfa.getInitialState());
        dfa.setStateSet(stateSet);

        // 터미널 집합 초기화
        dfa.setTerminalSet(enfa.getTerminalSet());

        // 시작 상태 설정
        dfa.setStartState(enfa.getInitialState());

        // 최종 상태 집합 초기화
        dfa.setFinalStateSet(enfa.getFinalStateSet());

        // 델타 함수 변환
        Map<String, Map<Character, String>> deltaFunctions = new HashMap<>();
        Queue<String> unmarkedStates = new LinkedList<>();
        unmarkedStates.offer(enfa.getInitialState());

        while (!unmarkedStates.isEmpty()) {
            String currentState = unmarkedStates.poll();
            Map<Character, Set<String>> transitions = new HashMap<>();

            // 입력 심볼마다 가능한 상태 집합 계산
            for (Character inputSymbol : enfa.getTerminalSet()) {
                Set<String> reachableStates = enfa.getReachableStates();
                transitions.put(inputSymbol, reachableStates);

                // 새로운 상태 집합인지 확인하여 unmarkedStates에 추가
                if (!stateSet.containsAll(reachableStates)) {
                    unmarkedStates.addAll(reachableStates);
                }
            }

            // 상태 집합 추가
            for (Set<String> nextStateSet : transitions.values()) {
                stateSet.addAll(nextStateSet);
            }

            // 델타 함수 추가
            Map<Character, String> deltaFunction = new HashMap<>();
            for (Map.Entry<Character, Set<String>> entry : transitions.entrySet()) {
                Character inputSymbol = entry.getKey();
                Set<String> reachableStates = entry.getValue();
                String nextState = String.join(",", reachableStates);
                deltaFunction.put(inputSymbol, nextState);
            }
            deltaFunctions.put(currentState, deltaFunction);
        }

        // 델타 함수 설정
        dfa.setDeltaFunctions(deltaFunctions);

        return dfa;
    }
}
