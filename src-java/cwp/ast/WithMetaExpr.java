package cwp.ast;

import cwp.lexer.Token;

public class WithMetaExpr extends Expr {

    Expr meta;
    Expr expr;

    public WithMetaExpr(Token initTok, Expr meta, Expr expr) {
        super(initTok);
        this.expr = expr;
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "WithMetaExpr{" +
                "meta=" + meta +
                ", expr=" + expr +
                '}';
    }

    @Override
    public String gen() {
        return "^" + meta.gen() +
                " " +
                expr.gen();
    }
}
