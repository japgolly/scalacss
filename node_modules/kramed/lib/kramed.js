/**
 * kramed - a markdown parser
 * Copyright (c) 2011-2014, Christopher Jeffrey. (MIT Licensed)
 * https://github.com/GitbookIO/kramed
 */
/**
 * kramed - a kramdown parser, based off chjj's kramed
 * Copyright (c) 2014, Aaron O'Mullan. (MIT Licensed)
 * https://github.com/GitbookIO/kramed
*/

var _utils = require('./utils');
var merge = _utils.merge;

var Lexer = require('./lex/block');
var InlineLexer = require('./lex/inline');

var Parser = require('./parser');
var Renderer = require('./renderer');

/**
 * Kramed
 */

function kramed(src, opt, callback) {
  if (callback || typeof opt === 'function') {
    if (!callback) {
      callback = opt;
      opt = null;
    }

    opt = merge({}, kramed.defaults, opt || {});

    var highlight = opt.highlight
      , tokens
      , pending
      , i = 0;

    try {
      tokens = Lexer.lex(src, opt)
    } catch (e) {
      return callback(e);
    }

    pending = tokens.length;

    var done = function(err) {
      if (err) {
        opt.highlight = highlight;
        return callback(err);
      }

      var out;

      try {
        out = Parser.parse(tokens, opt);
      } catch (e) {
        err = e;
      }

      opt.highlight = highlight;

      return err
        ? callback(err)
        : callback(null, out);
    };

    if (!highlight || highlight.length < 3) {
      return done();
    }

    delete opt.highlight;

    if (!pending) return done();

    for (; i < tokens.length; i++) {
      (function(token) {
        if (token.type !== 'code') {
          return --pending || done();
        }
        return highlight(token.text, token.lang, function(err, code) {
          if (err) return done(err);
          if (code == null || code === token.text) {
            return --pending || done();
          }
          token.text = code;
          token.escaped = true;
          --pending || done();
        });
      })(tokens[i]);
    }

    return;
  }
  try {
    if (opt) opt = merge({}, kramed.defaults, opt);
    return Parser.parse(Lexer.lex(src, opt), opt);
  } catch (e) {
    e.message += '\nPlease report this to https://github.com/GitbookIO/kramed.';
    if ((opt || kramed.defaults).silent) {
      return '<p>An error occured:</p><pre>'
        + escape(e.message + '', true)
        + '</pre>';
    }
    throw e;
  }
}

/**
 * Options
 */

kramed.options =
kramed.setOptions = function(opt) {
  merge(kramed.defaults, opt);
  return kramed;
};

kramed.defaults = {
  // Lexer options (both block and inline lexers)
  gfm: true,
  tables: true,
  breaks: false,
  pedantic: false,
  sanitize: false,
  smartLists: false,
  mathjax: true,

  // Kramed options
  silent: false,
  highlight: null,

  // Renderer options
  langPrefix: 'lang-',
  smartypants: false,
  headerPrefix: '',
  headerAutoId: true,
  xhtml: false,

  // Default rendrer passed to Parser
  renderer: new Renderer,
};

/**
 * Expose
 */

kramed.Parser = Parser;
kramed.parser = Parser.parse;

kramed.Renderer = Renderer;

kramed.Lexer = Lexer;
kramed.lexer = Lexer.lex;

kramed.InlineLexer = InlineLexer;
kramed.inlineLexer = InlineLexer.output;

kramed.parse = kramed;

module.exports = kramed;
