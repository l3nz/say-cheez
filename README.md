# Say-cheez :)

Captures a snapshot of the environment at build time, so you can display it at run-time.

Sometimes you'd want to reference the version of your package
at run time, or when / where / from what sources it was built, but that information
is not available anymore once you deploy your app somewhere else. 

It can also be useful to run a pipeline once when building, e.g. compiling a SASS file
into CSS and storing the result as a string.


[![Clojars Project](https://img.shields.io/clojars/v/say-cheez.svg)](https://clojars.org/say-cheez)
[![](https://cljdoc.xyz/badge/say-cheez)](https://cljdoc.xyz/jump/release/say-cheez)
![ClojarsDownloads](https://img.shields.io/clojars/dt/say-cheez)


##  Example

Look at this namespace:

	(ns baab.baah
		(:require [say-cheez.core :refer [capture-build-env-to]]))
	....
	(capture-build-env-to BUILD)

The var called `BUILD` is exactly the same as if you wrote by hand:

	(defonce BUILD 
	    {:project
    		{:arch "x86_64",
	    	 :git-build "e4b7836/2018-11-03.14:45:31",
		     :osname "gnu-linux",
	    	 :project "say-cheez",
		     :built-at "2018-11-03.14:49:25",
	    	 :built-by "jenkins",
		     :on-host "jenkins18.loway.internal",
	    	 :version "0.0.2",
		     :build-no "107"}})

But that would be pretty annoying to do by hand, because such information comes from different places: 

* project.clj
* the build environment
* git
* when the project was built

And would not usually be available at run time.

### Creating your own DEFs

The problem with the approach above is that linters may not understand that 
`BUILD` was defined at all, so they might display it as "broken" in your
IDE or raise an exception. 

To make them happy, you can define `BUILD` by yourself:

	(ns baab.baah
		(:require [say-cheez.core :refer [current-build-env]]))
	....
	(def BUILD (current-build-env))

Please note that `current-build-env` has a couple of minor differences to `capture-build-env-to`:

* It does not print the captured value (so you can have a 'silent' build)
* It does not nest the captured value under a `:project` key, as it is
  just a value that you can nest yourself.



### Customizing values

Of course, you can capture the exact values you need if our own chili is not to your taste.

		(capture-to MYBUILD {:project (leiningen-info :project-name)
		                     :myId    (env ["MY_OWN_ID"] "?")})

or, to avoid creating a silent def:

        (def ABC (capture {:project (leiningen-info :project-name)
		                   :myId    (env ["MY_OWN_ID"] "?")})
		               
or even:

        (def ABC (capture {:project (leiningen-info :project-name)
		                   :myId    (env ["MY_OWN_ID"] "?")}
		                   "value of ABC")
		                     
where, during compile, the value computed will be printed out as "value of ABC".


You can call any function and build any valid data structure.

Still, we offer some convenience functions to make your life easier:

* *Current time:* `(now-as :datetime)`. Also valid: 
	* `:date`
    * `:time`
    * `:timec`   (compact)
    * `:datetime`
* *Build Environment:* `(env ["A" "B"] "x")`. Tries reading first A and then B, and if all are undefined, returns "x". You may reasonably want to look for a sequence of environment variables if you build on different machines / OS's.
* *Leiningen:* `(leiningen-info :project-name)`. Also valid:
    * `:project-name`
    * `:version`
* *Git:* `(git-info :all)`. Also valid:
    * `:commit-id`
    * `:commit-long`
    * `:last-committer`
    * `:date`
    * `:date-compact`
    * `:all`    - *abcdefg/20181103.1023*
* *Runtime:* this is useful if you have a long running application - maybe close to the version and build number, you want to print the current memory usage. I surely do. So call `(runtime :mem)`. Also valid:
  * `:pid` - the current PID - under the JVM we also get the hostname
  * `:vm`  - the kind of VM we are running in
  * `:mem` - current memory state - e.g. `"141/1590M 9% used"`

About the runtime, under `platform` there is a function to set the current thread's name.

## Using

The library is available on Clojars, or the library can be easily referenced through Github:

	{:deps
	 {cli-matic
	  {:git/url "https://github.com/l3nz/say-cheez.git"
	   :sha "..."}}}


## Features

* Captures some environment (project name, date, version, build number, git commit) at build time
* Has a couple of functions for inspecting runtime JVM and PID.
* By separating the Java specific functions in the namespace "platform", it should be easy (?)
  to extend for ClojureScript.

TODO:

* Reading edn/json/cvs files, so they appear as a var and you do not have to read them. 

### Transitive dependencies

Say-cheez currently depends on:

* org.clojure/clojure

## License

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file epl.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.

You must not remove this notice, or any other, from this software.
