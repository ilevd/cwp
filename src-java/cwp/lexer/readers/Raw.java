package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.CharReader;
import cwp.lexer.Token;

public class Raw {

    public static Token read(CharReader r, int line, int column) {
        r.read1();
        r.read1();
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            int ch = r.read1();
            if (ch == -1)
                throw Util.runtimeException("EOF while reading raw string");
            if (ch == '"' && r.cur() == '"' && r.next() == '"') {
                r.read1();
                r.read1();
                return new Token(Token.Type.RAW, sb.toString(), null, line, column);
            }
            sb.append((char) ch);
        }
    }

}
