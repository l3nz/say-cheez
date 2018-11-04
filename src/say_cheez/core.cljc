^{:doc "Annotation!"}
(ns say-cheez.core
    (:require [say-cheez.platform :as P]
              [clojure.edn :as edn]))

;;
;; Date/time
;;



(defn now-as
    "Prints current date/time in different ways."
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
(defn runtime
  "Inspects the runtime for stuff you may want to
  print at runtime."
  [what]
  (condp = what
    :pid  (P/get-current-pid)
    :vm   (P/get-current-VM)
    ))



;;
;; DEFINE build env
;;



(defmacro capture-to
  "Captures the compile-time evaluation of `exp-to-eval`
  and binds it to `sym`.

  For example:

  (capture-to NOW (str (java.util.Date.))

  Is expanded to:

  (defonce NOW \"Sun Nov 04 20:28:41 CET 2018\")

  And while we are at it, it is also printed on STDOUT.

  "
  [sym exp-to-eval]
  (let [v (eval exp-to-eval)
        _ (prn (str "Say-cheez captured environment '" sym "' : " v))
        ]
    `(defonce ~sym {:project ~v})))


(defmacro capture-build-env-to
  "Captures common build environment stuff into a single map
    that contains most of what you may need.

    If you need to fine-tune the contents,
    use `capture-to` above.

    Call:

    (capture-build-env-to BUILD)


    Generates:

    `(defonce BUILD \n\t{:arch \"x86_64\",\n\t :git-build \"e4b7836/2018-11-03.14:45:31\",\n\t :osname \"gnu-linux\",\n\t :project \"say-cheez\",\n\t :built-at \"2018-11-03.14:49:25\",\n\t :built-by \"jenkins\",\n\t :on-host \"jenkins18.loway.internal\",\n\t :version \"0.0.2\",\n\t :build-no \"107\"})`

    (Not all values may be present, as it actually depends on
    what is available).

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


