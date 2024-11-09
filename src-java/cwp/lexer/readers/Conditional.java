package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.CharReader;
import cwp.lexer.Token;

public class Conditional {

    static public Token read(CharReader r, int line, int column) {
        boolean splicing = false;
        int splicingChar = r.read1();
        if (splicingChar == '@')
            splicing = true;
        else
            r.unread1(splicingChar);

        int nextChar = r.read1();
        if (nextChar == -1)
            throw Util.runtimeException("EOF while reading character");
        if (nextChar != '(') {
            throw Util.runtimeException("read-cond body must be a list");
        }
        return new Token(Token.Type.CONDITIONAL, "#?" + (splicing ? "@" : "") + "(", null, line, column);
    }
}
