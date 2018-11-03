(ns say-cheez.core-test
  (:require [clojure.test :refer :all]
            [say-cheez.core :refer :all]))

(def LEININGEN-PLAIN
  '(defproject
     say-cheez-a
     "3.2.1"
     :description
     "Takes a snapshot of the environment at build time."
     :url
     "https://github.com/l3nz/say-cheez"
     :license
     {:name "Eclipse Public License", :url "http://www.eclipse.org/legal/epl-v10.html"}
     :dependencies
     [[org.clojure/clojure "1.9.0" :scope "provided"]]
     :scm
     {:name "git", :url "https://github.com/l3nz/say-cheez"}
     :plugins
     [[lein-eftest "0.5.1"] [jonase/eastwood "0.2.5"] [lein-kibit "0.1.6"] [lein-cljfmt "0.5.7"]]
     :deploy-repositories
     [["clojars" {:sign-releases false, :url "https://clojars.org/repo"}]
      ["snapshots" {:sign-releases false, :url "https://clojars.org/repo"}]]))

(deftest leiningen-info-test
  (let [fnReader (constantly LEININGEN-PLAIN)]

    (is (= "say-cheez-a"
           (leiningen-info :project-name fnReader)))

    (is (= "3.2.1"
           (leiningen-info :version fnReader)))))
