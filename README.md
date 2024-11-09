# cwp

![alt words-count](doc/imgs/words-count.png)


[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.ilevd/cwp.svg)](https://clojars.org/org.clojars.ilevd/cwp)

Indentation-based syntax for Clojure.

The first place to land for those who would like to deep into practical dynamic functional programming language - Clojure,
but **don't like parentheses**. It provides familiar syntax, so it's easy to switch to it, from e.g. Python.

Features:
* Indentation-based, Python-like syntax
* Easy to write math operations
* In most cases separators: `,` - comma and `to`- keyword are optional
* Readable Clojure code generation

Being just a syntax for Clojure, it provides what Clojure has:
* Functional programming - immutable data structures, higher-order functions...
* Concurrent primitives
* Clojure/Java (JVM) ecosystem with a lot of libraries

In a nutshell it's a transpiler and a Leiningen plugin.

## Example
Data manipulation 

![alt users](doc/imgs/users.png)


## Documentation
* [Overview](doc/overview.md)


## Usage

Add to `project.clj` :plugins section:
```edn
  [cwp "<actual_version>"]
```

Add to `project.clj` builds info:
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

After that you can compile Clojure code to `.jar`.


## License

Copyright © 2024 ilevd