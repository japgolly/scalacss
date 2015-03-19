#!/bin/bash

cd "$(dirname "$(readlink -e "$0")")" || exit 1

n=css_toc

cat $n.html \
  | egrep -v '>(unset|inherit)<' \
  | grep '^<li><a' | egrep -v '<code>&lt;|\(\)</code>' | grep '/Web/CSS/[a-z][^"#]*"' \
  | perl -pe '
      sub camelCase {
        my $r = $3 =~ s/(?<=[^\W_-])[_-]+([^\W_-])|[_-]+/\U$1/gr;
        return $r;
      }
      s!(?<=title=)'"'"'!"!;
      s!'"'"'(?=><code)!"!;
      s!(CSS/margin".+)$!\1"><code>margin</code></a></li>!;
      s!^.*href="(.*?)".*title="(.*)".*<code>(.*?)</code>.*!"
/**
 * $2
 *
 * \@see <a href=\"https://developer.mozilla.org$1\">MDN</a>
 */
val ".camelCase." = Attr.simple(\"$3\")"
!e' | tee $n.out1

