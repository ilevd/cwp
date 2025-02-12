package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class SetExpr extends Expr {
    public ArrayList<Expr> args;

    public SetExpr(Token initTok, ArrayList<Expr> a, boolean callable) {
        super(initTok, callable);
        this.args = a;
    }

    @Override
    public String toString() {
        return "SetExpr: " + initTok.toString();
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder("#{");
        for (int i = 0; i < args.size() - 1; i++) {
            sb.append(args.get(i).gen());
            sb.append(" ");
        }
        if (!args.isEmpty()) {
            sb.append(args.get(args.size() - 1).gen());
        }
        sb.append("}");
        return sb.toString();
    }

}
