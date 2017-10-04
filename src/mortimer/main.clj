(ns mortimer.main
  (:require [clojure.pprint :as pp]
            [clojure.test :refer [function?]]
            [clojure.core.incubator :refer [seqable?]]
            [clojure.walk :as w]
            [riddley.walk :refer [macroexpand-all walk-exprs]])
  (:gen-class))

(def code-format (atom nil))

(defn cljfmt 
  "Pretty print Clojure code using clojure.pprint."
  [form]
  (with-out-str
    (pp/write form :dispatch pp/code-dispatch)))

(defn strfmt 
  "Format for return values so nil isn't empty, and strings have quotes."
  [obj]
  (cond
    (= (type obj) java.lang.String) (str "\"" obj "\"")
    (nil? obj) "nil"
    :else (str obj)))

(defn remove-traces [form]
  (w/postwalk
    (fn [s] 
      (if (and (seqable? s) (= (first s) 'mortimer.main/trace))
        (second s)
        s))
     form))

(defmacro trace [form]
  `(let [res# ~form] (println "TRACED: " (@code-format (remove-traces '~form)) "=>" (strfmt res#)) res#))

(defn wrap-trace [sym]
  (cons 'mortimer.main/trace (list sym)))

(declare backwalk)

(defn dotwalk
  "Special handling of forms where the first element is the dot operator."
  [f form]
  (wrap-trace
    (concat
      (list '. (backwalk f (second form)))
      (let [rest-form (drop 2 form)
            fr-form (first rest-form)]
        (if (seqable? fr-form)
          (list (concat
            (list (first fr-form))
            (backwalk f (rest fr-form))))
          rest-form)))))

(defn backwalk
  "Like postwalk, but do special handling for certain forms."
  [f form]
  (if (and (seqable? form) (= '. (first form)))
    (dotwalk f form)
    (clojure.walk/walk (partial backwalk f) f form)))

(defn trace?
  [sym]
  (or
    (function? sym)
    (= sym '.)))

(defn add-traces 
  "Add tracing macros around every function."
  [form]
  (backwalk
    (fn [sym]
      (if (and (seqable? sym) (trace? (first sym)))
        (wrap-trace sym)
        sym))
      form))

(defn eval-string 
  "Evaluate a string by expanding macros and adding traces."
  [code-string]
  (clojure.main/with-bindings
    (let [forms (read-string code-string)]
      (doseq [form forms]
        (eval (add-traces (macroexpand-all form)))))))

(defn -main
  "Main entry point for the program."
  [filename]
  (reset! code-format strfmt)
  (println "Running file" filename "...")
  (eval-string (str "[" (slurp filename) "]")))
