package cwp.lexer;

import clojure.lang.Util;

import java.util.ArrayList;

public class IdentReader {


    CharReader r;
    ArrayList<Integer> identation;
    int curLine = 1;

    public IdentReader(String s) {
        CharReader r = new CharReader(s);
        identation.add(1);
    }


    public Token read() {
        Token t = LexerReader.read(r);
        return t;
    }

    public ArrayList<Token> readAll() {
        ArrayList<Token> arr = new ArrayList<Token>();
        Token t = LexerReader.read(r);

        curLine = t.line;
        if (t.column != identation.get(identation.size() - 1)) {
            Util.runtimeException("EOF while reading character");
        }

        while (t.type != Token.Type.EOF) {
            arr.add(t);

            t = LexerReader.read(r);
            if (t.type == Token.Type.COLON) {

            }


        }
        arr.add(t);
        return arr;

    }


}
