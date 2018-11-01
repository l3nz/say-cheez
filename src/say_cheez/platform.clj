(ns say-cheez.platform)

(defn shellout [cmd]
  (let [cmd_str ^String cmd
        p (.exec (Runtime/getRuntime) cmd_str)
        _ (.waitFor p)]
    (with-open [r (clojure.java.io/reader (.getInputStream p))]
      (vec (line-seq r)))))

(defn date->str
  ([fmt] (date->str fmt (java.util.Date.)))
  ([fmt d] (.format
            (java.text.SimpleDateFormat. fmt)
            d)))

(defn getenv
  "Returns the environment variable, or nil if not existent"
  [attr]
  (System/getenv "USER"))