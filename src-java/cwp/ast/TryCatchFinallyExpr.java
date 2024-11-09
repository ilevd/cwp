package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class TryCatchFinallyExpr extends Expr {

    ArrayList<Expr> body;
    ArrayList<CatchExpr> catches;
    ArrayList<Expr> finallyExprs;


    public TryCatchFinallyExpr(Token initTok, ArrayList<Expr> body, ArrayList<CatchExpr> catches, ArrayList<Expr> finallies) {
        super(initTok);
        this.body = body;
        this.catches = catches;
        this.finallyExprs = finallies;
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder("(try ");
        for (int i = 0; i < body.size() - 1; i++) {
            sb.append(body.get(i).gen());
            sb.append("\n");
        }
        if (!body.isEmpty()) {
            sb.append(body.get(body.size() - 1).gen());
        }
        // add catches
        for (int i = 0; i < catches.size() - 1; i++) {
            sb.append(catches.get(i).gen());
            sb.append("\n");
        }
        if (!catches.isEmpty()) {
            sb.append(catches.get(catches.size() - 1).gen());
        }
        // add finally
        if (!finallyExprs.isEmpty()) {
            sb.append("(finally ");
            for (int i = 0; i < finallyExprs.size() - 1; i++) {
                sb.append(finallyExprs.get(i).gen());
                sb.append("\n");
            }
            if (!finallyExprs.isEmpty()) {
                sb.append(finallyExprs.get(finallyExprs.size() - 1).gen());
            }
            sb.append(")");
        }
        sb.append(")");
        return sb.toString();
    }


}
