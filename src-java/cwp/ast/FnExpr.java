package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class FnExpr extends Expr {

    ArrayList<Expr> args;
    ArrayList<Expr> body;

    public FnExpr(Token initTok, ArrayList<Expr> args, ArrayList<Expr> body) {
        super(initTok);
        this.args = args;
        this.body = body;
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder("(fn [");
        for (int i = 0; i < args.size() - 1; i++) {
            sb.append(args.get(i).gen());
            sb.append(" ");
        }
        if (!args.isEmpty()) {
            sb.append(args.get(args.size() - 1).gen());
        }
        sb.append("] ");
        for (int i = 0; i < body.size() - 1; i++) {
            sb.append(body.get(i).gen());
            sb.append(" ");
        }
        if (!body.isEmpty()) {
            sb.append(body.get(body.size() - 1).gen());
        }
        sb.append(")");
        return sb.toString();
    }
}
