package cwp.parser;

import clojure.lang.Util;
import cwp.ast.*;

import cwp.lexer.LexerReader;
import cwp.lexer.Token;
import cwp.lexer.readers.Str;

import java.util.*;

public class Parser {
    // Precedence
    final static int LOWEST = 0;
    final static int PIPE = 1;       // |> , |>>
    final static int OR = 2;
    final static int AND = 3;
    final static int NOT = 4;
    final static int COMPARISON = 5; // >, <, >= ,<=, =, ==, !=,
    final static int SUM = 6;        // +, -
    final static int PRODUCT = 7;    // *, /

    static HashMap<String, Integer> precedence = new HashMap<>();

    static {
        precedence.put("|>", PIPE);
        precedence.put("|>>", PIPE);
        precedence.put("or", OR);
        precedence.put("and", AND);
        // precedence.put("not", NOT);
        precedence.put("=", COMPARISON);
        precedence.put("==", COMPARISON);
        precedence.put("!=", COMPARISON);
        precedence.put(">", COMPARISON);
        precedence.put("<", COMPARISON);
        precedence.put(">=", COMPARISON);
        precedence.put("<=", COMPARISON);
        precedence.put("+", SUM);
        precedence.put("-", SUM);
        precedence.put("*", PRODUCT);
        precedence.put("/", PRODUCT);
    }

    public static int getPrecedence(String s) {
        return precedence.getOrDefault(s, -1);
    }

  /*
   enum Precedence {
        LOWEST,
        PIPE,  // |> , |>>
        OR,
        AND,
        NOT,
        COMPARISON, //>, <, >= ,<=, =, ==, !=,
        SUM,       // +, -
        PRODUCT   // *, /
        // PREFIX
        // CALL
    }
*/

    //CharReader charReader;
    LexerReader lexerReader;
    ArrayList<Integer> indentation = new ArrayList<Integer>();
    int curLine = 1;


    public int lastIndentation() {

        int lastIndex = indentation.size() - 1;
        if (lastIndex < 0) {
            return -1;
        }
        return indentation.get(lastIndex);
    }

    public void addIndentation(int i) {
        indentation.add(i);
        /*if (i > lastIndentation())
            indentation.add(i);
        else {
            Util.sneakyThrow(new ParserException("Bad indentation level: " + indentation + " " + i));
        }*/
    }

    public void popIndentation() {
        indentation.remove(indentation.size() - 1);
    }

    public boolean checkIndentation(int indent) {
        return indentation.contains(indent);
    }


    public Parser(String s) {
        //charReader = new CharReader(s);
        lexerReader = new LexerReader(s);
        // indentation.add(1);
    }

    public void unreadToken(Token t) {
        lexerReader.unread(t);
    }

    public Token nextTokenWithSep() {
        //return LexerReader.read(charReader);
        return lexerReader.read();
    }

    public Token nextToken() {
//        Token t = LexerReader.read(charReader);
//        while (t.type == Token.Type.COMMA || t.type == Token.Type.TO) {
//            t = LexerReader.read(charReader);
//        }
//        return t;
        Token t = lexerReader.read();
        while (t.type == Token.Type.COMMA || t.type == Token.Type.TO) {
            t = lexerReader.read();
        }
        return t;
    }

    public Expr readExpr() {
        return readExpr(Token.Type.EOF, EofExpr.EOF_EXPR);
    }

    Stack<Expr> stackExpr = new Stack<>();

    // Dangerous to use, because when read nextToken(), after unreadExpr() somewhere,
    // nextToken() return token after unreaded Expressions - because tokens aren't from unread expr aren't unread
    //    public void unreadExpr(Expr e) {
    //        stackExpr.push(e);
    //    }

