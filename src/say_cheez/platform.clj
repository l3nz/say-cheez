(ns say-cheez.platform
  "## Platform (JVM)

   This file contains platform-specific calls for the Java VM.

  "

  )


(defn shellout
  "Runs a command defined as a single string.
  Returns stdout as a vector of lines."
  [cmd]
  (let [cmd_str ^String cmd
        p (.exec (Runtime/getRuntime) cmd_str)
        _ (.waitFor p)]
    (with-open [r (clojure.java.io/reader (.getInputStream p))]
      (vec (line-seq r)))))

(defn date->str
  "Converts a a date (or now) to a formatted string."
  ([fmt] (date->str fmt (java.util.Date.)))
  ([fmt d] (.format
            (java.text.SimpleDateFormat. fmt)
            d)))

(defn getenv
  "Returns the environment variable, or nil if not existent."
  [attr]
  (System/getenv attr))

(defn get-current-pid
  "Returns a string that describes the current PID.
  Usually like \"2779@Lenzs-MacBook-Pro-2.local\"
  "
  []
  (let [runtime (bean (java.lang.management.ManagementFactory/getRuntimeMXBean))]
    (:name runtime)))

(defn get-current-VM
  "Returns a description of current VM

  Ex. \"Oracle Corporation 25.181-b13 (Java 1.8)\""
  []
  (let [runtime (bean (java.lang.management.ManagementFactory/getRuntimeMXBean))]
    (str (:vmVendor runtime) " "
         (:vmVersion runtime) " "
         "(Java " (:specVersion runtime) ")")))

