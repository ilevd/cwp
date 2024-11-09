package cwp.ast;

import cwp.lexer.Token;

public class SimpleExpr extends Expr {

    public SimpleExpr(Token initTok) {
        super(initTok);
    }

    public SimpleExpr(Token initTok, boolean callable) {
        super(initTok, callable);
    }

    @Override
    public String toString() {
        return "SimpleExpr: " + initTok.toString();
    }

    @Override
    public String gen() {
        return initTok.str;
    }
}
