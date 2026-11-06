all: kramed.js kramed.min.js

kramed.min.js: kramed.js
	@uglifyjs --comments '/\*[^\0]+?Copyright[^\0]+?\*/' -o kramed.min.js kramed.js

kramed.js: lib/* lib/lex/* lib/rules/*
	@browserify -o kramed.js -s kramed -e lib/kramed.js

clean:
	@rm kramed.js
	@rm kramed.min.js

bench:
	@node test --bench

.PHONY: clean all
