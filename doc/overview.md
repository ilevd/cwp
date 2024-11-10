## Overview

### Simple primitives with examples

Boolean: `true`, `false`

Null: `nil`

Number: `1`, `1.0`, `1/3`, `0xF`, `1e4`, `012`

String: `"Hello\nworld"`

Character: `\a`, `\newline`

Keyword: `:name`, `::keyword`

Keyword (like `:name`) - is syntax sugar for creation an Object that contains the string itself. 
They are mostly used as keys in map objects, e.g.: 
```scala
{:name "John", :age 20}
```

### Infix operators

| Operator                |       Description        |   
|-------------------------|:------------------------:|
| \|>,  \|>>              |           pipe           |
| or, and                 |         boolean          |
| =, ==, !=, >, <, >=, <= |        comparison        |
| +, -                    |  addition, subtraction   |
| *, /                    | multiplication, division |


### Parentheses
Parentheses are used for setting precedence:
```scala
10 * 2 + 3   // => 23
10 * (2 + 3) // => 50
```

### Comments
C/Java-like:
```
// my comment
```


### Source file/namespace structure

Clojure source file, and thus CWP usually consists of namespace declaration in the beginning:

```scala
ns my-project.core
```
To import other namespaces, there is `:require` block. And to import Java classes - `:import`:
```scala
ns my-proj.core
    require:
        [clojure.str :as str]
        [clojure.walk :as walk]
        [clojure.edn :as edn]
    import: [com.some-proj SomeClass]
```
Below that declarations started with `def` usually follow.


### Data structures
List:
```scala 
list(1, 2, 3)
```

Vector:
```scala
[1, 2, 3]
```

Map:
```scala
{:name to "John",
 :age  to 20}
```

Set:
```scala 
#{1, 2, 3}
```

### Optionality of separators
Separators `to` and comma `,` are equivalents and in most cases can be omitted.
For example previous map data structure can be written simply as:
```scala 
{:name "John" 
 :age  20}
```
But in some cases they improve readability, e.g.:
```scala
let a to 10 + 20,
    b to a * 10:
  println(a, b)
```

And they also are necessary in cases where you want to break expression with operators.
Thus, this is a vector with one expression:
```scala
[10 + 20]
```
And this is a vector with 3 expressions:
```scala
[10, +,  20]
```
Despite `to` and comma `,` are equivalents, it's recommended to use:
* comma `,` to separate pairs in maps, values in vectors and sets etc.
* `to`to separate key and value in map pair, definitions in structures like `let` etc.

### Declarations
Simple function declarations:
```scala
def add(a, b): a + b

def print-greeting(s): 
  println(str("Hello, ", s, "!"))
    
def print-hi(): 
  print("Hi")      
```
When using without parentheses `def` defines top-level value, that can be considered as a constant, e.g.:

```scala
def hello: "Hello, world"

def error-code: -1
```
### Anonymous functions
`fn` and `lambda` are equivalents:
```scala 
map(fn x: x + 2, [1,2,3,4,5]) // => (3 4 5 6 7)

filter(lambda x: x > 3, [1,2,3,4,5]) // => (4 5)
```



### Control code structures

#### let
```scala
let a to 10, b to 20, c to a + b: println(c)
```

#### if-else
```scala
if 10 > 5: println("Yes")
else: println("No")
```
if there is only one expression after if/else colon `:` can be omitted:
```scala
if 10 > 5 println("Yes") else println("No")
```

#### try-catch
```scala 
try:
  10 / 0
catch Exception e: 
  println("Division by zero!")
```

#### case
```scala
case val:
  10 to println("ten")
  20 to println("twenty")
  println("Something else")
```

There are other structures as: `while`, `cond`, `condp`, `doseq`, `dotimes` etc.

You can use a function or a macro from another namespace in a style of a control structure.
To do that, add `flat`, `map` or `vec` block in a namespace declaration.
E.g. to use built-in Clojure `str` function with that style:

```scala
ns my-server.core
  flat: str

str "Hello":
  "John"
  "Angela"
  "Smith"  
```
Result will be: `"HelloJohnAngelaSmith"`

In general, when adding imported function `my-fn` to `flat` block, for code:
```scala
my-fn val1 val2:
  val3
  val4
```
* generated Clojure code will be:  `(my-fn val1 val2 val3 val4)`
* when using `vec` generated Clojure code will be: `(my-fn [val1 val2] val3 val4)`
* when using `map` generated Clojure code will be: `(my-fn {val1 val2} val3 val4)`

### Clojure code
Sometimes it can be necessary to insert Clojure code.
Supposing some library has function `to` (`to` is used as separator) and it needs to be used.
For that purpose there are triple quotes:

```scala 
def run(arg1, arg2):
  """to"""(arg1, arg2)
```
It's possible to insert big Clojure blocks:
```scala 
"""
(defn hello [user]
  (println "Hello," user))
"""

hello("User")
```