    public Expr readExpr(Token.Type delim, Expr delimReturn) {
        if (!stackExpr.empty()) {
            return stackExpr.pop();
        }
        Token t = nextToken();
        if (t.type == Token.Type.EOF) {
            return EofExpr.EOF_EXPR;
        }
        if (t.type == delim) {
            delimReturn.initTok = t;
            return delimReturn;
        }
        if (t.type == Token.Type.SYMBOL && t.str.equals("def") && !t.callable) {
            return readDef(t);
        }
        if (t.type == Token.Type.SYMBOL && t.str.equals("if") && !t.callable) {
            return readIfElse(t);
        }
        if (t.type == Token.Type.SYMBOL && t.str.equals("try") && !t.callable) {
            return readTryCatchFinally(t);
        }
        if (t.type == Token.Type.SYMBOL && (t.str.equals("fn") || t.str.equals("lambda")) && !t.callable) {
            return readFn(t);
        }
        if (t.type == Token.Type.SYMBOL && t.str.equals("ns") && !t.callable) {
            return readNS(t);
        }
        if (t.type == Token.Type.SYMBOL && Controls.isFlat(t.str)) {
            return readFlatCotrol(t, Controls.Type.FLAT);
        }
        if (t.type == Token.Type.SYMBOL && Controls.isVec(t.str)) {
            return readFlatCotrol(t, Controls.Type.VEC);
        }
        if (t.type == Token.Type.SYMBOL && Controls.isMap(t.str)) {
            return readFlatCotrol(t, Controls.Type.MAP);
        }
        // Expr e = readBaseExpr(t);
        //Expr e = readUnaryExpr(t);
        Expr e = readInfixExpr(t, LOWEST);
        //System.out.println(">>>> " + e);
        if (e == null) {
            throw Util.sneakyThrow(new ParserException("Unexpected token: " + t.str, t));
        }
        return e;
    }


//    public Expr readInfixExpr(Token t, int prevPrecedence) {
//        Expr leftExpr = readUnaryExpr(t);
//        Token opToken = nextTokenWithSep();
//        //System.out.println(">>readInfixExpr " + leftExpr + " " + opToken + " " + prevPrecedence + " " + (prevPrecedence >= SUM));
//        for (; ; ) {
//            int curPrecedence = getPrecedence(opToken.str);
//            if (curPrecedence == -1 || prevPrecedence >= curPrecedence) {
//                unreadToken(opToken);
//                break;
//            }
//            //System.out.println(">>curPrec " + curPrecedence + " " + opToken.str);
//            // curPrecedence > prevPrecedence
//            Expr rightExpr = readInfixExpr(nextToken(), curPrecedence);
//            leftExpr = new InfixExpr(opToken, leftExpr, rightExpr);
//            opToken = nextTokenWithSep();
//        }
//        return leftExpr;
//    }

    public Expr readInfixExpr(Token t, int prevPrecedence) {
        Expr firstExpr = readUnaryExpr(t);
        if (firstExpr.initTok.type == Token.Type.EOF) throw Util.runtimeException("EOF while reading");
        Token opToken = nextTokenWithSep();
        MultiInfixExpr mExpr = new MultiInfixExpr(firstExpr.initTok, opToken, firstExpr);
        for (; ; ) {
            int curPrecedence = getPrecedence(opToken.str);
            if (curPrecedence == -1 || prevPrecedence >= curPrecedence) {
                unreadToken(opToken);
                break;
            }
            Expr rightExpr = readInfixExpr(nextToken(), curPrecedence);
            mExpr.add(rightExpr);
            opToken = nextTokenWithSep();
            if (!opToken.str.equals(mExpr.opToken.str)) {
                mExpr = new MultiInfixExpr(mExpr.initTok, opToken, mExpr);
            }
        }
        return mExpr.getExpr();
    }

    public Expr readUnaryExpr(Token t) {
        if (t.type == Token.Type.META) {
            Expr meta = readExpr();
            if (meta.initTok.type == Token.Type.EOF) throw Util.runtimeException("EOF while reading");
            Expr expr = readExpr();
            if (expr.initTok.type == Token.Type.EOF) throw Util.runtimeException("EOF while reading");
            return new WithMetaExpr(t, meta, expr);
        }
        if (t.type == Token.Type.DEREF ||
                t.type == Token.Type.QUOTE ||
                t.type == Token.Type.UNQUOTE ||
                t.type == Token.Type.SYNTAX_QUOTE) {
            Expr nextExpr = readExpr();
            if (nextExpr.initTok.type == Token.Type.EOF) throw Util.runtimeException("EOF while reading");
            return new UnaryExpr(t, nextExpr);
        }
        if (t.type == Token.Type.SYMBOL &&
                (t.str.equals("not")) ||
                (t.str.equals("throw"))) {
            Expr nextExpr = readExpr();
            if (nextExpr.initTok.type == Token.Type.EOF) throw Util.runtimeException("EOF while reading");
            return new UnaryExpr(t, nextExpr, true);
        }
        //return readBaseExpr(t);
        return readFunctionCallExpr(t);
    }

