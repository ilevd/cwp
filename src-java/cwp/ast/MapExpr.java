package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class MapExpr extends Expr {
    public ArrayList<Expr> args;

    public MapExpr(Token initTok, ArrayList<Expr> a, boolean callable) {
        super(initTok, callable);
        this.args = a;
    }

    @Override
    public String toString() {
        return "MapExpr: " + initTok.toString();
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder(initTok.str);
        for (int i = 0; i < args.size() - 2; i += 2) {
            sb.append(args.get(i).gen());
            sb.append(" ");
            sb.append(args.get(i + 1).gen());
            sb.append(", ");
        }
        if (!args.isEmpty()) {
            sb.append(args.get(args.size() - 2).gen());
            sb.append(" ");
            sb.append(args.get(args.size() - 1).gen());
        }
        sb.append("}");
        return sb.toString();
    }

}
