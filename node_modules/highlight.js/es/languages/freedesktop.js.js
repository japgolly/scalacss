function emitWarning() {
    if (!emitWarning.warned) {
      emitWarning.warned = true;
      console.log(
        'Deprecation (warning): Using file extension in specifier is deprecated, use "highlight.js/lib/languages/freedesktop" instead of "highlight.js/lib/languages/freedesktop.js"'
      );
    }
  }
  emitWarning();
    import lang from './freedesktop.js';
    export default lang;