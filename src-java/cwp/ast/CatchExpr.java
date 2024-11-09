package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class CatchExpr extends Expr {
    public Token exceptionType;
    public Token exceptionName;
    public ArrayList<Expr> body;

    public CatchExpr(Token initTok, Token exceptionType, Token exceptionName, ArrayList<Expr> body) {
        super(initTok);
        this.exceptionName = exceptionName;
        this.exceptionType = exceptionType;
        this.body = body;
    }

    @Override
    public String toString() {
        return initTok.toString();
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder("(catch ");
        sb.append(exceptionType.str);
        sb.append(" ");
        sb.append(exceptionName.str);
        sb.append(" ");
        for (int i = 0; i < body.size() - 1; i++) {
            sb.append(body.get(i).gen());
            sb.append("\n");
        }
        if (!body.isEmpty()) {
            sb.append(body.get(body.size() - 1).gen());
        }
        sb.append(")");
        return sb.toString();
    }
}
