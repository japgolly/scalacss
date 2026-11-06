# kramed

> A full-featured markdown parser and compiler, written in JavaScript. Built
> for speed.

[![NPM version](https://badge.fury.io/js/kramed.png)][badge]
[![Build Status](https://travis-ci.org/GitbookIO/kramed.svg?branch=master)](https://travis-ci.org/GitbookIO/kramed)

## Install

``` bash
npm install kramed --save
```

### Why fork `marked` ?

`marked` hasn't been evolving as much as it could be lately and due to our needs with [GitBook](https://github.com/GitbookIO/gitbook), we need features such as robust `mathjax` support and want to strive closer to the rising `kramdown` standard.

## Usage

Minimal usage:

```js
var kramed = require('kramed');
console.log(kramed('I am using __markdown__.'));
// Outputs: <p>I am using <strong>markdown</strong>.</p>
```

Example setting options with default values:

```js
var kramed = require('kramed');
kramed.setOptions({
  renderer: new kramed.Renderer(),
  gfm: true,
  tables: true,
  breaks: false,
  pedantic: false,
  sanitize: true,
  smartLists: true,
  smartypants: false
});

console.log(kramed('I am using __markdown__.'));
```

## kramed(markdownString [,options] [,callback])

### markdownString

Type: `string`

String of markdown source to be compiled.

### options

Type: `object`

Hash of options. Can also be set using the `kramed.setOptions` method as seen
above.

### callback

Type: `function`

Function called when the `markdownString` has been fully parsed when using
async highlighting. If the `options` argument is omitted, this can be used as
the second argument.

## Options

### highlight

Type: `function`

A function to highlight code blocks. The first example below uses async highlighting with
[node-pygmentize-bundled][pygmentize], and the second is a synchronous example using
[highlight.js][highlight]:

```js
var kramed = require('kramed');

var markdownString = '```js\n console.log("hello"); \n```';

// Async highlighting with pygmentize-bundled
kramed.setOptions({
  highlight: function (code, lang, callback) {
    require('pygmentize-bundled')({ lang: lang, format: 'html' }, code, function (err, result) {
      callback(err, result.toString());
    });
  }
});

// Using async version of kramed
kramed(markdownString, function (err, content) {
  if (err) throw err;
  console.log(content);
});

// Synchronous highlighting with highlight.js
kramed.setOptions({
  highlight: function (code) {
    return require('highlight.js').highlightAuto(code).value;
  }
});

console.log(kramed(markdownString));
```

#### highlight arguments

`code`

Type: `string`

The section of code to pass to the highlighter.

`lang`

Type: `string`

The programming language specified in the code block.

`callback`

Type: `function`

The callback function to call when using an async highlighter.

### renderer

Type: `object`
Default: `new Renderer()`

An object containing functions to render tokens to HTML.

#### Overriding renderer methods

The renderer option allows you to render tokens in a custom manor. Here is an
example of overriding the default heading token rendering by adding an embedded anchor tag like on GitHub:

```javascript
var kramed = require('kramed');
var renderer = new kramed.Renderer();

renderer.heading = function (text, level) {
  var escapedText = text.toLowerCase().replace(/[^\w]+/g, '-');

  return '<h' + level + '><a name="' +
                escapedText +
                 '" class="anchor" href="#' +
                 escapedText +
                 '"><span class="header-link"></span></a>' +
                  text + '</h' + level + '>';
},

console.log(kramed('# heading+', { renderer: renderer }));
```
This code will output the following HTML:
```html
<h1>
  <a name="heading-" class="anchor" href="#heading-">
    <span class="header-link"></span>
  </a>
  heading+
</h1>
```

#### Block level renderer methods

- code(*string* code, *string* language)
- blockquote(*string* quote)
- html(*string* html)
- heading(*string* text, *number*  level)
- hr()
- list(*string* body, *boolean* ordered)
- listitem(*string*  text)
- paragraph(*string* text)
- table(*string* header, *string* body)
- tablerow(*string* content)
- tablecell(*string* content, *object* flags)

`flags` has the following properties:

```js
{
    header: true || false,
    align: 'center' || 'left' || 'right'
}
```

#### Inline level renderer methods

- strong(*string* text)
- em(*string* text)
- codespan(*string* code)
- br()
- del(*string* text)
- link(*string* href, *string* title, *string* text)
- image(*string* href, *string* title, *string* text)

### gfm

Type: `boolean`
Default: `true`

Enable [GitHub flavored markdown][gfm].

### tables

Type: `boolean`
Default: `true`

Enable GFM [tables][tables].
This option requires the `gfm` option to be true.

### breaks

Type: `boolean`
Default: `false`

Enable GFM [line breaks][breaks].
This option requires the `gfm` option to be true.

### pedantic

Type: `boolean`
Default: `false`

Conform to obscure parts of `markdown.pl` as much as possible. Don't fix any of
the original markdown bugs or poor behavior.

### sanitize

Type: `boolean`
Default: `false`

Sanitize the output. Ignore any HTML that has been input.

### smartLists

Type: `boolean`
Default: `true`

Use smarter list behavior than the original markdown. May eventually be
default with the old behavior moved into `pedantic`.

### smartypants

Type: `boolean`
Default: `false`

Use "smart" typograhic punctuation for things like quotes and dashes.

## Access to lexer and parser

You also have direct access to the lexer and parser if you so desire.

``` js
var tokens = kramed.lexer(text, options);
console.log(kramed.parser(tokens));
```

``` js
var lexer = new kramed.Lexer(options);
var tokens = lexer.lex(text);
console.log(tokens);
console.log(lexer.rules);
```

## CLI

``` bash
$ kramed -o hello.html
hello world
^D
$ cat hello.html
<p>hello world</p>
```

## Philosophy behind kramed

The point of kramed was to create a markdown compiler where it was possible to
frequently parse huge chunks of markdown without having to worry about
caching the compiled output somehow...or blocking for an unnecesarily long time.

kramed is very concise and still implements all markdown features. It is also
now fully compatible with the client-side.

kramed more or less passes the official markdown test suite in its
entirety. This is important because a surprising number of markdown compilers
cannot pass more than a few tests. It was very difficult to get kramed as
compliant as it is. It could have cut corners in several areas for the sake
of performance, but did not in order to be exactly what you expect in terms
of a markdown rendering. In fact, this is why kramed could be considered at a
disadvantage in the benchmarks above.

Along with implementing every markdown feature, kramed also implements [GFM
features][gfmf].

## Benchmarks

node v0.8.x

``` bash
$ node test --bench
kramed completed in 3411ms.
kramed (gfm) completed in 3727ms.
kramed (pedantic) completed in 3201ms.
robotskirt completed in 808ms.
showdown (reuse converter) completed in 11954ms.
showdown (new converter) completed in 17774ms.
markdown-js completed in 17191ms.
```

__Kramed is now faster than Discount, which is written in C.__

For those feeling skeptical: These benchmarks run the entire markdown test suite 1000 times. The test suite tests every feature. It doesn't cater to specific aspects.

### Pro level

You also have direct access to the lexer and parser if you so desire.

``` js
var tokens = kramed.lexer(text, options);
console.log(kramed.parser(tokens));
```

``` js
var lexer = new kramed.Lexer(options);
var tokens = lexer.lex(text);
console.log(tokens);
console.log(lexer.rules);
```

``` bash
$ node
> require('kramed').lexer('> i am using kramed.')
[ { type: 'blockquote_start' },
  { type: 'paragraph',
    text: 'i am using kramed.' },
  { type: 'blockquote_end' },
  links: {} ]
```

## Running Tests & Contributing

If you want to submit a pull request, make sure your changes pass the test
suite. If you're adding a new feature, be sure to add your own test.

The kramed test suite is set up slightly strangely: `test/new` is for all tests
that are not part of the original markdown.pl test suite (this is where your
test should go if you make one). `test/original` is only for the original
markdown.pl tests. `test/tests` houses both types of tests after they have been
combined and moved/generated by running `node test --fix` or `kramed --test
--fix`.

In other words, if you have a test to add, add it to `test/new/` and then
regenerate the tests with `node test --fix`. Commit the result. If your test
uses a certain feature, for example, maybe it assumes GFM is *not* enabled, you
can add `.nogfm` to the filename. So, `my-test.text` becomes
`my-test.nogfm.text`. You can do this with any kramed option. Say you want
line breaks and smartypants enabled, your filename should be:
`my-test.breaks.smartypants.text`.

To run the tests:

``` bash
cd kramed/
node test
```

### TODO
  - [ ] Refactor code to have greater modularity
  - [ ] Strive for kramdown compatibility (it's the new standard)
  - [ ] Improve, improve, improve ...

### Contribution and License Agreement

If you contribute code to this project, you are implicitly allowing your code
to be distributed under the MIT license. You are also implicitly verifying that
all code is your original work. `</legalese>`

## License

Marked: Copyright (c) 2011-2014, Christopher Jeffrey. (MIT License)
Kramed: Copyright (c) 2014, Aaron O'Mullan. (MIT Licensed)

See LICENSE for more info.

[gfm]: https://help.github.com/articles/github-flavored-markdown
[gfmf]: http://github.github.com/github-flavored-markdown/
[pygmentize]: https://github.com/rvagg/node-pygmentize-bundled
[highlight]: https://github.com/isagalaev/highlight.js
[badge]: http://badge.fury.io/js/kramed
[tables]: https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#wiki-tables
[breaks]: https://help.github.com/articles/github-flavored-markdown#newlines
