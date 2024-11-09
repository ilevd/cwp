package cwp.lexer;

import java.util.ArrayList;
import java.util.Stack;

import cwp.ast.Expr;
import cwp.lexer.readers.*;
import cwp.lexer.readers.Number;

public class LexerReader {

    CharReader r;
    Stack<Token> tokenStack = new Stack<>();

    public LexerReader(String s) {
        r = new CharReader(s);
    }

    public Token read() {
        if (!tokenStack.empty()) {
            return tokenStack.pop();
        }
        return read(r);
    }

    public void unread(Token t) {
        tokenStack.push(t);
    }


    public static Object readString(String s) {
        //IdentReader r = new IdentReader(new StringReader(s));
        CharReader r = new CharReader(s);
        return read(r);
    }

    public static Token read(CharReader r) {
        Token t = readBaseToken(r);
        int ch = r.read1();
        if (ch == '(') {
            t.callable = true;
        }
        r.unread1(ch);
        return t;
    }

    public static Token readBaseToken(CharReader r) {
        try {
            for (; ; ) {
                int ch = r.read1();
                while (Common.isWhitespace(ch)) ch = r.read1();
                //System.out.println("Lexer read: " + (char) ch + " , r i:" + r.i);

                int line = r.getLineNumber();
                int column = r.getColumnNumber() - 1;

                if (ch == -1) return new Token(Token.Type.EOF, "", null, line, column);

                // number
                if (Character.isDigit(ch)) {
                    return Number.readNumber(r, (char) ch, line, column);
                }
                if (ch == '+' || ch == '-') {
                    int ch2 = r.read1();
                    if (Character.isDigit(ch2)) {
                        r.unread1(ch2);
                        return Number.readNumber(r, (char) ch, line, column);
                    }
                    r.unread1(ch2);
                }

                switch (ch) {
                    case '\\':
                        return Char.read(r, line, column);
                    case '"':
                        if (r.cur() == '"' && r.next() == '"')
                            return Raw.read(r, line, column);
                        return Str.read(r, line, column);
                    // Macros
                    case '\'':
                        return new Token(Token.Type.QUOTE, "'", null, line, column);
                    case '@':
                        return new Token(Token.Type.DEREF, "@", null, line, column);
                    case '^':
                        return new Token(Token.Type.META, "^", null, line, column);
                    case '`':
                        return new Token(Token.Type.SYNTAX_QUOTE, "`", null, line, column);
                    case '~':
                        int nextChar = r.read1();
                        if (nextChar == '@') {
                            return new Token(Token.Type.UNQUOTE, "~@", null, line, column);
                        }
                        r.unread1(nextChar);
                        return new Token(Token.Type.UNQUOTE, "~", null, line, column);
                    // case '%':
                    // return new Token(Token.Type.UNQUOTE, "'", null, line, column);
                    case '(':
                        return new Token(Token.Type.LPAREN, "(", null, line, column);
                    case ')':
                        return new Token(Token.Type.RPAREN, ")", null, line, column);
                    case '[':
                        return new Token(Token.Type.LBRACE, "[", null, line, column);
                    case ']':
                        return new Token(Token.Type.RBRACE, "]", null, line, column);
                    case '{':
                        return new Token(Token.Type.LCURLY, "{", null, line, column);
                    case '}':
                        return new Token(Token.Type.RCURLY, "}", null, line, column);
                    case ':':
                        if (Character.isWhitespace(r.cur())) {
                            return new Token(Token.Type.COLON, ":", null, line, column);
                        }
                        break;
                    case ',':
                        return new Token(Token.Type.COMMA, ",", null, line, column);
//                        if (Character.isWhitespace(r.cur())) {
//                            return new Token(Token.Type.COMMA, ",", null, line, column);
//                        }
                    case '/':
                        if (r.cur() == '/') {
                            r.read1();
                            readComment(r);
                            continue;
                        }

                        // Dispatch macros
                    case '#':
                        int cc = r.read1();
                        switch (cc) {
                            case '^':
                                return new Token(Token.Type.META, "#^", null, line, column);
                            case '#':
                                return SymbolicValue.read(r, line, column);
                            case '\'':
                                return new Token(Token.Type.VAR_QUOTE, "#'", null, line, column);
                            case '\"':
                                return Str.readRegex(r, line, column);
                            case '{':
                                return new Token(Token.Type.SET, "#{", null, line, column);
                            case '?':
                                return Conditional.read(r, line, column);
                            case ':':
                                return NamespaceMap.read(r, line, column);
                            case '!':
                                readComment(r);
                                continue;


                        }
                        r.unread1(cc);
                }
                //System.out.println("Lexer read: " + (char) ch + " , r i:" + r.i);
                return Identifier.readToken(r, (char) ch, line, column);

            }
        } catch (Exception e) {
            throw new RuntimeException("Reader exception at line: " + r.getLineNumber() + ", number: " + r.getColumnNumber(), e);
        }

    }

    public static void readComment(CharReader r) {
        for (; ; ) {
            int c = r.read1();
            if (c == '\n' || c == '\r' || c == -1) break;
        }
    }

    public static ArrayList<Token> readAll(String s) {
        ArrayList<Token> arr = new ArrayList<Token>();
        CharReader r = new CharReader(s);
        Token t = read(r);
        int i = 0;
        while (t.type != Token.Type.EOF) {
            //System.out.println(t);
            arr.add(t);
            t = read(r);
            i++;
        }
        arr.add(t);
        return arr;
    }


}
