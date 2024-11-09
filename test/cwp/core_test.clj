(ns cwp.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            )
  (:import (cwp.lexer LexerReader)
           (cwp.parser Parser)))


(defn gen [s]
  (time
    (try
      (let [p (Parser. s)]
        (.gen (.readExpr ^Parser p)))
      (catch Exception e (prn e)))))


(defn read-eval [s]
  (-> s gen read-string eval))


(defn eq [s1 s2]
  (= (str/replace s1 #"\s+" "")
    (str/replace s2 #"\s+" "")))

(deftest test
  (testing "Testing"

    (is (= (gen "[to , to , to to ,,]")
          "[]"))

    (is (= (gen "[1 1.0 1e5 1/3 -5]")
          "[1 1.0 1e5 1/3 -5]"))

    (is (= (gen "[\\a \\b \\newline \\space \"\" \"hello\"]")
          "[\\a \\b \\newline \\space \"\" \"hello\"]"))

    (is (= (gen "[nil true false ##Inf ##NaN ##-Inf]")
          "[nil true false ##Inf ##NaN ##-Inf]"))

    (is (= (gen "[#\"\"  #\"regex\"]")
          "[#\"\" #\"regex\"]"))

    (is (= (gen "[#?() #?(:clj 1) #?@() #?@(:clj [1 2 3])]")
          "[#?() #?(:clj 1) #?@() #?@(:clj [1 2 3])]"))

    (is (= (gen "[#{} #{1} {} {1 2} [] #{[] {} #{}}]")
          "[#{} #{1} {} {1 2} [] #{[] {} #{}}]"))

    (is (= (gen "[#::{}  #::{1 2}  #:hello{} #:hello{:a 1}]")
          "[#::{} #::{1 2} #:hello{} #:hello{:a 1}]"))

    (is (= (gen "[@a 'a `a ~a ~@a]")
          "[@a 'a `a ~a ~@a]"))

    (is (= (gen "[:a :a:a]")
          "[:a :a:a]"))

    (is (= "{:a (+ (- 10 20) 5)}" (gen "{:a to 10  - 20 + 5}")))
    (is (= "{:a 10, - (+ 20 5)}" (gen "{:a to 10,  - 20 + 5}")))
    (is (= "{:a 10, - 20, + 5}" (gen "{:a to 10,  - 20, + 5}")))
    ))


(deftest unary-test
  (testing "Unary testing"
    (is (eq "@a" (gen "@a")))
    (is (eq "'a" (gen "'a")))
    (is (eq "`a" (gen "`a")))
    (is (eq "~a" (gen "~a")))
    (is (eq "~@a" (gen "~@a")))
    (is (eq "(throw a)" (gen "throw a")))
    (is (eq "(not a)" (gen "not a")))
    (is (eq "^:dynamic ^:const a" (gen "^:dynamic ^:const a")))
    (is (eq "^:dynamic ^:const ^{:a 1} a" (gen "^:dynamic ^:const ^{:a 1} a")))))


(deftest infix-test
  (testing "Infix testing"
    (is (= "(+ 1 2)" (gen "1 + 2")))
    (is (= "(- (+ 1 2) 3)" (gen "1 + 2 - 3")))
    (is (= "(* (+ 5 5) (- 4 2))" (gen "(5 + 5) * (4 - 2)")))
    (is (= "(+ (* 1 2) (* 3 4))" (gen "1 * 2 + 3 * 4")))
    ; (is (= "(+ (+ (* 1 2) (* 3 4)) (* 5 6))" (gen "1 * 2 + 3 * 4 + 5 * 6")))
    (is (= "(+ (* 1 2) (* 3 4) (* 5 6))" (gen "1 * 2 + 3 * 4 + 5 * 6")))
    (is (= "(+ 1 (* 2 3) (* 4 5) 6)" (gen "1 + 2 * 3 + 4 * 5 + 6")))
    (is (= "(+ (- (+ (- (+ 10 4) 3) 5) 2) 5)" (gen "10 + 4 - 3 + 5 - 2 + 5")))

    (is (= "(and (+ 10 20) (+ 4 5))" (gen "10 + 20 and 4 + 5")))
    ;; (is (= "(-> (-> (-> (-> s f1) (+ f2 d)) f4) f5)" (gen "s |> f1 |> f2 + d |> f4 |> f5 ")))
    (is (= "(-> s f1 (+ f2 d) f4 f5)" (gen "s |> f1 |> f2 + d |> f4 |> f5 ")))

    ))


(deftest def-test
  (testing "def testing"
    (is (= "(def a 10)" (gen "def a: 10")))
    (is (= "(def a 10 20)" (gen "
def a:
  10
  20
  ")))
    (is (eq "(def ^:const a 10)" (gen "def ^:const a: 10")))
    (is (eq "(def ^:const ^:dynamic a 10)" (gen "def ^:const ^:dynamic a: 10")))
    (is (eq "(defn a [] 10)" (gen "def a(): 10")))
    (is (eq "(defn a [x] x)" (gen "def a(x): x")))
    (is (eq "(defn a [x y] (+ x y))" (gen "def a(x, y): x + y")))
    (is (eq "(defn f [x [a b]] (+ a b))" (gen "def f(x, [a, b]): a + b")))
    (is (eq "(defn ^{:a 1} f [x [a b]] (+ a b))" (gen "def ^{:a 1} f(x, [a, b]): a + b")))
    ))


(deftest if-else-test
  (testing "if-else testing"
    (is (= "(if a 10)" (gen "if a 10")))

    (is (= "(if a (do 10 20))" (gen "
if a:
 10
 20")))

    (is (= "(if a 10 40)" (gen "if a 10 else 40")))

    (is (= "(if a 10 (do 20 40))" (gen "
if a 10
else:
  20
  40")))

    (is (= "(if a (do 10 20) (do 40 50))"
          (gen "
if a:
   10
   20
else:
        40
        50")))

    (is (eq "((if a f))" (gen "(if a f)()")))
    (is (eq "((if a f1 f2))" (gen "(if a f1 else f2)()")))
    ))


(deftest try-catch-finally-test
  (testing "Try Testing"
    (is (= "(try 10)" (gen "try: 10")))

    (is (eq "(try 10 (catch Exception e 20))" (gen "
try: 10
catch Exception e: 20")))

    (is (eq "(def a
                (try (println \"Hello\")
                     (+ 1 1)
                (catch Exception e (println \"Err\"))
                (catch Exception e
                   (println \"Err\")
                   (println \"Err\"))
                (finally 100)))"
          (gen "
def a:
  try:
    println(\"Hello\")
    1 + 1
  catch Exception e:
    println(\"Err\")
  catch Exception e:
    println(\"Err\")
    println(\"Err\")
  finally:
     100")))
    ))


(deftest fn-test
  (testing "Fn testing"
    (is (eq "(fn [] (println))" (gen "fn: println()")))
    (is (eq "(fn [[w n]] (println w n))" (gen "fn [w, n]: println(w, n))")))
    (is (eq "((fn [x] (println (+ x 1))) 20)" (gen "(fn x: println(x + 1))(20)")))
    (is (eq "[(fn [x] x) (fn [y] (+ y 1))]" (gen "[fn x: x, fn y: y + 1]")))
    ))


(deftest controls-test
  (testing "Controls testing"
    (is (eq "(let [a 10 b 20] (* 10 20))" (gen "let a to 10, b to 20: 10 * 20")))
    (is (eq "(let [a 10 b 30 c 40] (+ a (* b c)))" (gen "
let a to 10, b to 30, c to 40:
    a + b * c")))
    (is (eq "(let [a (case 20 10 11 20 12)] (println \"a:\" a))"
          (gen "
let a to case 20:
           10 11
           20 12:
    println(\"a:\", a)
    ")))
    (is (eq "(while 20 30)" (gen "while 20: 30")))
    (is (eq "(with-redefs-fn {a 10 b 20} (+ a b))" (gen "with-redefs-fn a to 10 b to 20: a + b")))
    ))


(deftest nstest
  (testing "NST Testing"
    (is (eq "(ns my-server.core)" (gen "ns my-server.core")))

    (is (eq "(ns my-server.core\n(:require [clojure.str :as str]))"
          (gen "
ns my-server.core
  require: [clojure.str :as str]")))

    (is (eq "(ns my-server.core\n(:require [clojure.str :as str]\n          [clojure.walk :as walk]))"
          (gen "
ns my-server.core
  require:
    [clojure.str :as str]
    [clojure.walk :as walk]
    ")))
    (is (eq "(ns my-server.core\n(:require [clojure.str :as str]\n          [clojure.walk :as walk]))"
          (gen "
ns my-server.core
  require: [clojure.str :as str]
           [clojure.walk :as walk]
    ")))



    (is (eq "(ns my-server.core
 (:require [clojure.str :as str]
           [clojure.walk :as walk]
           [clojure.edn :as edn]))"
          (gen "
ns my-server.core
  require:
    [clojure.str :as str]
    [clojure.walk :as walk]
    [clojure.edn :as edn]
    ")))

    (is (eq "(ns my-server.core\n(:import (com.clickhouse.client ClickHouseException)))"
          (gen "
ns my-server.core
  import: [com.clickhouse.client ClickHouseException]
    ")))

    (is (eq "(ns my-server.core\n(:import (com.clickhouse.client ClickHouseException)\n         (com.somePack SomeClass)))"
          (gen "
ns my-server.core
  import:
    [com.clickhouse.client ClickHouseException]
    [com.somePack SomeClass]
    ")))

    ))

(deftest comment-test
  (testing "Comment Testing"
    (is (= "20" (gen "
// this is comment
#! this is comment to
20 // comment
// yet another  comment
#! comment again
   ")))
    )
  )


(deftest complex
  (testing "Complex Testing"

    (is (= "[(not 10) (throw (Exception. \"asfd\"))]"
          (gen "[not 10 throw Exception.(\"asfd\")]")))

    (is (= "(clojure.lang.LineNumberingPushbackReader. (java.io.InputStreamReader. (.openStream url) \"UTF-8\"))"
          (gen "  clojure.lang.LineNumberingPushbackReader.(
                              java.io.InputStreamReader.(
                                                    .openStream(url), \"UTF-8\"))")))
    ))


(deftest readeval-test
  (testing "Read-eval testing"

    (is (= '(["hello" 2] ["hi" 1])
          (read-eval
            "(
        def wordsCount(s):
          s |> clojure.string/split(#\"\\s+\") |>> frequencies |>> sort-by(second, >)
        wordsCount(\"hello hi hello\") )")))

    (is (= 20 (read-eval
"(def f(x): x + 10 + 20 / 10
 f(8))"
                ))))
  )