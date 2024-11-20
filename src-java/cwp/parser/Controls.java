package cwp.parser;

import java.util.ArrayList;

public class Controls {

    public static ArrayList<String> FLAT = new ArrayList<>();
    public static ArrayList<String> VEC = new ArrayList<>();
    public static ArrayList<String> MAP = new ArrayList<>();

    public static ArrayList<String> CUSTOM_FLAT = new ArrayList<>();
    public static ArrayList<String> CUSTOM_VEC = new ArrayList<>();
    public static ArrayList<String> CUSTOM_MAP = new ArrayList<>();

    public enum Type {
        FLAT,
        VEC,
        MAP
    }

    static {
        FLAT.add("while");
        FLAT.add("case");
        FLAT.add("cond");
        FLAT.add("condp");
        FLAT.add("cond->");
        FLAT.add("cond->>");
        FLAT.add("locking");
        FLAT.add("time");
        FLAT.add("when");

        VEC.add("let");
        VEC.add("for");
        VEC.add("loop");
        VEC.add("doseq");
        VEC.add("dotimes");
        VEC.add("binding");
        VEC.add("with-open");
        VEC.add("with-redefs");
        VEC.add("with-local-vars");
        VEC.add("when-let");
        VEC.add("when-first");

        MAP.add("with-bindings");
        MAP.add("with-redefs-fn");
    }

    public static boolean isFlat(String s) {
        return FLAT.contains(s) || CUSTOM_FLAT.contains(s);
    }

    public static boolean isVec(String s) {
        return VEC.contains(s) || CUSTOM_VEC.contains(s);
    }

    public static boolean isMap(String s) {
        return MAP.contains(s) || CUSTOM_MAP.contains(s);
    }

    public static void addFlat(String s) {
        remove(s);
        CUSTOM_FLAT.add(s);
    }

    public static void addVec(String s) {
        remove(s);
        CUSTOM_VEC.add(s);
    }

    public static void addMap(String s) {
        remove(s);
        CUSTOM_MAP.add(s);
    }

    public static void remove(String s) {
        CUSTOM_FLAT.remove(s);
        CUSTOM_VEC.remove(s);
        CUSTOM_MAP.remove(s);
    }

    public static void reset() {
        CUSTOM_FLAT.clear();
        CUSTOM_VEC.clear();
        CUSTOM_MAP.clear();
    }
}