    public Expr readFunctionCallExpr(Token t) {
        Expr e = readBaseExpr(t);
        while (e != null && e.callable) {
            Token nextTok = nextToken();
            if (nextTok.type != Token.Type.LPAREN) {
                throw Util.sneakyThrow(new ParserException("After callable should be '('", nextTok));
            }
            DelimitedListResult d = readDelimitedList(Token.Type.RPAREN);
            e = new FunctionCallExpr(e, d.a, d.last.initTok.callable);
        }
        return e;
    }

    public Expr readBaseExpr(Token t) {
        if (t.type == Token.Type.REGEX
                || t.type == Token.Type.NUMBER
                || t.type == Token.Type.STRING
                || t.type == Token.Type.KEYWORD
                || t.type == Token.Type.SYMBOL
                || t.type == Token.Type.BOOL
                || t.type == Token.Type.NULL
                || t.type == Token.Type.CHAR
                || t.type == Token.Type.SYMBOLIC_VALUE
                || t.type == Token.Type.RAW
        ) {
            return new SimpleExpr(t, t.callable);
        }
        if (t.type == Token.Type.LCURLY) {
            DelimitedListResult res = readDelimitedList(Token.Type.RCURLY);
            if (res.a.size() % 2 != 0) {
                throw Util.sneakyThrow(new ParserException("Map literal must have even number of forms", t));
            }
            return new MapExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.NAMESPACE_MAP) {
            DelimitedListResult res = readDelimitedList(Token.Type.RCURLY);
            if (res.a.size() % 2 != 0) {
                throw Util.sneakyThrow(new ParserException("Map literal must have even number of forms", t));
            }
            return new MapExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.CONDITIONAL) {
            DelimitedListResult res = readDelimitedList(Token.Type.RPAREN);
            if (res.a.size() % 2 != 0) {
                throw Util.sneakyThrow(new ParserException("Conditional must have even number of forms", t));
            }
            return new ConditionalExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.LBRACE) {
            DelimitedListResult res = readDelimitedList(Token.Type.RBRACE);
            return new VectorExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.SET) {
            DelimitedListResult res = readDelimitedList(Token.Type.RCURLY);
            return new SetExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.LPAREN) {
            DelimitedListResult res = readDelimitedList(Token.Type.RPAREN);
            return new ParensExpr(t, res.a, res.last.initTok.callable);
        }
        if (t.type == Token.Type.EOF){
            throw Util.runtimeException("EOF while reading");
        }
        Util.sneakyThrow(new ParserException("Unexpected token: " + t.str, t));
        return null;
    }

    public Expr readIfElse(Token initToken) {
        ArrayList<Expr> ifExprs;
        ArrayList<Expr> elseExprs = null;
        Expr condExpr = readExpr();
        if (condExpr == EofExpr.EOF_EXPR) {
            throw Util.runtimeException("EOF while reading");
        }
        // reading if clause
        Token nextToken = nextToken();
        if (nextToken.type == Token.Type.EOF) {
            throw Util.runtimeException("EOF while reading");
        }
        if (nextToken.type == Token.Type.COLON) {
            ifExprs = readBlock(initToken);
        } else {
            unreadToken(nextToken);
            Expr e = readExpr();
            if (e == EofExpr.EOF_EXPR) throw Util.runtimeException("EOF while reading");
            ifExprs = new ArrayList<>();
            ifExprs.add(e);
        }
        // reading else clause
        // FIXED: error when block , because block return unreadExpr() but here is nextToken
        nextToken = nextToken();
        // System.out.println(">>> nextToken, expect else: " + nextToken);
        if (nextToken.type == Token.Type.SYMBOL && nextToken.str.equals("else")) {
            nextToken = nextToken();
            if (nextToken.type == Token.Type.COLON) {
                elseExprs = readBlock(initToken);
            } else {
                unreadToken(nextToken);
                Expr e = readExpr();
                if (e == EofExpr.EOF_EXPR) throw Util.runtimeException("EOF while reading");
                elseExprs = new ArrayList<>();
                elseExprs.add(e);
            }
        } else {
            unreadToken(nextToken);
        }
        return new IfElseExpr(initToken, condExpr, ifExprs, elseExprs);
    }


