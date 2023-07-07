import javax.sound.sampled.Line;

public class Token {
    private String token;
    private int lineNumber;

    public Token(String token, int lineNumber) {
        this.token = token;
        this.lineNumber = lineNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return token+ " at line " + lineNumber;
    }
}
