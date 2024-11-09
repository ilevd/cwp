package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.CharReader;
import cwp.lexer.LexerReader;
import cwp.lexer.Token;

public class SymbolicValue {
    public static Token read(CharReader r, int line, int column) {
        Token t = LexerReader.read(r);
        if (t.type != Token.Type.SYMBOL)
            throw Util.runtimeException("Invalid token: ##" + t.str);
        if (t.str.equals("NaN"))
            return new Token(Token.Type.SYMBOLIC_VALUE, "##NaN", null, line, column);
        if (t.str.equals("Inf"))
            return new Token(Token.Type.SYMBOLIC_VALUE, "##Inf", null, line, column);
        if (t.str.equals("-Inf"))
            return new Token(Token.Type.SYMBOLIC_VALUE, "##-Inf", null, line, column);
        throw Util.runtimeException("Unknown symbolic value: ##" + t.str);
    }
}
