var _utils = require('../utils');
var escape = _utils.escape;
var noop = _utils.noop;

var inline = require('../rules/inline');
var Renderer = require('../renderer');
var defaultOptions = require('./options');
var isHTMLBlock =  require('./html_blocks');

/**
 * Inline Lexer & Compiler
 */

function InlineLexer(links, options, renderer) {
  this.options = options || defaultOptions;
  this.links = links;
  this.rules = inline.normal;
  this.renderer = renderer

  if (!this.links) {
    throw new
      Error('Tokens array requires a `links` property.');
  }

  if (this.options.gfm) {
    if (this.options.breaks) {
      this.rules = inline.breaks;
    } else {
      this.rules = inline.gfm;
    }
  } else if (this.options.pedantic) {
    this.rules = inline.pedantic;
  }

  // Is mathjax disabled ?
  if (!this.options.mathjax) {
     this.rules.math = noop;
  }
}

/**
 * Expose Inline Rules
 */

InlineLexer.rules = inline;

/**
 * Static Lexing/Compiling Method
 */

InlineLexer.output = function(src, links, options) {
  var inline = new InlineLexer(links, options, new Renderer());
  return inline.output(src);
};

InlineLexer.prototype.escape = function(html, encode) {
  // Handle escaping being turned off
  if(this.options && this.options.escape === false) {
    return html;
  }
  return escape(html, encode);
};

/**
 * Lexing/Compiling
 */

InlineLexer.prototype.output = function(src) {
  var out = ''
    , link
    , text
    , href
    , cap;

  while (src) {
    // escape
    if (cap = this.rules.escape.exec(src)) {
      src = src.substring(cap[0].length);
      out += cap[1];
      continue;
    }

    // autolink
    if (cap = this.rules.autolink.exec(src)) {
      src = src.substring(cap[0].length);
      if (cap[2] === '@') {
        text = cap[1].charAt(6) === ':'
          ? this.mangle(cap[1].substring(7))
          : this.mangle(cap[1]);
        href = this.mangle('mailto:') + text;
      } else {
        text = this.escape(cap[1]);
        href = text;
      }
      out += this.renderer.link(href, null, text);
      continue;
    }

    // url (gfm)
    if (!this.inLink && (cap = this.rules.url.exec(src))) {
      src = src.substring(cap[0].length);
      text = this.escape(cap[1]);
      href = text;
      out += this.renderer.link(href, null, text);
      continue;
    }

    // html
    if (cap = this.rules.html.exec(src)) {
      // Found a link
      if(cap[1] === 'a' && cap[2] && !this.inLink) {
        // Opening tag
        out += cap[0].substring(0, cap[0].indexOf(cap[2]));
        this.inLink = true;
        // In between the tag
        out += this.output(cap[2]);
        this.inLink = false;
        // Outer tag
        out += cap[0].substring(cap[0].indexOf(cap[2])+cap[2].length);
        // Advance parser
        src = src.substring(cap[0].length);
        continue;
      }

      // Found HTML that we should parse
      if(cap[1] && !isHTMLBlock(cap[1]) && cap[2]) {
        // Opening tag
        out += cap[0].substring(0, cap[0].indexOf(cap[2]));
        // In between the tag
        out += this.output(cap[2]);
        // Outer tag
        out += cap[0].substring(cap[0].indexOf(cap[2])+cap[2].length);
        // Advance parser
        src = src.substring(cap[0].length);
        continue;
      }

      // Any other HTML
      src = src.substring(cap[0].length);
      out += cap[0];
      continue;
    }

    // link
    if (cap = this.rules.link.exec(src)) {
      src = src.substring(cap[0].length);
      this.inLink = true;
      out += this.outputLink(cap, {
        href: cap[2],
        title: cap[3]
      });
      this.inLink = false;
      continue;
    }

    // reffn
    if ((cap = this.rules.reffn.exec(src))) {
        src = src.substring(cap[0].length);
        out += this.renderer.reffn(cap[1]);
        continue;
    }

    // reflink, nolink
    if ((cap = this.rules.reflink.exec(src))
        || (cap = this.rules.nolink.exec(src))) {
      src = src.substring(cap[0].length);
      link = (cap[2] || cap[1]).replace(/\s+/g, ' ');
      link = this.links[link.toLowerCase()];
      if (!link || !link.href) {
        out += cap[0].charAt(0);
        src = cap[0].substring(1) + src;
        continue;
      }
      this.inLink = true;
      out += this.outputLink(cap, link);
      this.inLink = false;
      continue;
    }

    // strong
    if (cap = this.rules.strong.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.strong(this.output(cap[2] || cap[1]));
      continue;
    }

    // em
    if (cap = this.rules.em.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.em(this.output(cap[2] || cap[1]));
      continue;
    }

    // code
    if (cap = this.rules.code.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.codespan(this.escape(cap[2], true));
      continue;
    }

    // math
    if (cap = this.rules.math.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.math(cap[1], 'math/tex', false); //FIXME: filter <script> & </script>
      continue;
    }

    // br
    if (cap = this.rules.br.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.br();
      continue;
    }

    // del (gfm)
    if (cap = this.rules.del.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.renderer.del(this.output(cap[1]));
      continue;
    }

    // text
    if (cap = this.rules.text.exec(src)) {
      src = src.substring(cap[0].length);
      out += this.escape(this.smartypants(cap[0]));
      continue;
    }

    if (src) {
      throw new
        Error('Infinite loop on byte: ' + src.charCodeAt(0));
    }
  }

  return out;
};

/**
 * Compile Link
 */

InlineLexer.prototype.outputLink = function(cap, link) {
  var href = this.escape(link.href)
    , title = link.title ? this.escape(link.title) : null;

  return cap[0].charAt(0) !== '!'
    ? this.renderer.link(href, title, this.output(cap[1]))
    : this.renderer.image(href, title, this.escape(cap[1]));
};

/**
 * Smartypants Transformations
 */

InlineLexer.prototype.smartypants = function(text) {
  if (!this.options.smartypants) return text;
  return text
    // em-dashes
    .replace(/--/g, '\u2014')
    // opening singles
    .replace(/(^|[-\u2014/(\[{"\s])'/g, '$1\u2018')
    // closing singles & apostrophes
    .replace(/'/g, '\u2019')
    // opening doubles
    .replace(/(^|[-\u2014/(\[{\u2018\s])"/g, '$1\u201c')
    // closing doubles
    .replace(/"/g, '\u201d')
    // ellipses
    .replace(/\.{3}/g, '\u2026');
};

/**
 * Mangle Links
 */

InlineLexer.prototype.mangle = function(text) {
  var out = ''
    , l = text.length
    , i = 0
    , ch;

  for (; i < l; i++) {
    ch = text.charCodeAt(i);
    if (Math.random() > 0.5) {
      ch = 'x' + ch.toString(16);
    }
    out += '&#' + ch + ';';
  }

  return out;
};

module.exports = InlineLexer;
