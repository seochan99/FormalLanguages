import java.io.*;

public class Scanner { // Scanner 클래스

    // 토큰 종류
    private boolean isEof = false;              // 파일의 끝인지 체크
    private char ch = ' ';                      // 현재 문자
    private BufferedReader input;               // 입력 스트림
    private String line = "";                   // 입력에서 읽은 한 줄
    private int lineno = 0;                     // 현재 행 번호
    private int col = 1;                        // 현재 열 번호
    private final String letters = "abcdefghijklmnopqrstuvwxyz" // 문자
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "0123456789"; // 숫자
    private final char eolnCh = '\n'; // 개행 문자
    private final char eofCh = '\004'; // 파일의 끝을 나타내는 문자

    // ERROR
    private boolean isError = false;





    // 스캐너 생성자
    public Scanner(String fileName) { // 소스 파일 이름

        // 파일 이름 출력
        System.out.println("Begin scanning... programs/" + fileName + "\n");

        // 파일 열기
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            // 파일이 없을 경우 오류 출력
            System.out.println("File not found: " + fileName);
            // 프로그램 종료
            System.exit(1);
        }
    }


    private char nextChar() { // 다음 문자 반환
        // 파일의 끝이면
        if (ch == eofCh){
            // 오류 출력
            error("Attempt to read past end of file");
            System.exit(1);
        }

        // 열 번호 증가
        col++;
        // 열 번호가 줄의 길이보다 크면
        if (col >= line.length()) {
            try {
                // 다음 줄 읽기
                line = input.readLine();
            } catch (IOException e) {
                // 오류 출력
                System.err.println(e);
                System.exit(1);
            }
            // 다음 줄이 없으면
            if (line == null) // 파일의 끝
                line = "" + eofCh;
            else {
                // System.out.println(lineno + ":\t" + line);
                // 줄 번호 증가
                lineno++;
                line += eolnCh;
            } // if line
            // 열 번호 초기화
            col = 0;
        } // if col
        // 다음 문자 반환
        return line.charAt(col);
    }


    // next를 통해서 다음 토큰을 읽어온다.
    public Token next() {
        do {
            // 공백 문자 건너뛰기
            if (isLetter(ch) || ch == '_') { // 식별자 또는 키워드
                // 식별자 또는 키워드 읽기
                String spelling = concat(letters + digits + '_');
                // 키워드인지 검사
                return Token.keyword(spelling,lineno, col);
            }else if(ch =='.'){
                String number = concat(digits);
                return Token.mkDoubleLiteral(number,lineno,col);
            }else if (isDigit(ch)) { // 정수 리터럴
                // 정수 리터럴 읽기
                String number = concat(digits);
                // 실수 리터럴인지 검사
                // 실수이 아니라면
                if(ch != '.'){
                    return Token.mkIntLiteral(number,lineno,col);
                }
                // 실수 리터럴 이라면
                number += concat(digits);
                // 실수 리터럴 반환
                return Token.mkDoubleLiteral(number,lineno,col);
            } else
                // switch 문을 사용하여 토큰을 구분
                switch (ch) {
                // ' ', \t 또는 \r 문자를 건너뛰기
                    case ' ':
                    case '\t':
                    case '\r':
                        // 다음 문자 읽기
                    case eolnCh:
                        ch = nextChar();
                        break;

                    case '/': // 나누기, 할당 또는 주석
                        ch = nextChar();
                        if (ch == '=') { // divAssign: /= 연산자
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.divAssignTok, lineno, col); // 나누기 후 할당하는 토큰 반환
                        }

                        // 주석이 아니면 나누기 연산자
                        if (ch != '*' && ch != '/')
                            return Token.mkDefaultToken(Token.divideTok, lineno, col); // 일반 나누기 토큰 생성 및 반환

                        // 다중라인 주석 처리
                        if (ch == '*') { // 다중라인 주석 시작
                            ch = nextChar();

                            // "/* */" 블록 주석 읽기
                            if (ch == '*') { // "/**" 주석
                                String comment = "";
                                do {
                                    while (ch != '*') { // '*' 문자가 나올 때까지 주석 내용을 읽음
                                        ch = nextChar();
                                        if (ch == eolnCh) { // 개행 문자면 '\n' 추가
                                            comment += '\n';
                                        } else {
                                            comment += ch; // 주석 내용 추가
                                        }
                                    }
                                    ch = nextChar(); // '*' 문자 읽기
                                    comment += (ch == eolnCh ? '\n' : ""); // 개행 문자면 '\n' 추가
                                } while (ch != '/'); // '/' 문자를 만날 때까지 주석 내용 읽기
                                ch = nextChar(); // '/' 문자 읽기
                                return Token.mkDocumentToken(comment.substring(0, comment.length() - 1), lineno, col); // 문서 토큰 생성 및 반환
                            } else { // 다중라인 주석 내용 무시
                                do {
                                    while (ch != '*')
                                        ch = nextChar();
                                    ch = nextChar();
                                } while (ch != '/'); // 주석 블록 끝을 만날 때까지 '*' 문자 읽기
                                ch = nextChar(); // '/' 문자 읽기
                            }
                        }

                        // "//" 한 줄 주석 처리
                        else if (ch == '/') { // 한 줄 주석 시작
                            ch = nextChar();
                            if (ch == '/') { // "//" 주석
                                String comment = "";
                                do {
                                    ch = nextChar();
                                    comment += ch; // 주석 내용 추가
                                } while (ch != eolnCh); // 개행 문자를 만날 때까지 주석 내용 읽기
                                ch = nextChar(); // 개행 문자 읽기
                                return Token.mkDocumentToken(comment, lineno, col); // 문서 토큰 생성 및 반환
                            } else { // "//" 다음 문자부터 개행 문자를 만날 때까지 무시
                                do {
                                    ch = nextChar();
                                } while (ch != eolnCh); // 개행 문자를 만날 때까지 문자 읽기
                                ch = nextChar(); // 개행 문자 읽기
                            }
                        }
                        break;

                    // 문자 리터럴, '인 경우
                    case '\'':  // 문자 리터럴
                        char ch1 = nextChar();
                        nextChar(); // '
                        ch = nextChar();
                        return Token.mkCharLiteral("" + ch1,lineno,col);

                        // 문자열 리터, " 인 경우
                    case '\"':
                        String str = "";
                        ch = nextChar();
                        // 다음 "가 나올 때까지 문자열을 읽는다.
                        while (ch != '\"') {
                            // 탈출 시퀸스를 만나면
                            if (ch == '\\') {
                                // 탈출 시퀸스를 문자열에 추가
                                str += ch;
                                // 다음 문자 읽기
                                ch = nextChar();
                                // 문자열에 추가
                                str += ch;
                                // 다음 문자 읽기
                                ch = nextChar();
                            }
                            // str에 문자 추가
                            str += StrConcat();
                        }
                        ch = nextChar();
                        // 문자열 리터럴 반환
                        return Token.mkStringLiteral(str, lineno, col);




                    // eofCh 문자를 만나면(End Of File)
                    case eofCh:
                        // 파일의 끝
                        return Token.eofTok;
                    case '+': // 더하기, 증가 또는 할당
                        ch = nextChar();
                        // +=
                        if (ch == '=') { // +=
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.addAssignTok, lineno, col);
                        } else if (ch == '+') { // ++
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.incrementTok, lineno, col);
                        }
                        // 그냥 "+"
                        return Token.mkDefaultToken(Token.plusTok, lineno, col);

                    case '-':
                        ch = nextChar();
                        if (ch == '=') { // -=
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.subAssignTok, lineno, col);
                        } else if (ch == '-') { // --
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.decrementTok, lineno, col);
                        }
                        return Token.mkDefaultToken(Token.minusTok, lineno, col);

                    case '*':
                        ch = nextChar();
                        if (ch == '=') { // multAssign
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.multAssignTok, lineno, col);
                        }
                        return Token.mkDefaultToken(Token.multiplyTok, lineno, col);

                    case '%':
                        ch = nextChar();
                        if (ch == '=') { // remAssign
                            ch = nextChar();
                            return Token.mkDefaultToken(Token.remAssignTok, lineno, col);
                        }
                        return Token.mkDefaultToken(Token.reminderTok, lineno, col);

                    case '(':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.leftParenTok, lineno, col);

                    case ')':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.rightParenTok, lineno, col);

                    case '{':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.leftBraceTok, lineno, col);

                    case '}':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.rightBraceTok, lineno, col);

                    // 추가 1 : 대괄호
                    case '[':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.leftBracketTok, lineno, col);
                    case ']':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.rightBracketTok, lineno, col);
                    // 추가 2 : 콜론
                    case ':':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.colonTok, lineno, col);
                    case ';':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.semicolonTok, lineno, col);
                    case ',':
                        ch = nextChar();
                        return Token.mkDefaultToken(Token.commaTok, lineno, col);
                    case '&':
                        check('&');
                        return Token.mkDefaultToken(Token.andTok, lineno, col);
                    case '|':
                        check('|');
                        return Token.mkDefaultToken(Token.orTok, lineno, col);
                    case '=':
                        return chkOpt('=', Token.mkDefaultToken(Token.assignTok, lineno, col),
                                Token.mkDefaultToken(Token.eqeqTok, lineno, col));
                    case '<':
                        return chkOpt('=', Token.mkDefaultToken(Token.ltTok, lineno, col),
                                Token.mkDefaultToken(Token.lteqTok, lineno, col));
                    case '>':
                        return chkOpt('=', Token.mkDefaultToken(Token.gtTok, lineno, col),
                                Token.mkDefaultToken(Token.gteqTok, lineno, col));
                    case '!':
                        return chkOpt('=', Token.mkDefaultToken(Token.notTok, lineno, col),
                                Token.mkDefaultToken(Token.noteqTok, lineno, col));
                    default:
                        error("Illegal character " + ch);
                } // switch
        } while (true);
    } // next


    // 문자열이라면 true를 반환
    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    // 숫자라면 true를 반환
    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    // 문자열을 읽어온다.
    private void check(char c) {
        ch = nextChar();
        // ch가 c가 아니라면 에러
        // c는 토큰의 종류를 나타낸다.
        // ch는 실제로 읽어온 문자이다.
        if (ch != c)
            error("Illegal character, expecting " + c);
        // ch가 c라면 다음 문자를 읽어온다.
        ch = nextChar();
    }

    // chkOpt 메소드는 두 문자 토큰을 구분한다.
    private Token chkOpt(char c, Token one, Token two) {
        ch = nextChar();
        if (ch != c)
            return one;
        ch = nextChar();
        // ch가 c라면 다음 문자를 읽어온다.
        return two;
    }

    // 문자열을 읽어온다.
    // concat을 통해서 문자열의 집합들을 읽어와 문자열을 만든다.
    private String concat(String set) {
        // r 문자열 할당
        String r = "";
        do {
            // r에 ch를 추가
            r += ch;
            ch = nextChar();
        } while (set.indexOf(ch) >= 0); // index 0 이상이면 문자열이 존재한다는 의미
        // r을 반환
        return r;
    }

    private String StrConcat() {
        // r 문자열 할당
        String str = "";
        do {
            str += ch;
            ch = nextChar();
        } while (ch != '\"' && ch != '\\');
        return str;
    }
    // 에러 메시지 출력
    public void error(String msg) {
        System.err.print(line);
        System.err.println("Error: column " + col + " " + msg);
        System.exit(1);
    }

    public boolean checkError() {
        boolean temp = isError;
        isError = false;

        return temp;
    }

    // 메인 메소드
    static public void main(String[] argv) {
        Scanner lexer = new Scanner(argv[0]);
        Token tok = lexer.next();
        while (tok != Token.eofTok) {
            if (!lexer.checkError()) {
                System.out.println(tok.toString(argv[0]));
            }
            tok = lexer.next();
        }
    } // main
}
