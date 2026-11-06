# direction [![Build Status](https://img.shields.io/travis/wooorm/direction.svg?style=flat)](https://travis-ci.org/wooorm/direction) [![Coverage Status](https://img.shields.io/coveralls/wooorm/direction.svg?style=flat)](https://coveralls.io/r/wooorm/direction?branch=master)

Detect direction: left-to-right, right-to-left, or neutral.

## Installation

npm:
```bash
$ npm install direction
```

Component:
```bash
$ component install wooorm/direction
```

Bower:
```bash
$ bower install direction
```

## Usage

```js
var direction = require('direction');

direction("A"); // "ltr"
direction("anglais"); // "ltr"
direction("بسيطة"); // "rtl"
direction("@"); // "neutral"
```

## CLI

Install:
```bash
$ npm install direction --global
```

Usage:
```
Usage: direction [options] words...

Detect directionality: left-to-right, right-to-left, or neutral

Options:

  -h, --help           output usage information
  -v, --version        output version number

Usage:

# output direction of given values
$ direction @
# neutral

# output direction from stdin
$ echo "الانجليزية" | direction
# rtl
```

## License

MIT © [Titus Wormer](http://wooorm.com)
