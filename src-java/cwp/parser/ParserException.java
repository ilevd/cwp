package cwp.parser;

import cwp.lexer.Token;

public class ParserException extends Exception {

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Token token) {
        super(message + ", at line: " + token.line + ", column: " + token.column);
    }

}
