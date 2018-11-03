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
    )
  )



;;
;; DEFINE build env
;;


;(defmacro defonce-to
;  "Writes a defonce with the contents of data"
;  [env-sym data]
;  `(defonce ~env-sym ~data))

(defmacro capture-build-env-to
    "Captures build environment into a single map
    that contains most of what you may need.

    If you need to fine-tune the contents,
    use defonce-to above.

    "
    [env-sym]
    (let [now (now-as :datetime)
          user (env "USER" "?")
          nbuild (env "BUILD_NUMBER" "?")
          host (env "HOSTNAME" "?")
          hosttype (env "HOSTTYPE" "?")
          osname (env "OSTYPE" "?")
          gitbuild (git-info :all)
          project (leiningen-info :project-name)
          version (leiningen-info :version)
          ]
        `(defonce ~env-sym {:project ~project
                            :version ~version
                            :built-at ~now
                            :on-host ~host
                            :osname ~osname
                            :arch   ~hosttype
                            :build-no ~nbuild
                            :git-build ~gitbuild
                            :built-by ~user})))
