# nunjucks-do

[![Build Status](https://travis-ci.org/SamyPesse/nunjucks-do.png?branch=master)](https://travis-ci.org/SamyPesse/nunjucks-do) [![NPM version](https://badge.fury.io/js/nunjucks-do.svg)](http://badge.fury.io/js/nunjucks-do)

Nunjucks extension that brings a "do" tag

### How to install it?

```
$ npm install nunjucks-do --save
```

### How to use it?

```js
var DoExtension = require("nunjucks-do")(nunjucks);

env.addExtension('DoExtension', new DoExtension());
```

```html
{% do %}
test = { hello: 'world' }
{% enddo %}
```