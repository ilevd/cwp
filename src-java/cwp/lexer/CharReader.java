package cwp.lexer;

public class CharReader {

    static private final char NEW_LINE = '\n';

    public String s;
    public int i = 0;
    public int lineNumber = 1;
    public int columnNumber = 1;

    public CharReader(String s) {
        this.s = s;
    }

    public String subs(int start) {
        return subs(start, i);
    }

    public String subs(int start, int end) {
        return s.substring(start, end);
    }

    public int read1() {
        int c = -1;
        if (i < s.length()) {
            c = s.charAt(i);
        }
        if (c == NEW_LINE) {
            lineNumber++;
            columnNumber = 1;
        } else {
            columnNumber++;
        }
        i++;
        // System.out.println("IdentReader: " + (char) c + "," + i);
        return c;
    }

    public int at(int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return -1;
        }
    }

    public int cur() {
        return at(i);
    }

    public int next() {
        return at(i + 1);
    }

    public int nnext() {
        return at(i + 2);
    }

    public void unread1(int ch) {
        i--;
        if (i < s.length() && s.charAt(i) == NEW_LINE) {
            columnNumber = 0;
            lineNumber--;
        } else {
            columnNumber--;
        }
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

}