    public DefExpr readDef(Token initTok) {
        boolean isFunction = false;
        ArrayList<Expr> metas = new ArrayList<>();
        ArrayList<Expr> args = new ArrayList<>();
        ArrayList<Expr> body = new ArrayList<>();
        Token nameTok;
        for (; ; ) {
            nameTok = nextToken();
            if (nameTok.type != Token.Type.META) {
                break;
            }
            Expr expr = readExpr();
            metas.add(expr);
        }
        if (nameTok.type != Token.Type.SYMBOL) {
            throw Util.sneakyThrow(new ParserException("Bad 'def' declaration, expected symbol, not: " + nameTok.str,
                    nameTok));
        }
        Token colonOrLBrace = nextToken();
        if (colonOrLBrace.type != Token.Type.LPAREN && colonOrLBrace.type != Token.Type.COLON) {
            throw Util.sneakyThrow(new ParserException("Bad 'def' declaration, expected `(` or `:`, not: " + colonOrLBrace.str,
                    colonOrLBrace));
        }
        if (colonOrLBrace.type == Token.Type.LPAREN) {
            isFunction = true;
            DelimitedListResult d = readDelimitedList(Token.Type.RPAREN);
            args = d.a;
            colonOrLBrace = nextToken();
        }
        if (colonOrLBrace.type != Token.Type.COLON) {
            throw Util.sneakyThrow(new ParserException("Bad 'def' declaration, expected `:`, not: " + colonOrLBrace.str,
                    colonOrLBrace));
        }
        body = readBlock(initTok);
        DefExpr defExpr = new DefExpr(initTok, nameTok, isFunction, metas, args, body);
        // System.out.println("<END DefExpr> : " + defExpr.gen());
        return defExpr;
    }

    public TryCatchFinallyExpr readTryCatchFinally(Token initTok) {
        Token nextTok = nextToken();
        if (nextTok.type != Token.Type.COLON) {
            throw Util.sneakyThrow(new ParserException("Expected ':', but got: " + nextTok.str, nextTok));
        }
        ArrayList<Expr> body = readBlock(initTok);
        ArrayList<CatchExpr> catches = new ArrayList<CatchExpr>();
        for (; ; ) {
            nextTok = nextToken();
            if (nextTok.str.equals("catch") && nextTok.column == initTok.column) {
                CatchExpr e = readCatch(nextTok);
                catches.add(e);
            } else {
                break;
            }
        }
        ArrayList<Expr> finallyExpr = new ArrayList<Expr>();
        if (nextTok.str.equals("finally") && nextTok.column == initTok.column) {
            /*if (nextTok.column != initTok.column) {
                throw Util.sneakyThrow(new ParserException("Finally keyword have to be at same column as try exception, at: "
                        + nextTok.line + ", " + nextTok.column));
            }*/
            Token finallyTok = nextTok;
            nextTok = nextToken();
            if (nextTok.type != Token.Type.COLON) {
                throw Util.sneakyThrow(new ParserException("Expected ':', but got:" + nextTok.str, nextTok));
            }
            finallyExpr = readBlock(finallyTok);
        } else {
            unreadToken(nextTok);
        }
        TryCatchFinallyExpr tcfExpr = new TryCatchFinallyExpr(initTok, body, catches, finallyExpr);
        // System.out.println("<END TryCatchFinallyExpr> : " + tcfExpr.gen());
        return tcfExpr;
    }

    public CatchExpr readCatch(Token initTok) {
        Token exceptionType = nextToken();
        if (exceptionType.type != Token.Type.SYMBOL) {
            throw Util.sneakyThrow(new ParserException("Expected exception type, but got:" + exceptionType.str,
                    exceptionType));
        }
        Token exceptionName = nextToken();
        //System.out.println(">>> Catch exType: " + exceptionType.str + " " + exceptionType.type);
        //System.out.println(">>> Catch exName: " + exceptionName.str + " " + exceptionName.type);
        if (exceptionName.type != Token.Type.SYMBOL) {
            throw Util.sneakyThrow(new ParserException("Expected exception name, but got:" + exceptionName.str,
                    exceptionName));
        }
        Token nextToken = nextToken();
        if (nextToken.type != Token.Type.COLON) {
            throw Util.sneakyThrow(new ParserException("Expected ':', but got:" + nextToken.str, nextToken));
        }
        ArrayList<Expr> body = readBlock(initTok);
        return new CatchExpr(initTok, exceptionType, exceptionName, body);
    }


    public FnExpr readFn(Token initTok) {
        ArrayList<Expr> args = new ArrayList<>();
        ArrayList<Expr> body = new ArrayList<>();
        for (; ; ) {
            Token nextToken = nextToken();
            if (nextToken.type == Token.Type.EOF) {
                throw Util.runtimeException("EOF while reading");
            }
            if (nextToken.type == Token.Type.COLON) {
                break;
            }
            unreadToken(nextToken);
            Expr e = readExpr();
            if (e == EofExpr.EOF_EXPR) {
                throw Util.runtimeException("EOF while reading");
            }
            args.add(e);
        }
        body = readBlock(initTok);
        FnExpr fnExpr = new FnExpr(initTok, args, body);
        return fnExpr;
    }

