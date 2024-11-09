package cwp.lexer;

public class Token {

    public enum Type {
        EOF,
        // Primitive
        NUMBER,
        CHAR,
        STRING,
        NULL,
        BOOL,
        SYMBOL,
        KEYWORD,

        // Macros
        QUOTE, // '
        DEREF, // @
        META, // ^ or #^
        SYNTAX_QUOTE, // `
        UNQUOTE, // ~ or ~@
        ARG, // %

        // Dispatch macros
        SYMBOLIC_VALUE, // ##Inf, ##NaN
        VAR_QUOTE, // #'var
        REGEX,      // #"asfd"
        SET,        // #{}
        COMMENT, // ##Inf, ##NaN
        CONDITIONAL, // #? or #?@
        NAMESPACE_MAP,  // #:a/b{} , #::{:a 1, :b 2}

        LBRACE,
        RBRACE,
        LPAREN,
        RPAREN,
        LCURLY,
        RCURLY,

        // Delimiters
        TO,
        COLON, // ':'
        COMMA, // ','
        RAW,

        // Operators
        PIPE_OP,
        OR_OP,
        AND_OP,
        NOT_OP,
        COMPARISON_OP,
        SUM_OP,
        PRODUCT_OP

        // Concatenators
        /*
        PLUS,
        MINUS,
        MUL,
        DIV,
        AND,
        OR,
       */
        // Braces   TODO: ?
        // Dispatch TODO: ?

        // Identation
        // INDENT,
        // DEDENT,
    }

    public Type type;
    public String str;
    public Object val;
    public int line;
    public int column;

    public Boolean callable = false;

    public Token(Type type) {
        this.type = type;
    }

    public Token(Type type, String str) {
        this.type = type;
        this.str = str;
    }

    public Token(Type type, String str, Object val) {
        this.type = type;
        this.str = str;
        this.val = val;
    }

    public Token(Type type, String str, Object val, int line, int column) {
        this.type = type;
        this.str = str;
        this.val = val;
        this.line = line;
        this.column = column;
    }

    public String toString() {
        return "<" + type +
                (str == null ? "" : (": '" + str + "'")) +
                ((str != null && val != null) ? " " : "") +
                (val == null ? "" : (": " + val)) +
                (callable ? " callable" : "") +
                ", line: " + line + ", column: " + column +
                ">";
    }
}
