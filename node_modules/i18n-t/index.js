var _ = require('lodash');
var path = require('path');
var fs = require('fs');

function I18n(opts) {
    if (!(this instanceof I18n)) return new I18n(opts);

    this.opts = _.defaults(opts || {}, {
        defaultLocale: 'en'
    });

    this._locales = {};

    _.bindAll(this, _.functionsIn(this));
}

// Extend locales
I18n.prototype.set = function(_locales) {
    this._locales = _.extend(this._locales, _locales);
};

// Return a locales or all
I18n.prototype.get = function(lang) {
    if (lang) {
        lang = this.resolve(lang);
        return this._locales[lang];
    } else {
        return this._locales;
    }
};

// Return list of locales
I18n.prototype.locales = function() {
    return _.keys(this._locales);
};

// Load locales from a folder
I18n.prototype.load = function(root) {
    var locales = _.chain(fs.readdirSync(root))
        .map(function(filename) {
            var ext = path.extname(filename);
            if (ext != '.json' && ext != '.js') return;

            var lang = path.basename(filename, ext);
            var filepath = path.resolve(root, filename);

            return [
                lang,
                require(filepath)
            ];
        })
        .compact()
        .fromPairs()
        .value();

    this.set(locales);
};

// Resolve a language to an existing locale
I18n.prototype.resolve = function(lang, defaultLocale) {
    defaultLocale = _.isUndefined(defaultLocale)? this.opts.defaultLocale : defaultLocale;

    return _.chain(this.locales())
        .map(function(locale) {
            return {
                locale: locale,
                score: compareLocales(lang, locale)
            };
        })
        .filter(function(lang) {
            return lang.score > 0;
        })
        .sortBy('score')
        .map('locale')
        .last()
        .value() || defaultLocale;
};

// Translate a phrase
I18n.prototype.t = function(lang, phrase) {
    var args = _.toArray(arguments).slice(2);
    var kwargs = {};
    var locale = this.get(lang);

    if (_.isObject(_.last(args))) {
        kwargs = _.last(args);
        args = args.slice(0, -1);
    }

    var tpl = locale[phrase];

    if (_.isUndefined(tpl)) {
        tpl = this.get(this.opts.defaultLocale)[phrase];
    }

    return interpolate(tpl || phrase, args, kwargs);
};

// Compare two language to find the most adequate
function compareLocales(lang, locale) {
    var langMain = _.first(lang.split('-'));
    var langSecond = _.last(lang.split('-'));

    var localeMain = _.first(locale.split('-'));
    var localeSecond = _.last(locale.split('-'));

    if (locale == lang) return 100;
    if (localeMain == langMain && localeSecond == localeMain) return 51;
    if (localeMain == langMain) return 50;
    if (localeSecond == langSecond) return 20;
    return 0;
}

// Interpolate a template
function interpolate(tpl, args, kwargs) {
    var value = tpl;
    kwargs = _.extend(kwargs, args);

    return _.reduce(kwargs, function(value, val, key) {
        var re = /{{([\s\S]*?[^\$])}}/g;
        return value.replace(re, val);
    }, tpl);
}

module.exports = I18n;
