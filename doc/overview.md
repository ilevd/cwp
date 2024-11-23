# Overview

The purpose of this document is to provide quick overview of some Clojure concepts
using CWP syntax.


```scala 
// this is a comment
// Simple literals
1 // long
"this is a string"
nil  // null is written as nil
true // boolean
:keyword // keyword is a object that contains a string itself, they are fast in equality tests
'symbol  // symbol is an identifier that is used to refer to something else (function, global var etc)
// Symbols can contain such characters: *, +, !, -, _, ', ?, <, >, = 
// Thus this is valid symbol: 'a*b+c!d-e_f'g?h>i<j=123

// Math expressions are written similarly as in other languages, 
// except that there are have to be whitespaces between operands and operator:
(1 + 20.0) * 20
(10 + 20) * (10 + 5) / 3

// Boolean expressions:
true and false
(not true) and true or false
10 > 5 and 20 > 3

// Pipe operators:
// |> insert left value as a first argument to right expression, 
// |>> inserts left value as a last argument to right expression,
// 'str' function - concatenates provided arguments to one string,
// 'println' takes 1 argument here, so both operators can be used with it
"a" |> str("b") |> println    // prints "ab"
"a" |>> str("b") |>> println  // prints "ba"

// if-else control structure:
if true: println("Yes")
else: println("No")
// In Clojure nil and false are falsy values, and everything else is truthy
if 10 or "Hello":
  println("Yes")  // prints: Yes

// "loop" is used to make a cycle,
// "recur" is used to rebind values for new iteration
loop i to 0:     
  if i < 10:
    println(i)    // prints values from 0 to 9
    recur(i + 1)  // provide new increased value for new iteration

// There are rich set of built-in Clojure core functions and macros,
// so previous cycle can be written with `dotimes`
dotimes i to 10:
  println(i)

// To bind symbols with values 'let' can be used, 
// this can be considered as declaring new local vars
let a to 10,
    b to 20:
  a + b     

// Base Clojure collections are: list, vector, map and set.
// They are immutable and persistent, so adding/removing/replacing a value creates new collection
// Creating a list and calling some functions on it:
let xs to list(1, 2, 3):
  println(conj(xs, 4)) // (4 1 2 3)   
  println(rest(xs))    // (2 3)
  println(xs)          // (1 2 3)

// Creating a vector and calling some functions on it:
let v to [1, 2, 3]:
  println(conj(v, 4)) // [1 2 3 4] 
  println(rest(v))    // (2 3 4)
  println(v)          // [1 2 3]

// Creating a map and calling some functions on it:
let m to {:name "John",
          :age  32}:
  println(keys(m))                             // (:name :age)
  println(vals(m))                             // (John 32)
  println(assoc(m, :job, :developer))          // {:name John, :age 32, :job :developer}
  println(dissoc(m, :age))                     // {:name John}
  println(merge(m, {:address "Some Street"}))  // {:name John, :age 32, :address Some Street}
  println(m)                                   // {:name John, :age 32}

// Creating a set and calling some functions on it:
let st to #{1, 2, 3}:
  println(conj(st, 4))   // #{1 4 3 2}  
  println(conj(st, 2))   // #{1 3 2}
  println(st)            // #{1 3 2}

// Most of Clojure sequence functions can be applied to different collection types:
let v to [1, 2, 3, 4, 5],
    m to {:name "John", :age 32}:
  println(first(v))                       // 1
  println(map(fn x: x + 1, v))            // (2 3 4 5 6)
  println(filter(fn x: x > 3, v))         // (4 5)
  // prints first map entry of map which consists of key an value
  println(first(m))                       // [:name John]
  // [k, v] - is destructuring for map entry
  println(map(fn [k, v]: str(k, v), m))   // (:nameJohn :age32) 

// It's useful to use '|>>' when processing sequences:
range(10)                 // creating sequence: (0 1 2 3 4 5 6 7 8 9)
|>> map(fn x: x * x)      // (0 1 4 9 16 25 36 49 64 81)
|>> filter(fn x: x > 20)  // (25 36 49 64 81)
|>> group-by(even?)       // {false [25 49 81], true [36 64]}

// Destructuring is useful feature to get values from nested data structure,
// some examples:
let [h & t] to [1, 2, 3, 4]:
  println(h)    //  1
  println(t)    //  (2 3 4)

let {:keys [age, name]} to {:name "John", :age 32}:
  println(name)  // John
  println(age)   // 32

// Clojure has good support for concurrency and parallelism, here are some examples below.
// To have shared state in multithread environment 'atom' can be used:
let *a to atom(0):         // create a reference type that contains initial value 0
  dotimes _ to 100:        // 100 times
    future(swap!(*a, inc)) // run a thread that increment initial value
  Thread/sleep(100)        // wait some time for all threads finish
  println(@*a)             // @*a is a short syntax for deref(*a) for getting value from atom

// To apply f for coll in parallel, 'pmap' can be used:
pmap(fn x: x * x, range(10))  // (0 1 4 9 16 25 36 49 64 81)
```
## What's next?

* [clojure.org](https://clojure.org/) - to read more about Clojure
* [Syntax and transpiling](syntax-and-transpiling.md) - to read more about CWP syntax and transpiling