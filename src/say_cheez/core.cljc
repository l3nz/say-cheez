(ns say-cheez.core
    (:require [say-cheez.platform :as P]))


(defn now-as
    [mode]
    (condp = mode
        :date  (P/date->str "yyyy-MM-dd")
        :time  (P/date->str "HH:mm:ss")
        :timec (P/date->str "HHmmss")
        :datetime (P/date->str "yyyy-MM-dd HH:mm:ss")
        ))



; git log --oneline -n 1 --pretty="%h - %aD - %aN"
; 87bac29 - Sun, 1 Oct 2017 18:19:38 -0400 - lenz
(defmacro capture-build-env-to
    "Captures build environment"
    [env-sym]
    (let [now (now-as :datetime)
          user (P/getenv "USER")
          nbuild (P/getenv "BUILD_NUMBER")
          gitbuild (first (P/shellout "git log --oneline -n 1 --pretty=%h.%aD.%aN"))
          ]
        `(defonce ~env-sym {:built-at ~now
                            :build-no ~nbuild
                            :git-build ~gitbuild
                            :build-by ~user})))




