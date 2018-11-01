# How to deploy

Before commit:

	lein clean && lein eftest

Then let's see if the linter notices something.

	lein eastwood

Then, let's format sources:

	lein cljfmt check
	lein cljfmt fix

And commit with a message "Fixing #XX - Description".

Do not forget to edit the Changelog.

## Pushing to Clojars

First advance the version in project.clj.

Then commit and create a tag.

Then check your Clojars credentials and push:

	lein deploy clojars
	Username: l3nz
	

## Pushing documentation

Change the documentation link in `README.md`. Then click on it.



