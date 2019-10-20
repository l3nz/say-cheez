(ns say-cheez.run
  (:require [say-cheez.core :refer [capture-build-env-to
                                    runtime
                                    capture
                                    capture-to
                                    current-build-env]])
  (:gen-class))

;
; This is a simple stub to assist development.
;
; lein clean && lein uberjar
; java -jar ./target/say-cheez-*-standalone.jar
;


(capture-build-env-to BUILD)

(capture-to CUSTOM {:a (say-cheez.core/now-as :datetime)
                    :b (say-cheez.core/now-as :date)})

(def BUILD-DEF (current-build-env))
(def CUSTOM-DEF
  (capture {:a (say-cheez.core/now-as :datetime)
            :b (say-cheez.core/now-as :date)}
           "My custom capture"))

(defn -main
  "This should be pretty simple."
  []
  (println "BUILD" BUILD)
  (println "BUILD in DEF" BUILD-DEF)
  (println "CUSTOM:" CUSTOM)
  (println "CUSTOM-DEF:" CUSTOM-DEF)
  (println "PID:" (runtime :pid))
  (println "VM:" (runtime :vm))
  (println "Memory:" (runtime :mem)))