    public ControlExpr readFlatCotrol(Token initTok, Controls.Type type) {
        //ArrayList<Expr> args = readBlock(initTok);
        ArrayList<Expr> args = new ArrayList<Expr>();
        for (; ; ) {
            Token nextToken = nextToken();
            if (nextToken.type == Token.Type.COLON) {
                break;
            }
            if (nextToken.type == Token.Type.EOF) {
                throw Util.runtimeException("EOF while reading");
            }
            unreadToken(nextToken);
            Expr e = readExpr();
            args.add(e);
        }
        ArrayList<Expr> body = readBlock(initTok);
        return new ControlExpr(initTok, args, body, type);
    }

    public NsExpr readNS(Token initTok) {
        ArrayList<Expr> requires = null;
        ArrayList<Expr> imports = null;
        ArrayList<Expr> flat = new ArrayList<>();
        ArrayList<Expr> map = new ArrayList<>();
        ArrayList<Expr> vec = new ArrayList<>();
        Expr nameExpr = readExpr();
        if (nameExpr == EofExpr.EOF_EXPR) {
            throw Util.runtimeException("EOF while reading");
        }

        Token nextToken = nextToken();

        ArrayList<String> names = new ArrayList<>(List.of("require", "import", "map", "flat", "vec"));
        Controls.reset();

        while (names.contains(nextToken.str)) {
            Token colonToken = nextToken();
            if (colonToken.type != Token.Type.COLON) {
                throw Util.sneakyThrow(new ParserException("Expected ':', but got: " + colonToken.str, colonToken));
            }
            switch (nextToken.str) {
                case "require":
                    requires = readBlock(nextToken);
                    break;
                case "import":
                    imports = readBlock(nextToken);
                    break;
                case "vec":
                    vec = readBlock(nextToken);
                    break;
                case "flat":
                    flat = readBlock(nextToken);
                    break;
                case "map":
                    map = readBlock(nextToken);
                    break;
            }
            nextToken = nextToken();
        }
        unreadToken(nextToken);
        // System.out.println(map);
        // System.out.println(vec);
        // System.out.println(flat);
        for (var e : map) Controls.addMap(e.gen());
        for (var e : vec) Controls.addVec(e.gen());
        for (var e : flat) Controls.addFlat(e.gen());

//        Token nextToken = nextToken();
//        if (nextToken.str.equals("require")) {
//            Token colonToken = nextToken();
//            if (colonToken.type != Token.Type.COLON) {
//                throw Util.sneakyThrow(new ParserException("Expected ':', but got: " + colonToken.str +
//                        " , at: " + colonToken.line + ", " + colonToken.column));
//            }
//            requires = readBlock(nextToken);
//        } else {
//            unreadToken(nextToken);
//        }
//        nextToken = nextToken();
//        if (nextToken.str.equals("import")) {
//            Token colonToken = nextToken();
//            if (colonToken.type != Token.Type.COLON) {
//                throw Util.sneakyThrow(new ParserException("Expected ':', but got: " + colonToken.str +
//                        " , at: " + colonToken.line + ", " + colonToken.column));
//            }
//            imports = readBlock(nextToken);
//        } else {
//            unreadToken(nextToken);
//        }


        NsExpr nsExpr = new NsExpr(initTok, nameExpr, requires, imports);
        // System.out.println(nsExpr);
        return nsExpr;
    }

