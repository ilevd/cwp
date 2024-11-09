package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class MultiInfixExpr extends Expr {

    public ArrayList<Expr> exprs;
    public Token opToken;

    public MultiInfixExpr(Token initTok, Token opToken, Expr expr) {
        super(initTok);
        this.opToken = opToken;
        this.exprs = new ArrayList<Expr>();
        this.exprs.add(expr);
    }

    public void add(Expr e) {
        this.exprs.add(e);
    }

    public Expr getExpr() {
        if (exprs.size() == 1) {
            return exprs.get(0);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "MultiInfixExpr: " + initTok.toString();
    }

    public static String toOp(String s) {
        if (s.equals("|>")) return "->";
        if (s.equals("|>>")) return "->>";
        if (s.equals("!=")) return "not=";
        return s;
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(toOp(opToken.str));
        for (Expr expr : exprs) {
            sb.append(" ").append(expr.gen());
        }
        sb.append(")");
        return sb.toString();
    }

}
