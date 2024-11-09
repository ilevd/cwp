package cwp.ast;

import java.util.ArrayList;

public class FunctionCallExpr extends Expr {
    public Expr first;
    public ArrayList<Expr> args;

    public FunctionCallExpr(Expr first, ArrayList<Expr> args, boolean callable) {
        super(first.initTok, callable);
        this.first = first;
        this.args = args;
    }

    @Override
    public String toString() {
        return "FunctionCallExpr: " + initTok.toString();
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(first.gen());
        if (!args.isEmpty()) sb.append(" ");
        for (int i = 0; i < args.size() - 1; i++) {
            sb.append(args.get(i).gen());
            sb.append(" ");
        }
        if (!args.isEmpty()) {
            sb.append(args.get(args.size() - 1).gen());
        }
        sb.append(")");
        return sb.toString();
    }

}
