package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class ParensExpr extends Expr {
    public ArrayList<Expr> args;

    public ParensExpr(Token initTok, ArrayList<Expr> a, boolean callable) {
        super(initTok, callable);
        this.args = a;
    }

    @Override
    public String toString() {
        return "ParensExpr: " + initTok.toString();
    }

    @Override
    public String gen() {
        if (args.size() == 1) {
            return args.get(0).gen();
        } else {
            StringBuilder sb = new StringBuilder("(do ");
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

}