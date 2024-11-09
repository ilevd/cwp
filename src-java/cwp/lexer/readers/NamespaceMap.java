/*
    Based on Clojure LispReader:
    https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/LispReader.java
*/
package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.CharReader;
import cwp.lexer.Common;
import cwp.lexer.Token;

public class NamespaceMap {

    static public Token read(CharReader r, int line, int column) {
        String sym = "";
        boolean auto = false;
        int autoChar = r.read1();
        if (autoChar == ':')
            auto = true;
        else
            r.unread1(autoChar);
        int nextChar = r.read1();
        if (Common.isWhitespace(nextChar)) {  // the #:: { } case or an error
            if (auto) {
                while (Common.isWhitespace(nextChar))
                    nextChar = r.read1();
                if (nextChar != '{') {
                    r.unread1(nextChar);
                    throw Util.runtimeException("Namespaced map must specify a namespace");
                }
            } else {
                r.unread1(nextChar);
                throw Util.runtimeException("Namespaced map must specify a namespace");
            }
        } else if (nextChar != '{') {  // #:foo { } or #::foo { }
            sym = Identifier.read(r, (char) nextChar);
            nextChar = r.read1();
            while (Common.isWhitespace(nextChar))
                nextChar = r.read1();
        }
        if (nextChar != '{')
            throw Util.runtimeException("Namespaced map must specify a map");
        return new Token(Token.Type.NAMESPACE_MAP, "#:" + (auto ? ":" : "") + sym + "{", null, line, column);
    }
}
