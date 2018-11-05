(ns say-cheez.platform-test
  (:require [clojure.test :refer :all])
  (:require [say-cheez.platform :refer [new-name-for-thread]]))

(deftest new-name-for-thread-test

  (are [x n d v]
       (= v (new-name-for-thread x n d))

    ; normal case
    "" "a" "b"  "a | b"
    "a | b" nil "cx"  "a | cx"
    "a | b" "x" "d"  "x | d"
    "a | b" nil nil  ""
    "a | b" "x" nil  "x"))
