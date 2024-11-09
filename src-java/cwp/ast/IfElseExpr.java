package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class IfElseExpr extends Expr {

    public Expr condExpr;
    public ArrayList<Expr> ifExprs;
    public ArrayList<Expr> elseExprs;

    public IfElseExpr(Token initTok, Expr condExpr, ArrayList<Expr> ifExprs, ArrayList<Expr> elseExprs) {
        super(initTok);
        this.condExpr = condExpr;
        this.ifExprs = ifExprs;
        this.elseExprs = elseExprs;
    }

    @Override
    public String toString() {
        return "IfElseExpr: " + initTok.toString();
    }


    public String gen() {
        return "(if " + condExpr.gen() + " " +
                genIf() +
                (elseExprs != null ? " " + genElse() : "") +
                ")";
    }

    public String genIf() {
        return (ifExprs.size() == 1 ? ifExprs.get(0).gen() :
                ("(do " + arrToStr(ifExprs) + ")"));
    }

    public String genElse() {
        return (elseExprs.size() == 1 ? elseExprs.get(0).gen() :
                ("(do " + arrToStr(elseExprs) + ")"));
    }


    public String arrToStr(ArrayList<Expr> a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.size() - 1; i++) {
            sb.append(a.get(i).gen());
            sb.append(" ");
        }
        sb.append(a.get(a.size() - 1).gen());
        return sb.toString();
    }
}
