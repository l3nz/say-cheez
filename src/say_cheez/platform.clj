(ns say-cheez.platform
  "## Platform (JVM)

   This file contains platform-specific calls for the Java VM.

  "

  (:require [clojure.string :as str]))

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

; ===============================================================
;
;                   R U N T I M E
;
; ===============================================================

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

(defn get-memory-state
  "Returns an object like  `{:used 10 :max 20 :other 5}`
  where:

  * used is the current memory used
  * max is the maximum allowed heap
  * other is ny extra memory allocated outside the heap.

  All values in bytes.
  "
  []
  (let [memBean (bean (java.lang.management.ManagementFactory/getMemoryMXBean))
        {hU :used hM :max}    (bean (:heapMemoryUsage memBean))
        {nU :used nM :committed} (bean (:nonHeapMemoryUsage memBean))]

    {:used hU
     :max hM
     :other (+ hM nM)}))

; ===============================================================
;
;                   T H R E A D S
;
; ===============================================================

(def THREAD-SEPARATOR " | ")

(defn current-thread-name
  "If the curren thtred has a name in the form *\"fullname | detail...\"*
  it return only the first part; otherwise returns it all.
  "

  [thread-name]
  (if (str/includes? thread-name THREAD-SEPARATOR)
    (subs thread-name 0 (str/index-of thread-name THREAD-SEPARATOR))

    thread-name))

(defn new-name-for-thread
  "Computes a new name for the thread, to be used in [[set-thread-name]]."
  [existing name detail]

  (cond
    ; if only the detail is set, we replace it
    (and (nil? name) (some? detail))
    (new-name-for-thread existing (current-thread-name existing) detail); all empty
    (and (nil? name) (nil? detail))
    ""

    ; detail empty
    (and (some? name) (nil? detail))
    name

    ; normal case
    :else
    (str name THREAD-SEPARATOR detail)))

(defn set-thread-name
  "Sets a new thread name.

  A thread nme has the form \"fullname | detail...\".
  If you set the name to nil, it will be inferred from
  the current thread name, so you can just change the detail.

  The one-arity form just replaces the detail.

  "
  ([detail]
   (set-thread-name nil detail))

  ([name detail]
   (let [currName (.getName ^Thread (Thread/currentThread))
         newName (new-name-for-thread currName name detail)]
     (.setName ^Thread (Thread/currentThread) newName))))

