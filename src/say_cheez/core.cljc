(ns say-cheez.core
  "## Core

  This namespace is divided into two areas:

  * fns that capture some attribute of the environment ([[now-as]], [[env]], [[leiningen-info]] and [[git-info]])
  * macros that 'stick it together' at build time ([[capture-build-env-to]] that builds a
  simple default, and [[capture-to]] if you need finer control).

  Please note that anything in this namespace is supposed to be plain Clojure,
  so most functions reach out to something under [[say-cheez.platform]] that actually
  implements the basic functionality.

  Most functions are supposed to receive a single keyword that will act as a \"preset\"
  and tells them what is needed.


  "
    (:require [say-cheez.platform :as P]
              [clojure.edn :as edn]))

;;
;; Date/time
;;



(defn now-as
    "Prints the current date/time in different ways.

    * :date
    * :time
    * :timec   (compact)
    * :datetime

    "
    [mode]
    (condp = mode
        :date  (P/date->str "yyyy-MM-dd")
        :time  (P/date->str "HH:mm:ss")
        :timec (P/date->str "HHmmss")
        :datetime (P/date->str "yyyy-MM-dd.HH:mm:ss")
        ))


;;
;; Environment
;;

(defn env
  "Reads an environment value.
  Receives either a label or a vector of labels, and a default
  value.

  If there is more than one label, tries them in order.
  If no value found, returns defValue.

  e.g. `(env [\"A\" \"B\"] \"x\")`

  Will try *A*, then *B*, and in neither is defined will return *x*.

  "
  [lEnvVars defValue]
  (let [labels (if (string? lEnvVars)
                 [lEnvVars]
                 lEnvVars
                 )
        attrs (mapv P/getenv labels)
        attrs-with-default (conj attrs defValue)]

    (first (filter some? attrs-with-default))))



;;
;; Leiningen project
;;


(defn read-project-clj
    []
    (let [src (slurp "project.clj")]
        (edn/read-string src)))


(defn leiningen-info
    "Reads information from a leiningen project.

    * :projectt-name
    * :version

    Use the one-arity version.

    "
   ([tag]
     (leiningen-info tag read-project-clj))

    ([tag fnReader]
    (let [project (fnReader)
          [_ name version & opts] project]
        (condp = tag
            :project-name
            (str name)

            :version
            version))))

;;
;; GIT
;;

(defn gitlog [parms]
    (let [cmdline (str "git log --oneline -n 1 " parms)]

    (first (P/shellout cmdline))))


(defn git-info
    "Reads data from GIT. Git must be installed (we just shell-out).

    * :commit-id
    * :commit-long
    * :last-committer
    * :date
    * :date-compact
    * :all    - *abcdefg/20181103.1023*

    "

    [what]
    (condp = what
        :commit-id (gitlog "--pretty=%h")
        :commit-long (gitlog "--pretty=%H")
        :last-committer (gitlog "--pretty=%aN")

        :date    (gitlog "--date=format:%Y-%m-%d.%H:%M:%S --pretty=%cd")
        :date-compact (gitlog "--date=format:%Y%m%d-%H%M --pretty=%cd")
        :all   (str (git-info :commit-id) "/"
                    (git-info :date))
        ))




;; ======================================================
;; RUNTIME INFORMATION
;; ======================================================

(defn safe-quot
  "Integer division; if divide by zero, returns 0."
  [n d]
  (cond
    (zero? d) 0
    :else (quot n d)))

(defn asMb
  "Convert a number of bytes to Mb"
  [bytes]
  (safe-quot bytes (* 1024 1024)))

(defn asPrc
  "Computes an integer percentage."
  [n d]
  (str (safe-quot (* n 100) d) "%"))


(defn display-memory
  "Displays the amount of memory available.

  Output like \"141/1590M 9% used\" that means:

  * 141 mb used out of max 1500 + 90 non-heap
  * 9% as 141/1500

  "
  [{:keys [used max other]}]

  (str (asMb used) "/"
       (asMb (+ max other)) "M "
       (asPrc used max) " used"))



(defn runtime
  "Inspects the runtime for stuff you may want to
  print at runtime.

  * :pid - the current PID - under the JVM we also get the hostname
  * :vm  - the kind of VM we are running in
  * :mem - current memory state - e.g. `\"141/1590M 9% used\"`
  "
  [what]
  (condp = what
    :pid  (P/get-current-pid)
    :vm   (P/get-current-VM)
    :mem  (display-memory (P/get-memory-state))
    ))



;;
;; DEFINE build env
;;



(defmacro capture-to
  "Captures the compile-time evaluation of `exp-to-eval`
  and binds it to `sym`.

  For example:

  `(capture-to NOW (str (java.util.Date.))`

  Is expanded to:

  `(defonce NOW \"Sun Nov 04 20:28:41 CET 2018\")`

  And while we are at it, the captured value is also printed on STDOUT,
  so you can see it immediately during the build.

  "
  [sym exp-to-eval]
  (let [v (eval exp-to-eval)
        _ (prn (str "=== Say-cheez captured environment '" sym "':"))
        _ (prn v)
        ]
    `(defonce ~sym {:project ~v})))


(defmacro capture-build-env-to
  "Captures common build environment stuff into a single map
    that contains most of what you may need.

    Call:

    `(capture-build-env-to BUILD)`


    Generates:

    `(defonce BUILD
         {:arch \"x86_64\",
          :git-build \"e4b7836/2018-11-03.14:45:31\",
          :osname \"gnu-linux\",
          :project \"say-cheez\",
          :built-at \"2018-11-03.14:49:25\",
          :built-by \"jenkins\",
          :on-host \"jenkins18.loway.internal\",
          :version \"0.0.2\",
          :build-no \"107\"})`

    Not all values may be present, as it actually depends on
    what is available. If you need to fine-tune the contents,
    use [[capture-to]]. Look at the source.

    "
  [sym]
  `(capture-to ~sym {:project (leiningen-info :project-name)
                   :version (leiningen-info :version)
                   :built-at (now-as :datetime)
                   :on-host (env ["HOSTNAME"] "?")
                   :osname (env ["OSTYPE"] "?")
                   :arch   (env ["HOSTTYPE"] "?")
                   :build-no (env ["BUILD_NUMBER"] "?")
                   :git-build (git-info :all)
                   :built-by (env ["USER"] "?")}))


