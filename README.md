# cwp

![alt words-count](doc/imgs/words-count.png)

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.ilevd/cwp.svg)](https://clojars.org/org.clojars.ilevd/cwp)

Indentation-based syntax for [Clojure](https://clojure.org/).

Clojure is a practical dynamic functional programming language.
This project provides familiar syntax, so it's easy to switch to it, from e.g. Python.

Features:
* Indentation-based, Python-like syntax
* Easy to write math operations
* In most cases separators: `,` - comma and `to`- keyword are optional
* Readable Clojure code generation

Being just a syntax for Clojure, it provides access to Clojure features such as:
* Functional programming - immutable data structures, higher-order functions...
* Concurrent primitives
* Clojure/Java (JVM) ecosystem with a lot of libraries

It's a transpiler and a [Leiningen](https://leiningen.org/) plugin.

## Examples

FizzBuzz

```scala 
doseq i to range(1, 101):
  cond:
    mod(i, 3) = 0 and mod(i, 5) = 0 to print("FizzBuzz")
    mod(i, 3) = 0 to print("Fizz")
    mod(i, 5) = 0 to print("Buzz")
    :else print(i)
```

Caesar cipher

```scala 
def encode(^String s, ^long i):
  let sb StringBuilder.():
    doseq c s:
      cond:
        int(c) >= int(\a) and int(c) <= int(\z) 
        .append(sb, char(int(\a) + mod(int(c) - int(\a) + i, 26)))
        
        int(c) >= int(\A) and int(c) <= int(\Z) 
        .append(sb, char(int(\A) + mod(int(c) - int(\A) + i, 26)))
       
        :else .append(sb, c)
    .toString(sb)

def decode(^String s, ^long i):
  encode(s, 26 - i)
```


Data manipulation 

```scala 
def users: [{:name to "John",  :age to 20}
            {:name to "Anna",  :age to 32}
            {:name to "Smith", :age to 27}]

def avg-age(users):
  let ages to users |>> map(:age)
                    |>> reduce(+):
    ages / count(users) |> double

def greetings(users):
  let names to users |>> map(:name)
                     |>> str/join(", "):
    str("Hello, ", names, "!")

println(avg-age(users))
println(greetings(users))
```

Simple HTTP server with [http-kit](https://github.com/http-kit/http-kit),
[Hiccup](https://github.com/weavejester/hiccup) and [Ring](https://github.com/ring-clojure/ring)

```scala 
ns my-project.server
  require: [ring.middleware.params :as params]
           [ring.middleware.keyword-params :as kparams]
           [org.httpkit.server :refer [run-server]]
           [hiccup2.core :as h]

def fruits: ["Banana", "Apple", "Lemon"]

def get-html(user):
  [:div
    [:p {:style {:font-weight :bold}} str("Hello, ", user or "User", "!")]
    "Fruits:"
    for fruit to fruits:
      [:p {} fruit]]
  |> h/html |> str

def app(req):
 {:status  200
  :headers {"Content-Type" "text/html"}
  :body    get-html(req |> :params |> :name)}

run-server(app |> kparams/wrap-keyword-params 
               |> params/wrap-params,
           {:port 8080})
```

Some function from [clojure.core](https://github.com/clojure/clojure/blob/clojure-1.11.1/src/clj/clojure/core.clj#L7918)
rewritten with CWP

```scala
def load-data-reader-file(mappings, ^java.net.URL url):
  with-open rdr to clojure.lang.LineNumberingPushbackReader.(
                     java.io.InputStreamReader.(
                       .openStream(url), "UTF-8")):
    binding *file* to .getFile(url):
      let read-opts to if .endsWith(.getPath(url), "cljc"):
                         {:eof nil :read-cond :allow}
                       else {:eof nil}
          new-mappings to read(read-opts, rdr):
        if not map?(new-mappings):
          throw ex-info(str("Not a valid data-reader map"), {:url url})
        reduce(fn m, [k, v]:
                  if not symbol?(k):
                    throw ex-info(str("Invalid form in data-reader file"),
                                  {:url url
                                   :form k})
                  let v-var to data-reader-var(v):
                    if contains?(mappings, k) and mappings(k) != v-var:
                      throw ex-info("Conflicting data-reader mapping",
                                    {:url      url
                                     :conflict k
                                     :mappings m})
                    assoc(m, k, v-var),
               mappings,
               new-mappings)
```


## Documentation
* [Overview](doc/overview.md)
* [Syntax and transpiling](doc/syntax-and-transpiling.md)


## Usage

Add to `project.clj` :plugins section:
```edn
[org.clojars.ilevd/cwp  "<actual_version>"] 
```

Add builds info to `project.clj` root:
```edn 
:cwp {:builds [{:in  "src-cwp"
                :out "src-out"}]}
```
`:in` - folder where CWP sources are,
`:out` - folder for generated Clojure code

Files extensions mapping:
* `.cw` -> `.clj`
* `.cws` -> `.cljs`
* `.cwc` -> `.cljc`

Run: `lein cwp`

After that you can compile Clojure code to `.jar`.


## License

Copyright © 2024 ilevd