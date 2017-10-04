(ns mortimer.main-test
  (:require [clojure.test :refer :all]
            [mortimer.main :refer :all]))

(def folder-prefix "test/mortimer/")

(defn test-form
  [form filename]
  (reset! code-format strfmt)
  (let [forms [form]
        result (with-out-str (eval-string (str forms)))
        expected (slurp (str folder-prefix filename))]
    (is (= result expected))))

(deftest test-1
  (test-form '(+ 2 (+ 2 2)) "test-1"))

(deftest test-2
  (test-form '(.toUpperCase (apply str (seq "fred"))) "test-2"))
