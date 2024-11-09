package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.Common;
import cwp.lexer.CharReader;
import cwp.lexer.Token;

import java.util.regex.Pattern;

public class Str {

    static private int readUnicodeChar(CharReader r, int initch, int base, int length, boolean exact) {
        int uc = Character.digit(initch, base);
        if (uc == -1)
            throw new IllegalArgumentException("Invalid digit: " + (char) initch);
        int i = 1;
        for (; i < length; ++i) {
            int ch = r.read1();
            if (ch == -1 || Common.isWhitespace(ch) || Common.isMacro(ch)) {
                r.unread1(ch);
                break;
            }
            int d = Character.digit(ch, base);
            if (d == -1)
                throw new IllegalArgumentException("Invalid digit: " + (char) ch);
            uc = uc * base + d;
        }
        if (i != length && exact)
            throw new IllegalArgumentException("Invalid character length: " + i + ", should be: " + length);
        return uc;
    }

    public static Token read(CharReader r, int line, int column) {
        StringBuilder sb = new StringBuilder();
        int start = r.i - 1;

        for (int ch = r.read1(); ch != '"'; ch = r.read1()) {
            if (ch == -1)
                throw Util.runtimeException("EOF while reading string");
            // escape
            if (ch == '\\') {
                ch = r.read1();
                if (ch == -1)
                    throw Util.runtimeException("EOF while reading string");
                switch (ch) {
                    case 't':
                        ch = '\t';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case '\\':
                        break;
                    case '"':
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'u': {
                        ch = r.read1();
                        if (Character.digit(ch, 16) == -1)
                            throw Util.runtimeException("Invalid unicode escape: \\u" + (char) ch);
                        ch = readUnicodeChar(r, ch, 16, 4, true);
                        break;
                    }
                    default: {
                        if (Character.isDigit(ch)) {
                            ch = readUnicodeChar(r, ch, 8, 3, false);
                            if (ch > 0377)
                                throw Util.runtimeException("Octal escape sequence must be in range [0, 377].");
                        } else
                            throw Util.runtimeException("Unsupported escape character: \\" + (char) ch);
                    }
                }
            }
            sb.append((char) ch);
        }
        return new Token(Token.Type.STRING, r.subs(start), sb.toString(), line, column);
    }

    public static Token readRegex(CharReader r, int line, int column) {
        StringBuilder sb = new StringBuilder();
        int start = r.i - 1;
        for (int ch = r.read1(); ch != '"'; ch = r.read1()) {
            if (ch == -1)
                throw Util.runtimeException("EOF while reading regex");
            sb.append((char) ch);
            if (ch == '\\')    //escape
            {
                ch = r.read1();
                if (ch == -1)
                    throw Util.runtimeException("EOF while reading regex");
                sb.append((char) ch);
            }
        }
        return new Token(Token.Type.REGEX, "#" + r.subs(start), Pattern.compile(sb.toString()), line, column);
    }

}
