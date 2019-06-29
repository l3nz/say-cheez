(defproject say-cheez "0.1.1"
  :description "Takes a snapshot of the environment at build time."
  :url "https://github.com/l3nz/say-cheez"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :aliases
  {"fix" ["cljfmt" "fix"]
  ; Kondo
   "clj-kondo" ["with-profile" "kondo"
                "trampoline" "run" "-m"
                "clj-kondo.main" "--" "--lint" "src/" "--cache" ".cli-kondo-cache"]
   "clj-kondo-test" ["with-profile" "kondo"
                     "trampoline" "run" "-m"
                     "clj-kondo.main" "--" "--lint" "test/" "--cache" ".cli-kondo-cache"]}

  ; deps
  :dependencies
  [[org.clojure/clojure "1.9.0" :scope "provided"]]

  :profiles {:kondo
             {:dependencies [[org.clojure/clojure "1.10.1"]
                             [clj-kondo "2019.06.23-alpha"]]}} :main say-cheez.run
  :scm {:name "git"
        ;; :tag "..."
        :url "https://github.com/l3nz/say-cheez"}

  ; Some plugins
  :plugins [[lein-eftest "0.5.1"]
            [lein-cljfmt "0.5.7"]]

  ; Deploy to repos          
  :deploy-repositories
  [["clojars"  {:sign-releases false :url "https://clojars.org/repo"}]
   ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])
