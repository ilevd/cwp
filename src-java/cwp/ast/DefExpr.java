package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class DefExpr extends Expr {

    public Token initTok;
    public Token nameTok;
    public boolean isFunction = false;

    public ArrayList<Expr> metas;
    public ArrayList<Expr> args;
    public ArrayList<Expr> body;

    public DefExpr(Token initTok, Token nameTok, boolean isFunction, ArrayList<Expr> metas,
                   ArrayList<Expr> args, ArrayList<Expr> body) {
        super(initTok);
        this.initTok = initTok;
        this.nameTok = nameTok;
        this.isFunction = isFunction;
        this.metas = metas;
        this.args = args;
        this.body = body;
    }

    @Override
    public String toString() {
        return "DefExpr: " + initTok.toString();
    }

    public String gen() {
        if (isFunction)
            return genDefn();
        else
            return genDef();
    }

    public String genMetas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < metas.size(); i++) {
            sb.append("^");
            sb.append(metas.get(i).gen());
            sb.append(" ");
        }
        return sb.toString();
    }

    public String genDef() {
        return "(def " + genMetas() + nameTok.str + " "
                + arrToStr(body) +
                ")";
    }

    public String genDefn() {
        return "(defn " + genMetas() + nameTok.str + " ["
                + arrToStr(args) + "] "
                + arrToStr(body)
                + ")";
    }

    public String arrToStr(ArrayList<Expr> a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.size() - 1; i++) {
            sb.append(a.get(i).gen());
            sb.append(" ");
        }
        if (!a.isEmpty())
            sb.append(a.get(a.size() - 1).gen());
        return sb.toString();
    }
}
