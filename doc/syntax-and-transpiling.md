## Syntax and transpiling

### Function call

Function call is written without whitespaces between function name and parentheses:

```scala 
f()
```

is transpiled to

```clojure 
(f)
```

There can be chained function call:

```f()()``` => ```((f))```

Function call here can be considered not only in relation to function, but to Clojure macro ot other form.

### Comments

Comments are written in C/Java style. Example:
```scala
// comment
```


### Basic expressions

Basic expressions are transpiled to themselves:

| Type          | Expression   |  Transpiled  |   
|---------------|--------------|:------------:|
| **Boolean**   | true, false  | true, false  |
| **Null**      | nil          |     nil      |
| **Numbers**   | 1, 10.2, 3/4 | 1, 10.2, 3/4 |
| **String**    | "string"     |   "string"   |
| **Keywrod**   | :keyword     |   :keyword   |
| **Character** | \a, \newline | \a, \newline |
| **Symbol**    | symbol       |    symbol    |

### Data structures

Vectors, maps, sets are transpiled to themselves:

| Type       | Expression   |  Transpiled  |   
|------------|--------------|:------------:|
| **Map**    | {:a 1, :b 2} | {:a 1, :b 2} |
| **Vector** | [1, 2, 3]    |  [1, 2, 3]   |
| **Sets**   | #{1, 2}      |   #{1, 2}    |

Parentheses are used for grouping expressions and setting precedence,
so `list` can be declared as: `list(1, 2, 3)` => `(list 1 2 3)`

### Other expressions

There are other supported expressions.
Some of them can be considered as
unary operators (quote, syntax quote, unquote, var quote, deref):

| Type                   | Expression           |      Transpiled      |   
|------------------------|----------------------|:--------------------:|
| **Regular expression** | #"hello"             |       #"hello"       |
| **Symbolic value**     | ##Inf, ##-Inf, ##NaN | ##Inf, ##-Inf, ##NaN |
| **Var quote**          | #'some-var           |      #'some-var      |
| **Quote**              | 'symbol              |       'symbol        |
| **Syntax quote**       | `sym                 |         `sym         |
| **Unquote**            | ~a, ~@a              |       ~a, ~@a        |
| **Deref**              | @val                 |         @val         |
| **Meta**               | ^:hello c            |      ^:hello c       |
| **Conditional**        | #?(), #?@()          |     #?(), #?@()      |

### Additional expressions

There are two syntax keywords that can be used without parentheses: **throw** and **not**:

|           | Example                      |            Transpiled            |   
|-----------|------------------------------|:--------------------------------:|
| **throw** | `throw ex-info("My ex", {})` | `(throw (ex-info \"My ex\" {}))` |
| **not**   | `not val`                    |           `(not val)`            |

### Infix operators

| Operator                |       Description        |   
|-------------------------|:------------------------:|
| \|>,  \|>>              |           pipe           |
| or, and                 |         boolean          |
| =, ==, !=, >, <, >=, <= |        comparison        |
| +, -                    |     sum, subtraction     |
| *, /                    | multiplication, division |

Infix operators are used as *concatenators* - they are grouping two expressions into one.

`[1 2]` - vector of 2 expressions: `1` and `2`,

`[1 + 2]` - vector of 1 expression: `1 + 2`

### Optionality of separators
Keyword `to` and comma `,` are equivalents.
They are used as separators and in most cases can be omitted.

Expressions below are identical:

```scala 
[1, 2, 3]
[1 2 3]
[1 to 2 to 3]
[1 , ,, to to to 2 to 3]
````
If they are used in infix expression, they prevent *concatenation* of expressions, thus:

`[1 + 2]` - vector of 1 expression: `1 + 2`

`[1, +, 2}` - vector of 3 expressions: `1`, `+` and `2`


### Parentheses

If parentheses are not a part of a function call,
they are used for setting precedence:


```scala
10 * 2 + 3   // => 23   (+ (* 10 2) 3)
10 * (2 + 3) // => 50   (* 10 (+ 2 3))
```

and grouping expressions:
```scala 
(println("Hello"), println("World")) // => (do (println "Hello") (println "World"))

```

Thus, if there are more than one expression in parentheses, expression transpiles to `do` block.

### Code block

Code block is the main part that makes syntax indentation-based.

The base structure of code block consists of starting symbol (token),
expressions that are following starting symbol,
colon `:` and block expressions:

```
<starting-symbol> [<expr_1> <expr_2> ...]? :
    <block_expr_1>
    <block_expr_2>?
    ...
```
Block expression can be at the same line as a starting symbol:

``` 
<starting-symbol> [<expr_1> <expr_2> ...]? : <block_expr_1>
                                             <block_expr_2>
```
There can be multiple block expressions started from the second line:

```
<starting-symbol> [<expr_1> <expr_2> ...]? :
    <block_expr_1> <block_expr_2> <block_expr_3>
    <block_expr_4> <block_expr_5>
    ...
```
But if there is another expression in first line after first block expression -
this expression is not related to main block construct.
This enables to define, for example, vector of simple functions in one line:
```scala
[fn x: x + 1, fn x: x * 2] // => [(fn [x] (+ x 1)) (fn [x] (* x 2))]
```
Comparing with:
```scala
[fn x: 
  x + 1, fn x: x * 2] // => [(fn [x] (+ x 1) (fn [x] (* x 2)))]
