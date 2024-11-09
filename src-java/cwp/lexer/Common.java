/*
    Based on Clojure LispReader:
    https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/LispReader.java
*/
package cwp.lexer;

public class Common {

    static Integer[] macros = new Integer[256];

    static {
        macros['"'] = 1; // String
        macros[';'] = 1; // Comment
        // macros['\'']  //  Quote
        // macros['@']   //  Deref
        macros['^'] = 1; // Meta
        macros['`'] = 1; // SyntaxQuote
        // macros['~']   // Unquote
        macros['('] = 1; // List
        macros[')'] = 1; // UnmatchedDelimiter
        macros['['] = 1; // Vector
        macros[']'] = 1; // UnmatchedDelimiter
        macros['{'] = 1; // Map
        macros['}'] = 1; // UnmatchedDelimiter
        // macros['|']   //  ArgVector
        macros['\\'] = 1;// Character
        // macros['%']   //  Arg
        macros['#'] = 1; // DispatchReader
    }

    public static boolean isWhitespace(int c) {
        return Character.isWhitespace(c);
    }

    static public boolean isMacro(int ch) {
        return (ch < macros.length && macros[ch] != null);
    }

    static public boolean isTerminatingMacro(int ch) {
        return (ch != '#' && ch != '\'' && ch != '%' && isMacro(ch));
    }

}
