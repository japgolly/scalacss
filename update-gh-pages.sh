#!/bin/bash
cd "$(dirname "$0")" || exit 1

#[ $# -ne 1 ] && echo "Usage: $0 <xxx>" && exit 1
#tmp=/tmp/$(date +%Y%m%d-%H%M%S)-$$

git pull \
  && git merge main \
  && bin/gitbook build doc book \
  && git add -A book \
  && git st \
  && echo "git commit -m 'Update book' && git push" && echo