    public ArrayList<Expr> readBlock(Token initTok) {
        ArrayList<Expr> body = new ArrayList<>();
        addIndentation(initTok.column);
        // System.out.println(">>> add indentation def: " + indentation + "  initTok.column: " + initTok.column);
        Expr firstExpr = readExpr();
        if (firstExpr.initTok.type == Token.Type.EOF) {
            throw Util.runtimeException("EOF while reading");
        }
        /*if (firstExpr.initTok.line == initTok.line) {
            body.add(firstExpr);
            return body;
        }*/
        if (firstExpr.initTok.column <= initTok.column) {
            throw Util.sneakyThrow(new ParserException("Bad indentation, for '"
                    + firstExpr.initTok.str + "', should be nested to: " + initTok.str, firstExpr.initTok));
        }
        body.add(firstExpr);
        addIndentation(firstExpr.initTok.column);
        //System.out.println(">>> add indentation first-tok: " + indentation + "  initTok.column: " + firstExpr.initTok.column
        //        + ", '" + firstExpr.gen() + "'");
        //System.out.println(">>> indentation: " + indentation + " " + firstExpr.initTok.column + " " + firstExpr.gen());
        Expr prevExpr = firstExpr;
        boolean second = true;
        for (; ; ) {
            Token nextToken = nextToken();
            //System.out.println("NextExpr: " + nextToken.column + " " + initTok.column + " " + nextToken.toString() + "'");
            if (nextToken.type == Token.Type.EOF ||
                    nextToken.type == Token.Type.COLON ||
                    nextToken.type == Token.Type.RPAREN ||
                    nextToken.type == Token.Type.RBRACE ||
                    nextToken.type == Token.Type.RCURLY
            ) {
                //System.out.println("NextExpr EOF: " + nextToken);
                unreadToken(nextToken);
                break;
            } else if (nextToken.column == firstExpr.initTok.column ||
                    (nextToken.line == prevExpr.initTok.line && nextToken.line != initTok.line)
                //  ||                    ((nextToken.line == prevExpr.initTok.line) && !second)
            ) {
                unreadToken(nextToken);
                Expr nextExpr = readExpr();
                //System.out.println("NextExpr add: " + nextExpr.gen());
                body.add(nextExpr);
                prevExpr = nextExpr;
            } else if (nextToken.column > firstExpr.initTok.column && nextToken.line != initTok.line) {
                //System.out.println("NextExpr throw: " + nextToken);
                Util.sneakyThrow(new ParserException("Bad indentation, greater than "
                        + firstExpr.initTok.column + ": " + nextToken.str, nextToken));
            } else /*if (!checkIndentation(nextToken.column)) {
                // System.out.println(">>: checkIndentation: " + indentation);
                // popIndentation();
                Util.sneakyThrow(new ParserException("Bad indentation for: '" + nextToken.str
                        + "', available: " + indentation.toString() + ", but get: " + nextToken.column + " "
                        + ", at line: " + nextToken.line
                        + ", column: " + nextToken.column));
                break;
            } else */ {
                //System.out.println(">>block unread: " + nextToken);
                unreadToken(nextToken);
                break;
            }
            second = false;
        }
        popIndentation();
        popIndentation();
        // System.out.println(">>> return Block 2: " + body);
        return body;
    }


    public class DelimitedListResult {
        public ArrayList<Expr> a;
        public Expr last;

        public DelimitedListResult(ArrayList<Expr> a, Expr last) {
            this.a = a;
            this.last = last;
        }
    }

    public DelimitedListResult readDelimitedList(Token.Type tokDelim) {
        Expr onReturnExpr = new Expr();
        ArrayList<Expr> a = new ArrayList<Expr>();
        for (; ; ) {
            Expr form = readExpr(tokDelim, onReturnExpr);
            if (form == EofExpr.EOF_EXPR) {
                throw Util.runtimeException("EOF while reading");
            } else if (form == onReturnExpr) {
                return new DelimitedListResult(a, form);
            }
            a.add(form);
        }
    }


    public ArrayList<Token> readAll() {
        ArrayList<Token> arr = new ArrayList<Token>();
        //Token t = LexerReader.read(charReader);
        Token t = lexerReader.read();

        curLine = t.line;
        if (t.column != lastIndentation()) {
            Util.runtimeException("EOF while reading character");
        }

        while (t.type != Token.Type.EOF) {
            arr.add(t);

            // t = LexerReader.read(charReader);
            t = lexerReader.read();
            if (t.type == Token.Type.COLON) {

            }

        }
        arr.add(t);
        return arr;

    }

    public static ArrayList<Expr> readString(String s) {
        ArrayList<Expr> arr = new ArrayList<Expr>();
        Parser p = new Parser(s);
        for (; ; ) {
            Expr e = p.readExpr();
            if (e == EofExpr.EOF_EXPR) break;
            if (e.initTok.column != 1) {
                Util.sneakyThrow(new ParserException("Top level expression should start at column 1, but got: '"
                        + e.initTok.str, e.initTok));
            }
            arr.add(e);
        }
        return arr;
    }

    public static String genStr(String s) {
        ArrayList<Expr> arr = readString(s);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.size() - 1; i++) {
            Expr e = arr.get(i);
            sb.append(e.gen());
            sb.append("\n\n");
        }
        if (!arr.isEmpty()) {
            sb.append(arr.get(arr.size() - 1).gen());
        }
        return sb.toString();
    }
}
