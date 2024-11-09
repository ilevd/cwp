package cwp.ast;

import cwp.lexer.Token;
import cwp.parser.Controls;

import java.util.ArrayList;

public class ControlExpr extends Expr {

    ArrayList<Expr> args;
    ArrayList<Expr> body;
    Controls.Type type;

    public ControlExpr(Token initTok, ArrayList<Expr> args, ArrayList<Expr> body, Controls.Type type) {
        super(initTok);
        this.args = args;
        this.body = body;
        this.type = type;
    }

    @Override
    public String toString() {
        return initTok.toString();
    }

    @Override
   /* public String gen() {
        *if (type == Controls.Type.FLAT) {
            return genFlat();
        }
        if (type == Controls.Type.MAP) {
            return genMap();
        }
        return genVec();
    }*/
    public String gen() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(initTok.str);
        sb.append(" ");

        if (type == Controls.Type.VEC) {
            sb.append("[");
        } else if (type == Controls.Type.MAP) {
            sb.append("{");
        }

        // args
        for (int i = 0; i < args.size() - 1; i++) {
            sb.append(args.get(i).gen());
            sb.append(" ");
        }
        if (!args.isEmpty()) {
            sb.append(args.get(args.size() - 1).gen());
        }
        if (type == Controls.Type.VEC) {
            sb.append("]");
        } else if (type == Controls.Type.MAP) {
            sb.append("}");
        }
        if (!args.isEmpty())
            sb.append(" ");
        // body
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
