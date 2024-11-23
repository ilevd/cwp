(ns leiningen.cwp
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [zprint.core :as zp])
  (:import (cwp.parser Parser)))


(def ^:dynamic ext-mappings {"cw"  "clj"
                             "cws" "cljs"
                             "cwc" "cljc"})

(defn ends-with-ext? [s]
  (->> (keys ext-mappings)
    (map #(str "." %))
    (some #(str/ends-with? s %))))

(defn ext-reg []
  (->> (keys ext-mappings)
    (str/join "$|")
    re-pattern))

(defn run [{:keys [in out]}]
  (doseq [f (file-seq (io/file in))
          :let [in-path (.getPath f)]]
    (when (ends-with-ext? in-path)
      (let [o              (-> in-path
                             (str/replace-first (re-pattern in) out)
                             (str/replace (ext-reg) ext-mappings))
            file-out       (io/file o)
            in-str         (slurp f)
            out-str        (Parser/genStr in-str)
            format-out-str (zp/zprint-file-str out-str in-path)]
        (.mkdirs (.getParentFile file-out))
        (spit file-out format-out-str)))))


(defn cwp [project & args]
  (let [builds (-> project :cwp :builds)]
    (doseq [b builds]
      (run b))))


(comment
  (run {:in  "src-e"
        :out "src"})
  )