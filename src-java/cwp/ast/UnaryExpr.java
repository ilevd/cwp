package cwp.ast;

import cwp.lexer.Token;

public class UnaryExpr extends Expr {

    Expr val;
    boolean asFn = false;

    public UnaryExpr(Token initTok, Expr val) {
        super(initTok);
        this.val = val;
    }

    public UnaryExpr(Token initTok, Expr val, boolean asFn) {
        super(initTok);
        this.val = val;
        this.asFn = asFn;
    }

    @Override
    public String toString() {
        return "UnaryExpr: " + initTok.toString() + val.initTok.str;
    }

    @Override
    public String gen() {
        if (asFn) {
            return genFn();
        }
        return genPrefix();
    }

    public String genFn() {
        return "(" + initTok.str + " " + val.gen() + ")";
    }

    public String genPrefix() {
        return initTok.str + val.gen();
    }

}


