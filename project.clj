(defproject say-cheez "0.1.1"
  :description "Takes a snapshot of the environment at build time."
  :url "https://github.com/l3nz/say-cheez"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :aliases
  {"fix" ["cljfmt" "fix"]
   "clj-kondo" ["trampoline" "run" "-m" "clj-kondo.main" "--" "--lint" "src/" "--cache" ".cli-kondo-cache"]}

  ; deps
  :dependencies
  [[org.clojure/clojure "1.9.0" :scope "provided"]
   [clj-kondo "2019.05.19-alpha" :scope "provided"]]

  :main say-cheez.run
  :scm {:name "git"
        ;; :tag "..."
        :url "https://github.com/l3nz/say-cheez"}
  :plugins [[lein-eftest "0.5.1"]
            [jonase/eastwood "0.2.5"]
            [lein-kibit "0.1.6"]
            [lein-cljfmt "0.5.7"]]; repos          
  :deploy-repositories [["clojars"  {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])
