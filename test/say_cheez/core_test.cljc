(ns say-cheez.core-test
  (:require [clojure.test :refer :all])
  (:require [say-cheez.core :refer [display-memory]]))

(defn mb [n]
  (* 1024 1024 n))


(deftest display-memory-test
  (are [u m t s]
    (= s (display-memory {:used u :max m :other t}))

    ; -
    (mb 10) (mb 20) (mb 30) "10/50M 50% used"

    ; - esempio reale.
    (mb 141) (mb 1500) (mb 90) "141/1590M 9% used"


    )




  )
