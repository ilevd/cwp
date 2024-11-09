package cwp.lexer.readers;

import clojure.lang.Util;
import cwp.lexer.Token;
import cwp.lexer.Common;
import cwp.lexer.CharReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Identifier {

    static Pattern symbolPat = Pattern.compile("[:]?([\\D&&[^/]].*/)?(/|[\\D&&[^/]][^/]*)");
    static Pattern arraySymbolPat = Pattern.compile("([\\D&&[^/:]].*)/([1-9])");

    static public String read(CharReader r, char initch) {
        StringBuilder sb = new StringBuilder();
        sb.append(initch);
        for (; ; ) {
            int ch = r.read1();
//            System.out.println("Token read: " + (char) ch +
//                    " " + (ch == -1 || Common.isWhitespace(ch) || Common.isTerminatingMacro(ch))
//                    + " " + r.i);
            if (ch == -1 ||
                    Common.isWhitespace(ch) ||
                    Common.isTerminatingMacro(ch) ||
                    // ch == ':' ||
                    ch == ',') {
                r.unread1(ch);
                String s = sb.toString();
                if (s.charAt(s.length() - 1) == ':') {
                    s = s.substring(0, s.length() - 1);
                    r.unread1(':');
                }
               /* System.out.println(">>endread: " + s);
                int i = s.length() - 1;
                while (i >= 0 && s.charAt(i) == ':') {
                    System.out.println(">>decolon: " + i + " " + s.charAt(i));
                    r.unread1(s.charAt(i));
                    i--;
                }
                s = s.substring(0, i + 1);
                if (s.isEmpty()) {
                    throw Util.runtimeException("Invalid token: " + s);
                }*/
                return s;
            }
            sb.append((char) ch);
        }
    }

    static public Token interpret(String s) {
        if (s.equals("nil")) {
            return new Token(Token.Type.NULL, "nil", null);
        } else if (s.equals("true")) {
            return new Token(Token.Type.BOOL, "true", Boolean.TRUE);
        } else if (s.equals("false")) {
            return new Token(Token.Type.BOOL, "false", Boolean.FALSE);
        } else if (s.equals("to")) {
            return new Token(Token.Type.TO, "to", Boolean.FALSE);
        }
//        else if (s.equals("|>") || s.equals("|>>")) {
//            return new Token(Token.Type.PIPE_OP, s);
//        } else if (s.equals("or")) {
//            return new Token(Token.Type.OR_OP, s);
//        } else if (s.equals("and")) {
//            return new Token(Token.Type.AND_OP, s);
//        } else if (s.equals("not")) {
//            return new Token(Token.Type.NOT_OP, s);
//        } else if (s.equals("=") || s.equals("==") || s.equals("!=") || s.equals(">=") || s.equals("<=")
//                || s.equals(">") || s.equals("<")) {
//            return new Token(Token.Type.COMPARISON_OP, s);
//        } else if (s.equals("+") || s.equals("-")) {
//            return new Token(Token.Type.SUM_OP, s);
//        } else if (s.equals("*") || s.equals("/")) {
//            return new Token(Token.Type.PRODUCT_OP, s);
//        }
        Token ret = matchSymbol(s);
        if (ret != null)
            return ret;
        throw Util.runtimeException("Invalid token: " + s);
    }

    static public Token readToken(CharReader r, char initchString, int line, int column) {
        Token t = interpret(read(r, initchString));
        t.line = line;
        t.column = column;
        if ((t.type == Token.Type.SYMBOL ||
                t.type == Token.Type.KEYWORD) && r.cur() == '(') {
            t.callable = true;
        }
        return t;
    }

    private static Token matchSymbol(String s) {
        Matcher m = symbolPat.matcher(s);
        if (m.matches()) {
            int gc = m.groupCount();
            String ns = m.group(1);
            String name = m.group(2);
            if (ns != null && ns.endsWith(":/")
                    || name.endsWith(":")
                    || s.indexOf("::", 1) != -1)
                return null;
            if (s.startsWith("::")) {
                return new Token(Token.Type.KEYWORD, s);
            }
            boolean isKeyword = s.charAt(0) == ':';
            if (isKeyword)
                return new Token(Token.Type.KEYWORD, s);
            return new Token(Token.Type.SYMBOL, s);
        } else {
            Matcher am = arraySymbolPat.matcher(s);
            if (am.matches())
                return new Token(Token.Type.SYMBOL, s);
        }
        return null;
    }


}
