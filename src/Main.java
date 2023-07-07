import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import static java.util.regex.Pattern.matches;

public class Main {

    static int tokenCount = 0;
    static List<String> tokens = new ArrayList<>();

    static List<String> reservedWords = new ArrayList<>(List.of(new String[]{
            "project", "const", "var", "routine", "start", "end", "input", "output","if", "then", "endif", "else",
            "statement", "do", "loop", "λ", "var"
    }));


    /**
     * read tokens from program.txt file
     */
    private static void readTokens() {
        //read file using scanner
        File file = new File("/Users/kareemhalayka/IdeaProjects/COMP439Proj/src/input.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                tokens.add(scanner.next());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void projectDeclaration() {
        projectDef();
        if (tokenCount <= tokens.size() - 1) {
            if (tokens.get(tokenCount).equals(".")) {
                tokenCount++;
            } else {
                error("Error: missing '.' at token count " + tokenCount);
            }
        } else {
            error("Error: program ended before '.' token at token count" + tokenCount);
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
        if (tokens.get(tokenCount).equals("project")) {
            tokenCount++;
        } else {
            error("Error: missing 'project' token at token count" + tokenCount);
        }
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ";");
        if (tokens.get(tokenCount).equals(";")) {
            tokenCount++;
        } else {
            error("Error: missing ';' token at token count" + tokenCount);
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
        if (tokens.get(tokenCount).equals("const")) {
            tokenCount++;
        }  else {
            return;
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            while (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {

                constItem();
                //check if the given token is ';' or not
                checkTokenCount(tokenCount, ";");
                if (tokens.get(tokenCount).equals(";")) {
                    tokenCount++;
                } else {
                    error("Error: missing ';' token at token count" + tokenCount);
                }
            }
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }

    }

    private static void constItem() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }
        //check if the given token is '=' or not
        checkTokenCount(tokenCount, "=");
        if (tokens.get(tokenCount).equals("=")) {
            tokenCount++;
        } else {
            error("Error: missing '=' token at token count" + tokenCount);
        }
        //check if the given token is 'integer-value' or not
        checkTokenCount(tokenCount, "integer-value");
        if (tokens.get(tokenCount).matches("[0-9]+")) {
            tokenCount++;
        } else {
            error("Error: missing an integer token at token count" + tokenCount);
        }
    }

    private static void varDecl() {
        checkTokenCount(tokenCount, "var");
        if (tokens.get(tokenCount).equals("var")) {
            tokenCount++;
        } else {
            return;
        }
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            while (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {

                varItem();
                checkTokenCount(tokenCount, ";");
                if (tokens.get(tokenCount).equals(";")) {
                    tokenCount++;
                } else {
                    error("Error: missing ';' token at token count" + tokenCount);
                }
            }
        } else if(tokens.get(tokenCount).equals("routine")) {
            subroutineDecl();
        } else{
            error("Error: missing 'name' token at token count" + tokenCount);
        }

    }

    private static void varItem() {
        nameList();
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ":");
        if (tokens.get(tokenCount).equals(":")) {
            tokenCount++;
        } else {
            error("Error: missing ':' token at token count" + tokenCount);
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, "int");
        if (tokens.get(tokenCount).equals("int")) {
            tokenCount++;
        } else {
            error("Error: missing 'int' token at token count" + tokenCount);
        }
    }

