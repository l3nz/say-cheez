# Say-cheez

Takes a snapshot of the environment at build time.

Sometimes you'd want to reference the version of your package
at run time, or when / where / from what sources it was built, but that information
is not available anymore. 

But Clojure macros come to the rescue!

##  Example

	(ns baab.baah
		(:require [say-cheez.core :refer [capture-build-env-to]]))
	....
	(capture-build-env-to BUILD)

Is exactly the same as if you wrote by hand:

	(defonce BUILD 
		{:arch "x86_64",
		 :git-build "e4b7836/2018-11-03.14:45:31",
		 :osname "gnu-linux",
		 :project "say-cheez",
		 :built-at "2018-11-03.14:49:25",
		 :built-by "jenkins",
		 :on-host "jenkins18.loway.internal",
		 :version "0.0.2",
		 :build-no "107"})

But for the fact that such information comes from different places: 

* project.clj
* the build environment
* git
* when the project was built

And would not usually be available at run time.


## Using

The library is available on Clojars:

[![Clojars Project](https://img.shields.io/clojars/v/say-cheez.svg)](https://clojars.org/say-cheez)
[![](https://cljdoc.xyz/badge/say-cheez)](https://cljdoc.xyz/jump/release/say-cheez)


Or the library can be easily referenced through Github:

	{:deps
	 {cli-matic
	  {:git/url "https://github.com/l3nz/say-cheez.git"
	   :sha "..."}}}


## Features

* Captures some environment (project name, date, version, build number) at build time
* Has a couple of functions for inspecting runtime JVM and PID.
* By separating the Java specific functions in the namespace "platform", it should be easy
  to extend for ClojureScript.

TODO:

* Making the macro totally extensible
* Reading edn/json/cvs files 

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