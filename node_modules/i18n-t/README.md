# i18n-t

Easy to use i18n utility. It does not contain express specific middlewares, etc.

[![Build Status](https://travis-ci.org/SamyPesse/i18n-t.png?branch=master)](https://travis-ci.org/SamyPesse/i18n-t)
[![NPM version](https://badge.fury.io/js/i18n-t.svg)](http://badge.fury.io/js/i18n-t)


```js
var I18n = require('i18n-t');


var i18n = new I18n({
    defaultLocale: 'en'
});

// Load locales from a directory
i18n.load('./locales');

// or using a pre-loaded objects
i18n.set({
    en: {
        HELLO: 'Hello {{name}}',
        CATS: '{{0}} cats'
    }
});

// Translate sentences
i18n.t('en', 'HELLO', { name: 'Samy' });
i18n.t('en', 'CATS', 10);
```

