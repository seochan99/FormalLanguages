package main;

import java.util.Scanner;

import dfa.DFAToRDFAConverter;
import dfa.ENFAToDFAConverter;
import dfa.RDFA;
import e_nfa.ENFA;
import e_nfa.ENFACreator;
import re.RE;
import dfa.DFA;



public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String re = "";

        String expandedRegex = "";

        boolean isValid = false;

        /*--------------------re 체크--------------------*/
        while (!isValid) {
            System.out.println("정규 표현식을 입력하세요");
            re = scanner.nextLine();  // 사용자로부터 정규 표현식을 입력받습니다.
            isValid = RE.validateRegex(re); // isValid Check from ValidDateRegex

            // 정규 표현식에 맞지 않을경우 다시 입력받음, 이를 위한 안내문 출력
            if (!isValid) {
                System.out.println("입력한 정규 표현식이 조건에 맞지 않습니다. 다시 입력해주세요.");
            }
        }
        /*--------------------Exapnd Regex --------------------*/
        expandedRegex = RE.getExpandedRegex(re);

        System.out.println("Re:" + re);
        System.out.println("Expand Re"+expandedRegex);

        /*--------------------Convert RE to ε-NFA --------------------*/
        // 정규 표현식을 ε-NFA로 변환합니다.
        ENFA enfa = ENFACreator.convertToENFA(expandedRegex);

        // 변환된 ε-NFA를 파일로 저장합니다.
        ENFACreator.saveENFAToFile(enfa);

        /*--------------------Convert ε-NFA to DFA --------------------*/
        // ENFA를 DFA로 변환합니다.
        DFA dfa = ENFAToDFAConverter.convertToDFA(enfa);

        // 변환된 DFA를 파일로 저장합니다.
        dfa.saveDFAToFile();

        /*--------------------Convert DFA To Reduced DFA --------------------*/
        // DFA를 Reduced DFA로 변환합니다.
        RDFA rdfa = DFAToRDFAConverter.convertToRDFA(dfa);

        // 변환된 Reduced DFA를 파일로 저장합니다.
         rdfa.saveRDFAToFile();




        scanner.close();
    }

}
