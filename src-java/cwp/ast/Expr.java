package cwp.ast;

import cwp.lexer.Token;

public class Expr {

    public Token initTok;
    public boolean callable = false;

    public Expr() {
    }

    public Expr(Token initTok) {
        this.initTok = initTok;
    }

    public Expr(Token initTok, boolean callable) {
        this.initTok = initTok;
        this.callable = callable;
    }

    @Override
    public String toString() {
        return "Expr: " + initTok.toString();
    }

    public String gen() {
        return "<empty>";
    }

}
