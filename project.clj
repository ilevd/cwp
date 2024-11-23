(defproject org.clojars.ilevd/cwp "0.1.1"
  :description "Indentation-based syntax for Clojure"
  :url "https://github.com/ilevd/cwp"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [zprint "1.2.9"]]

  :java-source-paths ["src-java"]

  :repl-options {:init-ns cwp.core}

  :eval-in-leiningen true)
