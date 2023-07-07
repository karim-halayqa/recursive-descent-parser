import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import static java.util.regex.Pattern.matches;

public class Main {

    static int tokenCount = 0;
    static List<Token> tokens = new ArrayList<>();

    static List<String> reservedWords = new ArrayList<>(List.of(new String[]{
            "project", "const", "var", "routine", "start", "end", "input", "output", "if", "then", "endif", "else",
            "statement", "do", "loop", "λ", "var"
    }));

    //all symbols that can be used in the program in a list
    static List<String> symbols = new ArrayList<>(List.of(new String[]{
            ";", ".", ":", ",", ":=", "=", "(", ")", "+", "-", "*", "/", "<", ">", "<=", ">=", "==", "!=", "&&", "||", "!"
    }));

    /**
     * read tokens from program.txt file
     */
    private static void readTokens() {
        try {
            //read the input.txt file using BufferedReader
            try (BufferedReader br = new BufferedReader(new FileReader("/Users/kareemhalayka/IdeaProjects/recursive-descent-parser/src/input.txt"))) {
                String line;
                int lineNumber = 1;
                while ((line = br.readLine()) != null) {
                    String[] lineTokens = line.split("\\s+");
                    for (String lineToken : lineTokens) {
                        // regex tht splits the token on every ocurance of a symbol -+*/()=;.,:{}<> in order to treat them as separate tokens
                        String[] subTokens = lineToken.split("(?<=[-+*/()=;.,:{}<>])|(?=[-+*/()=;.,:{}<>])");
                        for (int i = 0; i < subTokens.length; i++) {
                            String subToken = subTokens[i];
                            if (!subToken.isEmpty()) {
                                if(i+1 <= subTokens.length-1) {
                                    if (subToken.equals("<") && subTokens[i + 1].equals("=")) {
                                        subToken = "<=";
                                        i++;
                                    } else if (subToken.equals(">") && subTokens[i + 1].equals("=")) {
                                        subToken = ">=";
                                        i++;
                                    } else if (subToken.equals("<") && subTokens[i + 1].equals(">")) {
                                        subToken = "<>";
                                        i++;
                                    } else if (subToken.equals(":") && subTokens[i + 1].equals("=")) {
                                        subToken = ":=";
                                        i++;
                                    }
                                }
                                tokens.add(new Token(subToken, lineNumber));
                            }
                        }
                    }
                    lineNumber++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void projectDeclaration() {
        projectDef();
        if (tokenCount <= tokens.size() - 1) {
            if (tokens.get(tokenCount).getToken().equals(".")) {
                tokenCount++;
            } else {
                error("Error: missing '.' at token count " + tokenCount);
            }
        } else {
            error("Error: program ended before '.' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void projectDef() {
        projectHeading();
        declarations();
        compoundStmt();
    }

    private static void projectHeading() {
        //check if the given token is 'project' or not
        checkTokenCount(tokenCount, "project");
        if (tokens.get(tokenCount).getToken().equals("project")) {
            tokenCount++;
        } else {
            error("Error: missing 'project' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ";");
        if (tokens.get(tokenCount).getToken().equals(";")) {
            tokenCount++;
        } else {
            error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void declarations() {
        constDecl();
        varDecl();
        subroutineDecl();
    }

    private static void constDecl() {
        //check if the given token is 'const' or not
        checkTokenCount(tokenCount, "const");
        if (tokens.get(tokenCount).getToken().equals("const")) {
            tokenCount++;
        } else {
            return;
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            while (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {

                constItem();
                //check if the given token is ';' or not
                checkTokenCount(tokenCount, ";");
                if (tokens.get(tokenCount).getToken().equals(";")) {
                    tokenCount++;
                } else {
                    error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
                }
            }
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }

    }

    private static void constItem() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is '=' or not
        checkTokenCount(tokenCount, "=");
        if (tokens.get(tokenCount).getToken().equals("=")) {
            tokenCount++;
        } else {
            error("Error: missing '=' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is 'integer-value' or not
        checkTokenCount(tokenCount, "integer-value");
        if (tokens.get(tokenCount).getToken().matches("[0-9]+")) {
            tokenCount++;
        } else {
            error("Error: missing an integer token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void varDecl() {
        checkTokenCount(tokenCount, "var");
        if (tokens.get(tokenCount).getToken().equals("var")) {
            tokenCount++;
        } else {
            return;
        }
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            while (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {

                varItem();
                checkTokenCount(tokenCount, ";");
                if (tokens.get(tokenCount).getToken().equals(";")) {
                    tokenCount++;
                } else {
                    error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
                }
            }
        } else if (tokens.get(tokenCount).getToken().equals("routine")) {
            subroutineDecl();
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }

    }

    private static void varItem() {
        nameList();
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ":");
        if (tokens.get(tokenCount).getToken().equals(":")) {
            tokenCount++;
        } else {
            error("Error: missing ':' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, "int");
        if (tokens.get(tokenCount).getToken().equals("int")) {
            tokenCount++;
        } else {
            error("Error: missing 'int' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void nameList() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }

        while (tokens.get(tokenCount).getToken().equals(",")) {

            //check if the given token is ';' or not
            checkTokenCount(tokenCount, ",");
            if (tokens.get(tokenCount).getToken().equals(",")) {
                tokenCount++;
            } else {
                error("Error: missing ',' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
            }
            //check if the given token is 'name' or not
            checkTokenCount(tokenCount, "name");
            if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
                tokenCount++;
            } else {
                error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
            }
        }
    }

    private static void subroutineDecl() {
        //check if the given token is 'routine' or not
        checkTokenCount(tokenCount, "routine");
        if (tokens.get(tokenCount).getToken().equals("routine")) {
            subroutineHeading();
            declarations();
            compoundStmt();

            //check if the given token is ',' or not
            checkTokenCount(tokenCount, ";");
            if (tokens.get(tokenCount).getToken().equals(";")) {
                tokenCount++;
            } else {
                error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
            }
        }
    }

    private static void subroutineHeading() {
        //check if the given token is 'routine' or not
        checkTokenCount(tokenCount, "routine");
        if (tokens.get(tokenCount).getToken().equals("routine")) {
            tokenCount++;
        } else {
            error("Error: missing 'routine' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ";");
        if (tokens.get(tokenCount).getToken().equals(";")) {
            tokenCount++;
        } else {
            error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void compoundStmt() {
        //check if the given token is 'start' or not
        checkTokenCount(tokenCount, "start");
        if (tokens.get(tokenCount).getToken().equals("start")) {
            tokenCount++;
        } else {
            error("Error: missing 'start' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        stmtList();
        //check if the given token is 'end' or not
        checkTokenCount(tokenCount, "end");
        if (tokens.get(tokenCount).getToken().equals("end")) {
            tokenCount++;
        } else {
            error("Error: missing 'end' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void stmtList() {
        checkTokenCount(tokenCount, "token from statements list");
        String token = tokens.get(tokenCount).getToken();
        while (true) {
            checkTokenCount(tokenCount, "statement");
            //check if the next token is a statement first() token
            if ((token.matches(".*[^0-9].*") && !reservedWords.contains(token)) || token.equals("input") || token.equals("output")
                    || token.equals("if") || token.equals("loop")
                    || token.equals("start") || token.equals(";")
                    || token.equals("else")) {
                statement();
            } else {
                break;
            }
            //check if the given token is ';' or not
            checkTokenCount(tokenCount, ";");
            token = tokens.get(tokenCount).getToken();
            if (token.equals(";")) {
                tokenCount++;
                token = tokens.get(tokenCount).getToken();
            } else {
                error("Error: missing ';' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
            }
            token = tokens.get(tokenCount).getToken();
        }

    }

    private static void statement() {
        String token = tokens.get(tokenCount).getToken();
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (token.matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            assStmt();
        } else if (token.equals("input") || token.equals("output")) {
            inoutStmt();
        } else if (token.equals("if")) {
            ifStmt();
        } else if (token.equals("loop")) {
            loopStmt();
        } else if (token.equals("start")) {
            compoundStmt();
        } else if (token.equals(";")) {
            stmtList();
        } else if (token.equals("else")) {
            elsePart();
        } else {
            error("Error: missing statement token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void assStmt() {
        //check if the given token is 'name' or not
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is ':=' or not
        checkTokenCount(tokenCount, ":=");
        if (tokens.get(tokenCount).getToken().equals(":=")) {
            tokenCount++;
        } else {
            error("Error: missing ':=' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        arithExp();
    }

    private static void arithExp() {
        term();
        String token = tokens.get(tokenCount).getToken();
        while (token.equals("+") || token.equals("-")) {

            addSign();
            checkTokenCount(tokenCount, "term");
            term();
            token = tokens.get(tokenCount).getToken();
        }
    }

    private static void term() {
        factor();
        while (tokens.get(tokenCount).getToken().equals("*") || tokens.get(tokenCount).getToken().equals("/") || tokens.get(tokenCount).getToken().equals("%")) {
            mulSign();
            checkTokenCount(tokenCount, "factor");
            factor();
        }
    }

    private static void factor() {
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).getToken().equals("(")) {
            tokenCount++;

            arithExp();
            //check if the given token is ')' or not
            checkTokenCount(tokenCount, ")");
            if (tokens.get(tokenCount).getToken().equals(")")) {
                tokenCount++;
            } else {
                error("Error: missing ')' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
            }
        } else if ((tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) || tokens.get(tokenCount).getToken().matches("[0-9]+")) {
            nameValue();
        } else {
            error("Error: missing '(' or 'name' or 'integer_value' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void nameValue() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else if (tokens.get(tokenCount).getToken().matches("[0-9]+")) {
            tokenCount++;
        } else {
            error("Error: missing 'name' or 'integer_value' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void addSign() {
        //check if the given token is '+' or not
        checkTokenCount(tokenCount, "+ or -");
        if (tokens.get(tokenCount).getToken().equals("+")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).getToken().equals("-")) {
            tokenCount++;
        } else {
            error("Error: missing '+' or '-' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void mulSign() {
        //check if the given token is '*' or not
        checkTokenCount(tokenCount, "* or / or %");
        if (tokens.get(tokenCount).getToken().equals("*")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).getToken().equals("/")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).getToken().equals("%")) {
            tokenCount++;
        } else {
            error("Error: missing '*' or '/' or '%' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void inoutStmt() {
        //check if the given token is 'input' or not
        checkTokenCount(tokenCount, "input or output");
        if (tokens.get(tokenCount).getToken().equals("input")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).getToken().equals("output")) {
            tokenCount++;
        } else {
            error("Error: missing 'input' or 'output' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).getToken().equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).getToken().matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount).getToken())) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).getToken().equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }

    }

    private static void ifStmt() {
        //check if the given token is 'if' or not
        checkTokenCount(tokenCount, "if");
        if (tokens.get(tokenCount).getToken().equals("if")) {
            tokenCount++;
        } else {
            error("Error: missing 'if' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).getToken().equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        boolExp();
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).getToken().equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is 'then' or not
        checkTokenCount(tokenCount, "then");
        if (tokens.get(tokenCount).getToken().equals("then")) {
            tokenCount++;
        } else {
            error("Error: missing 'then' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        String token = tokens.get(tokenCount).getToken();
        statement();
        token = tokens.get(tokenCount).getToken();
        elsePart();
        //check if the given token is 'endif' or not
        checkTokenCount(tokenCount, "endif");
        if (tokens.get(tokenCount).getToken().equals("endif")) {
            tokenCount++;
        } else {
            error("Error: missing 'endif' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void elsePart() {
        //check if the given token is 'else' or 'endif' or not
        checkTokenCount(tokenCount, "else");
        String token = tokens.get(tokenCount).getToken();
        if (tokens.get(tokenCount).getToken().equals("else")) {
            tokenCount++;
        }
    }

    private static void loopStmt() {
        //check if the given token is 'loop' or not
        checkTokenCount(tokenCount, "loop");
        if (tokens.get(tokenCount).getToken().equals("loop")) {
            tokenCount++;
        } else {
            error("Error: missing 'loop' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).getToken().equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        boolExp();
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).getToken().equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        //check if the given token is 'do' or not
        checkTokenCount(tokenCount, "do");
        if (tokens.get(tokenCount).getToken().equals("do")) {
            tokenCount++;
        } else {
            error("Error: missing 'do' token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
        statement();
    }

    private static void boolExp() {
        nameValue();
        relationalOper();
        nameValue();
    }

    private static void relationalOper() {
        //check if the given token is relational operator or not
        checkTokenCount(tokenCount, "relational operator");
        String token = tokens.get(tokenCount).getToken();
        if (token.equals("=")) {
            tokenCount++;
        } else if (token.equals("<>")) {
            tokenCount++;
        } else if (token.equals("<")) {
            tokenCount++;
        } else if (token.equals("<=")) {
            tokenCount++;
        } else if (token.equals(">")) {
            tokenCount++;
        } else if (token.equals(">=")) {
            tokenCount++;
        } else {
            error("Error: missing relational operator token at line "+tokens.get(tokenCount).getLineNumber()+" at token count " + tokenCount);
        }
    }

    private static void error(String errorMsg) {
        //throw runtime exception with the given error message
        throw new RuntimeException(errorMsg);
    }

    private static void checkTokenCount(int tokenCount, String token) {
        if (!(tokenCount <= tokens.size() - 1)) {
            error("Error: program ended before token " + token + " at token count" + tokenCount);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        readTokens();
        projectDeclaration();
        System.out.println("Program is syntactically correct.");
    }

}