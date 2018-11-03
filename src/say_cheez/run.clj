(ns say-cheez.run
  (:require [say-cheez.core :refer [capture-build-env-to runtime]])
  (:gen-class))

(capture-build-env-to BUILD)

(defn -main
  "This should be pretty simple."
  []
  (println "BUILD" BUILD)
  (println "PID:" (runtime :pid))
  (println "VM:" (runtime :vm))


  )

