(ns say-cheez.run
  (:require [say-cheez.core :refer [capture-build-env-to runtime capture-to]])
  (:gen-class))

(capture-build-env-to BUILD)

(capture-to CUSTOM {:a (say-cheez.core/now-as :datetime)
                    :b (say-cheez.core/now-as :date)})

(defn -main
  "This should be pretty simple."
  []
  (println "BUILD" BUILD)
  (println "CUSTOM:" CUSTOM)
  (println "PID:" (runtime :pid))
  (println "VM:" (runtime :vm))
  (println "Memory:" (runtime :mem)))

