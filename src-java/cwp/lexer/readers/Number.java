/*
    Based on Clojure LispReader:
    https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/LispReader.java
*/
package cwp.lexer.readers;

import clojure.lang.BigInt;
import clojure.lang.Numbers;
import cwp.lexer.Token;
import cwp.lexer.Common;
import cwp.lexer.CharReader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Number {

    static Pattern intPat =
            Pattern.compile(
                    "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?");
    static Pattern ratioPat = Pattern.compile("([-+]?[0-9]+)/([0-9]+)");
    static Pattern floatPat = Pattern.compile("([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?");

    private static Object matchNumber(String s) {
        Matcher m = intPat.matcher(s);
        if (m.matches()) {
            if (m.group(2) != null) {
                if (m.group(8) != null)
                    return BigInt.ZERO;
                return Numbers.num(0);
            }
            boolean negate = (m.group(1).equals("-"));
            String n;
            int radix = 10;
            if ((n = m.group(3)) != null)
                radix = 10;
            else if ((n = m.group(4)) != null)
                radix = 16;
            else if ((n = m.group(5)) != null)
                radix = 8;
            else if ((n = m.group(7)) != null)
                radix = Integer.parseInt(m.group(6));
            if (n == null)
                return null;
            BigInteger bn = new BigInteger(n, radix);
            if (negate)
                bn = bn.negate();
            if (m.group(8) != null)
                return BigInt.fromBigInteger(bn);
            return bn.bitLength() < 64 ?
                    Numbers.num(bn.longValue())
                    : BigInt.fromBigInteger(bn);
        }
        m = floatPat.matcher(s);
        if (m.matches()) {
            if (m.group(4) != null)
                return new BigDecimal(m.group(1));
            return Double.parseDouble(s);
        }
        m = ratioPat.matcher(s);
        if (m.matches()) {
            String numerator = m.group(1);
            if (numerator.startsWith("+")) numerator = numerator.substring(1);

            return Numbers.divide(Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(numerator))),
                    Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(m.group(2)))));
        }
        return null;
    }

    static public Token readNumber(CharReader r, char initch, int line, int column) {
        // int line = r.getLineNumber();
        // int column = r.getColumnNumber();
        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (; ; ) {
            int ch = r.read1();
            if (ch == -1 || Common.isWhitespace(ch) || ch == ',' || Common.isMacro(ch)
                    || ch == ':') {
                r.unread1(ch);
                break;
            }
            sb.append((char) ch);
        }

        String s = sb.toString();
        Object n = matchNumber(s);
        if (n == null)
            throw new NumberFormatException("Invalid number: " + s);
        return new Token(Token.Type.NUMBER, s, n, line, column);
    }

}
