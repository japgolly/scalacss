#!/bin/bash
cd "$(dirname "$(readlink -e "$0")")" || exit 1

fgrep shorthand css_toc.html \
  | fgrep -v '>unset<' \
  | grep -v 'Note that in many cases the shorthand CSS properties.*are more convenient' \
  | perl -pe '
      s/^.*title=.(.+).><code>(.+?)<.*/\2\n\1\n/;
      s/(?<=[^\W_-])[_-]+([^\W_-])|[_-]+/\U$1/g;
      s/,? and (?:the )?/, /g;
    ' \
  | tee shorthand.out
