#!/bin/bash
cd "$(dirname "$(readlink -e "$0")")/.." || exit 1

src=../nyaya/nyaya-test/src/main/scala/japgolly/nyaya/test/Domain.scala
tgt=core/src/main/scala/japgolly/scalacss/Domain.scala

[ ! -e $src ] && echo "File not found: $src" && exit 1
[ -e $tgt ] && cp $tgt /tmp

perl -pe '
s/^package .+/package japgolly.scalacss/;
s!(trait Domain\[)!
// Note: This file is copied from Nyaya.
// https://github.com/japgolly/nyaya


\1!
' < $src > $tgt && ls -l $tgt
