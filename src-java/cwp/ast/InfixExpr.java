package cwp.ast;

import cwp.lexer.Token;

public class InfixExpr extends Expr {

    public Expr leftExpr;
    public Expr rightExpr;

    public InfixExpr(Token initTok, Expr leftExpr, Expr rightExpr) {
        super(initTok);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public String toString() {
        return "InfixExpr: " + initTok.toString();
    }

    public static String toOp(String s) {
        if (s.equals("|>")) return "->";
        if (s.equals("|>>")) return "->>";
        if (s.equals("!=")) return "not=";
        return s;
    }

    @Override
    public String gen() {
        return "(" + toOp(initTok.str) + " " + leftExpr.gen() + " " + rightExpr.gen() + ")";
    }

}
