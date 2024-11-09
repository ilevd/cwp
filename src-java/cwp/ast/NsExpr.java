package cwp.ast;

import cwp.lexer.Token;

import java.util.ArrayList;

public class NsExpr extends Expr {

    Expr name;
    ArrayList<Expr> requires;
    ArrayList<Expr> imports;

    public NsExpr(Token initTok, Expr name, ArrayList<Expr> requires, ArrayList<Expr> imports) {
        super(initTok);
        this.name = name;
        this.requires = requires;
        this.imports = imports;
    }

    @Override
    public String toString() {
        return "<NsExpr: " +
                (requires == null ? "" : requires.toString()) + " " +
                (imports == null ? "" : imports.toString()) + " " +
                (requires == null ? "" : requires.size()) + " " +
                (imports == null ? "" : imports.size()) +
                ">";
    }

    public String subs(String s) {
        return s.substring(1, s.length() - 1);
    }

    @Override
    public String gen() {
        StringBuilder sb = new StringBuilder();
        sb.append("(ns ");
        sb.append(name.gen());
        // requires
        if (requires != null) {
            if (!requires.isEmpty()) {
                sb.append("\n  (:require ");
                sb.append(requires.get(0).gen());
            }
            for (int i = 1; i < requires.size() - 1; i++) {
                sb.append("\n            ");
                sb.append(requires.get(i).gen());
            }
            if (requires.size() > 1) {
                sb.append("\n            ");
                sb.append(requires.get(requires.size() - 1).gen());
            }
            if (!requires.isEmpty()) {
                sb.append(")");
            }
        }
        // imports
        if (imports != null) {
            if (!imports.isEmpty()) {
                sb.append("\n  (:import (");
                sb.append(subs(imports.get(0).gen()));
                sb.append(")");
            }
            for (int i = 1; i < imports.size() - 1; i++) {
                sb.append("\n            (");
                sb.append(subs(imports.get(i).gen()));
                sb.append(")");
            }
            if (imports.size() > 1) {
                sb.append("\n           (");
                sb.append(subs(imports.get(imports.size() - 1).gen()));
                sb.append(")");
            }
            if (!imports.isEmpty()) {
                sb.append(")");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
