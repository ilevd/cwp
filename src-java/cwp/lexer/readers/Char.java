package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.Token;
import cwp.lexer.CharReader;

public class Char {

    static private int readUnicodeChar(String token, int offset, int length, int base) {
        if (token.length() != offset + length)
            throw new IllegalArgumentException("Invalid unicode character: \\" + token);
        int uc = 0;
        for (int i = offset; i < offset + length; ++i) {
            int d = Character.digit(token.charAt(i), base);
            if (d == -1)
                throw new IllegalArgumentException("Invalid digit: " + token.charAt(i));
            uc = uc * base + d;
        }
        return (char) uc;
    }

    public static Token read(CharReader r, int line, int column) {
        int ch = r.read1();
        if (ch == -1)
            throw Util.runtimeException("EOF while reading character");
        String token = Identifier.read(r, (char) ch);  //readToken(r, (char) ch);
        if (token.length() == 1) {
            // return Character.valueOf(token.charAt(0))
            return new Token(Token.Type.CHAR, "\\" + token, null, line, column);
        } else if (token.equals("newline")) {
            //return '\n';
            return new Token(Token.Type.CHAR, "\\newline", null, line, column);
        } else if (token.equals("space"))
            //return ' ';
            return new Token(Token.Type.CHAR, "\\space", null, line, column);
        else if (token.equals("tab"))
            //return '\t';
            return new Token(Token.Type.CHAR, "\\t", null, line, column);
        else if (token.equals("backspace"))
            //  return '\b';
            return new Token(Token.Type.CHAR, "\\b", null, line, column);
        else if (token.equals("formfeed"))
            // return '\f';
            return new Token(Token.Type.CHAR, "\\f", null, line, column);
        else if (token.equals("return"))
            // return '\r';
            return new Token(Token.Type.CHAR, "\\r", null, line, column);
        else if (token.startsWith("u")) {
            char c = (char) readUnicodeChar(token, 1, 4, 16);
            if (c >= '\uD800' && c <= '\uDFFF') // surrogate code unit?
                throw Util.runtimeException("Invalid character constant: \\u" + Integer.toString(c, 16));
            //return c;
            return new Token(Token.Type.CHAR, "\\" + token, null, line, column);
        } else if (token.startsWith("o")) {
            int len = token.length() - 1;
            if (len > 3)
                throw Util.runtimeException("Invalid octal escape sequence length: " + len);
            int uc = readUnicodeChar(token, 1, len, 8);
            if (uc > 0377)
                throw Util.runtimeException("Octal escape sequence must be in range [0, 377].");
            //return (char) uc;
            return new Token(Token.Type.CHAR, "\\" + token, null, line, column);
        }
        throw Util.runtimeException("Unsupported character: \\" + token);
    }


}