```

Code blocks are used in various code structures (constructs), such as `ns` declaration, `if`-`else`, `let` and others.

### Special structures

There are several structures that are handled by parser to facilitate declarations.
These structures are: `ns`, `def`, `if`-`else`, `try`-`catch`-`finally`, `fn` (`lambda`)
and they are started with `ns`, `def`, `if`, `try`, `fn` (or `lambda`) keywords respectively.

### `ns`

Namespace declaration starts with `ns` keyword followed by namespace name and optional
`require`, `import` blocks.

Example:
```scala
ns my-proj.core
    require:
        [clojure.str :as str]
        [clojure.walk :as walk]
        [clojure.edn :as edn]
    import: [com.some-proj SomeClass]
```
There can be also `flat`, `vec`, `map` blocks in `ns` declaration. More about them later.

### `def`

`def` is used to define top-level var.

Simple function declarations:
```scala
def add(x, y): x + y

def print-greeting(s):
  println(str("Hello, " s "!"))

def print-hi(): print("Hi")     
```

Simple declarations that can be considered as constants:

```scala
def hello: "Hello, world"

def error-code: -1
```

### `if-else`

Simple control flow structure example:
```scala 
if 10 > 5: 
  println("Yes")
else: println("No")
```

if there is only one expression in `if` or `else` blocks, colon `:` can be omitted:
```scala
if 10 > 5 println("Yes") else println("No")
```

### `try-catch-finally`

Example:
```scala
try:
  10 / 0
catch SQLException e:
  println("SQL error!")
catch Exception e: 
  println("Division by zero!")
finally:
  println("The end")
```
There can be one or more `catch` branches, `finally` branch is optional.

### `fn`

`fn` and `lambda` are equivalents and are used to define anonymous functions.

Example:
```scala
map(fn x: x + 2, [1,2,3,4,5])   // => (3 4 5 6 7)  (map (fn [x] (+ x 2)) [1 2 3 4 5])

filter(lambda x: x > 3, [1,2,3,4,5])  // => (4 5)  (filter (fn [x] (> x 3)) [1 2 3 4 5])
```

### Other structures

There are many structures that match code block declaration:
```
<starting-symbol> [<expr_1> <expr_2> ...]? :
    <block_expr_1>
    <block_expr_2>?
    ...
```
Such structures are `let`, `case`, `with-bindings` and others.

They differ in Clojure code generation.

### `flat`-structures

*Flat* structures are resulted in simple Clojure code generation
(without `[]` and `{}`).

Example:
```scala 
case val:
  1 to println("one")
  2 to println("tow")
  println("Something else")
```
Corresponding generated Clojure code (formatted):
```clojure
(case val
  1 (println "one") 
  2 (println "tow")
  (println "Something else"))
```
### `vec`-structures
*Vec* structures are resulted in Clojure code generation with brackets `[]`.

Example:
```scala 
dotimes i to 10:
  println("Iteration:", i)
```
Corresponding generated Clojure code (formatted):
```clojure
(dotimes [i 10]
  (println "Iteration:" i))
```
Here expressions before colon: `i` and `10` are inserted into brackets: `[i 10]`.


### `map`-structures

*Map* structures are resulted in Clojure code generation with braces `{}`.

Example:
```scala 
with-bindings #'x to 1:
  println("x:", x)
```
Corresponding generated Clojure code (formatted):
```clojure
(with-bindings {#'x 10} 
  (println "x:" x))
```
Here expressions before colon: `#'x` and `1` are inserted into curly braces: `{#'x 10}`.

### Table of structures

| `flat`                                                         | `vec`                                                                                                                                                                    |             `map`             |   
|----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------:|
| `while`, <br/>`case`, <br/>`cond`, <br/>`condp`, <br/>`cond->`, <br/>`cond->>`, <br/>`locking`, <br/>`time`, <br/>`when` | `let`, <br/>`for`,<br/>`loop`, <br/>`doseq`, <br/>`dotimes`, <br/>`binding`, <br/>`with-open`, <br/>`with-redefs`, <br/>`with-local-vars`, <br/>`when-let`, <br/>`when-first` | `with-bindings`, <br/>`with-redefs-fn` |

### Custom constructs

It's possible to use a function or a macro from another namespace
in a `flat`, `vec` or `map` style with indentation block.

To do that, there are `flat`, `map` or `vec` blocks in a namespace declaration.
For example, to use built-in Clojure `str` function with that style:

```scala 
ns example.core
  flat: str

str "Hello":
  "John"
  "Angela"
  "Smith"  
```

`flat`-block here is for transpiler. Generated Clojure code (formatted):

```clojure 
(ns example.core)

(str "Hello" "John" "Angela" "Smith")
```

### Clojure code
Sometimes it can be necessary to insert Clojure code.
Supposing, there is a library that has function `to`
(`to`keyword is used as separator) and it needs to be used.
For that purpose there are triple quotes:

```scala 
"""to"""(arg1, arg2) // => (to arg1 arg2)
```
It's possible to insert big Clojure blocks:
```scala 
"""
(defn hello [user]
  (println "Hello," user))
"""

hello("User")
```