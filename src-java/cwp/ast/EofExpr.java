package cwp.ast;

import cwp.lexer.Token;

public class EofExpr extends Expr {

    public static EofExpr EOF_EXPR = new EofExpr(new Token(Token.Type.EOF));

    public EofExpr(Token token) {
        super(token);
    }

    @Override
    public String toString() {
        return "EofExpr: " + initTok.toString();
    }

    public String gen() {
        return "<EOF>";
    }

}