    private static void nameList() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }

        while (tokens.get(tokenCount).equals(",")) {

            //check if the given token is ';' or not
            checkTokenCount(tokenCount, ",");
            if (tokens.get(tokenCount).equals(",")) {
                tokenCount++;
            } else {
                error("Error: missing ',' token at token count" + tokenCount);
            }
            //check if the given token is 'name' or not
            checkTokenCount(tokenCount, "name");
            if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
                tokenCount++;
            } else {
                error("Error: missing 'name' token at token count" + tokenCount);
            }
        }
    }

    private static void subroutineDecl() {
        //check if the given token is 'routine' or not
        checkTokenCount(tokenCount, "routine");
        if (tokens.get(tokenCount).equals("routine")) {
            subroutineHeading();
            declarations();
            compoundStmt();

            //check if the given token is ',' or not
            checkTokenCount(tokenCount, ";");
            if (tokens.get(tokenCount).equals(";")) {
                tokenCount++;
            } else {
                error("Error: missing ';' token at token count" + tokenCount);
            }
        }
    }

    private static void subroutineHeading() {
        //check if the given token is 'routine' or not
        checkTokenCount(tokenCount, "routine");
        if (tokens.get(tokenCount).equals("routine")) {
            tokenCount++;
        } else {
            error("Error: missing 'routine' token at token count" + tokenCount);
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }
        //check if the given token is ';' or not
        checkTokenCount(tokenCount, ";");
        if (tokens.get(tokenCount).equals(";")) {
            tokenCount++;
        } else {
            error("Error: missing ';' token at token count" + tokenCount);
        }
    }

    private static void compoundStmt() {
        //check if the given token is 'start' or not
        checkTokenCount(tokenCount, "start");
        if (tokens.get(tokenCount).equals("start")) {
            tokenCount++;
        } else {
            error("Error: missing 'start' token at token count" + tokenCount);
        }
        stmtList();
        //check if the given token is 'end' or not
        checkTokenCount(tokenCount, "end");
        if (tokens.get(tokenCount).equals("end")) {
            tokenCount++;
        } else {
            error("Error: missing 'end' token at token count" + tokenCount);
        }
    }

    private static void stmtList() {
        checkTokenCount(tokenCount, "token from statements list");
        String token = tokens.get(tokenCount);
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
            token = tokens.get(tokenCount);
            if (token.equals(";")) {
                tokenCount++;
                token = tokens.get(tokenCount);
            } else {
                error("Error: missing ';' token at token count" + tokenCount);
            }
            token = tokens.get(tokenCount);
        }

    }

    private static void statement() {
        String token = tokens.get(tokenCount);
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (token.matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
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
            error("Error: missing statement token at token count" + tokenCount);
        }
    }

    private static void assStmt() {
        //check if the given token is 'name' or not
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }
        //check if the given token is ':=' or not
        checkTokenCount(tokenCount, ":=");
        if (tokens.get(tokenCount).equals(":=")) {
            tokenCount++;
        } else {
            error("Error: missing ':=' token at token count" + tokenCount);
        }
        arithExp();
    }

    private static void arithExp() {
        term();
        String token = tokens.get(tokenCount);
        while (token.equals("+") || token.equals("-")) {

            addSign();
            checkTokenCount(tokenCount, "term");
            term();
            token = tokens.get(tokenCount);
        }
    }

    private static void term() {
        factor();
        while (tokens.get(tokenCount).equals("*") || tokens.get(tokenCount).equals("/") || tokens.get(tokenCount).equals("%")) {
            mulSign();
            checkTokenCount(tokenCount, "factor");
            factor();
        }
    }

    private static void factor() {
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).equals("(")) {
            tokenCount++;

            arithExp();
            //check if the given token is ')' or not
            checkTokenCount(tokenCount, ")");
            if (tokens.get(tokenCount).equals(")")) {
                tokenCount++;
            } else {
                error("Error: missing ')' token at token count" + tokenCount);
            }
        } else if ((tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) || tokens.get(tokenCount).matches("[0-9]+")) {
            nameValue();
        } else {
            error("Error: missing '(' or 'name' or 'integer_value' token at token count" + tokenCount);
        }
    }

    private static void nameValue() {
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else if(tokens.get(tokenCount).matches("[0-9]+")){
            tokenCount++;
        } else {
            error("Error: missing 'name' or 'integer_value' token at token count" + tokenCount);
        }
    }

    private static void addSign() {
        //check if the given token is '+' or not
        checkTokenCount(tokenCount, "+ or -");
        if (tokens.get(tokenCount).equals("+")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).equals("-")) {
            tokenCount++;
        } else {
            error("Error: missing '+' or '-' token at token count" + tokenCount);
        }
    }

    private static void mulSign() {
        //check if the given token is '*' or not
        checkTokenCount(tokenCount, "* or / or %");
        if (tokens.get(tokenCount).equals("*")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).equals("/")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).equals("%")) {
            tokenCount++;
        } else {
            error("Error: missing '*' or '/' or '%' token at token count" + tokenCount);
        }
    }

    private static void inoutStmt() {
        //check if the given token is 'input' or not
        checkTokenCount(tokenCount, "input or output");
        if (tokens.get(tokenCount).equals("input")) {
            tokenCount++;
        } else if (tokens.get(tokenCount).equals("output")) {
            tokenCount++;
        } else {
            error("Error: missing 'input' or 'output' token at token count" + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at token count" + tokenCount);
        }
        //check if the given token is 'name' or not
        checkTokenCount(tokenCount, "name");
        if (tokens.get(tokenCount).matches(".*[^0-9].*") && !reservedWords.contains(tokens.get(tokenCount))) {
            tokenCount++;
        } else {
            error("Error: missing 'name' token at token count" + tokenCount);
        }
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at token count" + tokenCount);
        }

    }

    private static void ifStmt() {
        //check if the given token is 'if' or not
        checkTokenCount(tokenCount, "if");
        if (tokens.get(tokenCount).equals("if")) {
            tokenCount++;
        } else {
            error("Error: missing 'if' token at token count" + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at token count" + tokenCount);
        }
        boolExp();
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at token count" + tokenCount);
        }
        //check if the given token is 'then' or not
        checkTokenCount(tokenCount, "then");
        if (tokens.get(tokenCount).equals("then")) {
            tokenCount++;
        } else {
            error("Error: missing 'then' token at token count" + tokenCount);
        }
        String token = tokens.get(tokenCount);
        statement();
        token = tokens.get(tokenCount);
        elsePart();
        //check if the given token is 'endif' or not
        checkTokenCount(tokenCount, "endif");
        if (tokens.get(tokenCount).equals("endif")) {
            tokenCount++;
        } else {
            error("Error: missing 'endif' token at token count" + tokenCount);
        }
    }

    private static void elsePart() {
        //check if the given token is 'else' or 'endif' or not
        checkTokenCount(tokenCount, "else");
        String token = tokens.get(tokenCount);
        if (tokens.get(tokenCount).equals("else")) {
            tokenCount++;
        }
    }

    private static void loopStmt() {
        //check if the given token is 'loop' or not
        checkTokenCount(tokenCount, "loop");
        if (tokens.get(tokenCount).equals("loop")) {
            tokenCount++;
        } else {
            error("Error: missing 'loop' token at token count" + tokenCount);
        }
        //check if the given token is '(' or not
        checkTokenCount(tokenCount, "(");
        if (tokens.get(tokenCount).equals("(")) {
            tokenCount++;
        } else {
            error("Error: missing '(' token at token count" + tokenCount);
        }
        boolExp();
        //check if the given token is ')' or not
        checkTokenCount(tokenCount, ")");
        if (tokens.get(tokenCount).equals(")")) {
            tokenCount++;
        } else {
            error("Error: missing ')' token at token count" + tokenCount);
        }
        //check if the given token is 'do' or not
        checkTokenCount(tokenCount, "do");
        if (tokens.get(tokenCount).equals("do")) {
            tokenCount++;
        } else {
            error("Error: missing 'do' token at token count" + tokenCount);
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
        String token = tokens.get(tokenCount);
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
            error("Error: missing relational operator token at token count" + tokenCount);
        }
    }

    private static void error(String errorMsg) {
        //throw runtime exception with the given error message
        throw new RuntimeException(errorMsg);
    }

    private static void checkTokenCount(int tokenCount, String token) {
        if (!(tokenCount <= tokens.size() - 1)) {
            error("Error: program ended before " + token + " token at token count" + tokenCount);
        }
    }

    public static void main(String[] args) {
        readTokens();
        projectDeclaration();
        System.out.println("Program is syntactically correct.");
//        String input = "project project;\nconst\n  len=100;\nvar\n  total:int;\n  \nroutine compute;\n  \n  var\n    num:int;\nstart\n  input(x);\n  num:=n+10;\n  output(num);\nend;\n  \nstart\n  input(i);\n  if (i< 100) then\n    j:=5\n  endif;\n  loop (i<> min) do\n    start\n      i := i+1;\n      outputt(i);\n      total :=total+i;\n    end;\n  output(total);\nend.";
//        StringTokenizer tokenizer = new StringTokenizer(input, " ;:=<>()\n.", true);
//        while (tokenizer.hasMoreTokens()) {
//            String token = tokenizer.nextToken();
//            if(!(token.equals(" ") || token.equals("\n"))) {
//                System.out.println(token);
//            }
//        }
    }

}