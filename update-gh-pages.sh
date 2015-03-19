#!/bin/bash
cd "$(dirname "$(readlink -e "$0")")" || exit 1

#[ $# -ne 1 ] && echo "Usage: $0 <xxx>" && exit 1
#tmp=/tmp/$(date +%Y%m%d-%H%M%S)-$$

git merge master \
  && bin/gitbook build doc book \
  && git add -A book \
  && git st \
  && echo "git commit -m 'Updated book.'"

