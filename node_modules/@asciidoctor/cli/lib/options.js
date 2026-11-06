/* global Opal */
const yargs = require('yargs')
const asciidoctor = require('@asciidoctor/core')()

const convertOptions = (args, attrs) => {
  const attributes = attrs || []
  const backend = args.backend
  const doctype = args.doctype
  const safeMode = args['safe-mode']
  const embedded = args.embedded === true || args['no-header-footer'] === true || args.standalone === false
  const standalone = !embedded
  const sectionNumbers = args['section-numbers']
  const baseDir = args['base-dir']
  const destinationDir = args['destination-dir']
  const outFile = args['out-file']
  const templateDir = args['template-dir']
  const templateEngine = args['template-engine']
  const quiet = args.quiet
  const verbose = args.verbose
  const timings = args.timings
  const trace = args.trace
  const requireLib = args.require
  let level = args['failure-level'].toUpperCase()
  if (level === 'WARNING') {
    level = 'WARN'
  }
  const failureLevel = asciidoctor.LoggerSeverity[level]
  const debug = verbose && args['verbose-sole-argument'] !== true
  if (debug) {
    console.log('require ' + requireLib)
    console.log('backend ' + backend)
    console.log('doctype ' + doctype)
    console.log('standalone ' + standalone)
    console.log('section-numbers ' + sectionNumbers)
    console.log('failure-level ' + level)
    console.log('quiet ' + quiet)
    console.log('verbose ' + verbose)
    console.log('timings ' + timings)
    console.log('trace ' + trace)
    console.log('base-dir ' + baseDir)
    console.log('destination-dir ' + destinationDir)
    console.log('template-dir ' + templateDir)
    console.log('template-engine ' + templateEngine)
  }
  const verboseMode = quiet ? 0 : verbose ? 2 : 1
  if (sectionNumbers) {
    attributes.push('sectnums')
  }
  const cliAttributes = args.attribute
  if (cliAttributes) {
    attributes.push(...cliAttributes)
  }
  if (debug) {
    console.log('verbose-mode ' + verboseMode)
    console.log('attributes ' + attributes)
  }
  const options = {
    doctype: doctype,
    safe: safeMode,
    standalone: standalone,
    failure_level: failureLevel,
    verbose: verboseMode,
    timings: timings,
    trace: trace
  }
  if (backend) {
    options.backend = backend
  }
  if (baseDir != null) {
    options.base_dir = baseDir
  }
  if (destinationDir != null) {
    options.to_dir = destinationDir
  }
  if (templateDir) {
    options.template_dirs = templateDir
  }
  if (templateEngine) {
    options.template_engine = templateEngine
  }
  if (typeof outFile !== 'undefined') {
    if (outFile === '') {
      options.to_file = '-'
    } else {
      options.to_file = outFile
      options.mkdirs = true
    }
  } else {
    options.mkdirs = true
  }
  options.attributes = attributes
  if (debug) {
    console.log('options ' + JSON.stringify(options))
  }
  if (options.to_file === '-') {
    options.to_file = Opal.gvars.stdout
  }
  return options
}

class Options {
  constructor (options) {
    this.options = options || {}
    this.args = {
      standalone: typeof this.options.standalone !== 'undefined' ? this.options.standalone : true,
      backend: this.options.backend,
      'safe-mode': typeof this.options.safe !== 'undefined' ? this.options.safe : 'unsafe'
    }
    if (Array.isArray(this.options.attributes)) {
      this.attributes = options.attributes
    } else if (typeof this.options.attributes === 'object') {
      const attrs = this.options.attributes
      const attributes = []
      Object.keys(attrs).forEach((key) => {
        attributes.push(`${key}=${attrs[key]}`)
      })
      this.attributes = attributes
    } else {
      this.attributes = []
    }
    this.cmd = yargs
      .option('backend', {
        alias: 'b',
        describe: 'set output format backend',
        type: 'string'
      })
      .option('doctype', {
        alias: 'd',
        describe: 'document type to use when converting document',
        choices: ['article', 'book', 'manpage', 'inline']
      })
      .option('out-file', {
        alias: 'o',
        describe: 'output file (default: based on path of input file) use \'\' to output to STDOUT',
        type: 'string'
      })
      .option('safe-mode', {
        alias: 'S',
        describe: 'set safe mode level explicitly, disables potentially dangerous macros in source files, such as include::[]',
        choices: ['unsafe', 'safe', 'server', 'secure']
      })
      .option('embedded', {
        alias: 'e',
        describe: 'suppress enclosing document structure and output an embedded document',
        type: 'boolean'
      })
      .option('no-header-footer', {
        alias: 's',
        describe: 'suppress enclosing document structure and output an embedded document',
        type: 'boolean'
      })
      .option('section-numbers', {
        alias: 'n',
        default: false,
        describe: 'auto-number section titles in the HTML backend disabled by default',
        type: 'boolean'
      })
      .option('base-dir', {
        // QUESTION: should we check that the directory exists ? coerce to a directory ?
        alias: 'B',
        describe: 'base directory containing the document and resources (default: directory of source file)',
        type: 'string'
      })
      .option('destination-dir', {
        // QUESTION: should we check that the directory exists ? coerce to a directory ?
        alias: 'D',
        describe: 'destination output directory (default: directory of source file)',
        type: 'string'
      })
      .option('failure-level', {
        default: 'FATAL',
        describe: 'set minimum logging level that triggers non-zero exit code',
        choices: ['info', 'INFO', 'warn', 'WARN', 'warning', 'WARNING', 'error', 'ERROR', 'fatal', 'FATAL']
      })
      .option('quiet', {
        alias: 'q',
        default: false,
        describe: 'suppress warnings',
        type: 'boolean'
      })
      .option('trace', {
        default: false,
        describe: 'include backtrace information on errors',
        type: 'boolean'
      })
      .option('verbose', {
        alias: 'v',
        default: false,
        describe: 'enable verbose mode',
        type: 'boolean'
      })
      .option('timings', {
        alias: 't',
        default: false,
        describe: 'enable timings mode',
        type: 'boolean'
      })
      .option('template-dir', {
        alias: 'T',
        array: true,
        describe: 'a directory containing custom converter templates that override the built-in converter (may be specified multiple times)',
        type: 'string'
      })
      .option('template-engine', {
        alias: 'E',
        describe: 'template engine to use for the custom converter templates',
        type: 'string'
      })
      .option('attribute', {
        alias: 'a',
        array: true,
        describe: 'a document attribute to set in the form of key, key! or key=value pair',
        type: 'string'
      })
      .option('require', {
        alias: 'r',
        array: true,
        describe: 'require the specified library before executing the processor, using the standard Node require',
        type: 'string'
      })
      .version(false)
      .option('version', {
        alias: 'V',
        default: false,
        describe: 'display the version and runtime environment (or -v if no other flags or arguments)',
        type: 'boolean'
      })
      .help(false)
      .option('help', {
        describe: `print a help message
show this usage if TOPIC is not specified or recognized
show an overview of the AsciiDoc syntax if TOPIC is syntax`,
        type: 'string'
      })
      .nargs('template-dir', 1)
      .nargs('attribute', 1)
      .nargs('require', 1)
      .usage(`$0 [options...] files...
Translate the AsciiDoc source file or file(s) into the backend output format (e.g., HTML 5, DocBook 5, etc.)
By default, the output is written to a file with the basename of the source file and the appropriate extension`)
      .example('$0 -b html5 doc.asciidoc', 'convert an AsciiDoc file to HTML5; result will be written in a file named doc.html')
      .epilogue('For more information, please visit https://asciidoctor.org/docs')
    this.yargs = yargs
  }

  parse (argv) {
    const processArgs = argv.slice(2)
    this.argv = argv
    const args = this.argsParser().parse(processArgs)
    Object.assign(this.args, args)
    const files = this.args.files
    this.stdin = files && files.length === 0 && processArgs[processArgs.length - 1] === '-'
    if (this.stdin) {
      this.args['out-file'] = this.args['out-file'] || '-'
    }
    this.args['verbose-sole-argument'] = this.args.verbose && processArgs.length === 1
    const options = convertOptions(this.args, this.attributes)
    Object.assign(this.options, options)
    return this
  }

  addOption (key, opt) {
    this.cmd.option(key, opt)
    return this
  }

  argsParser () {
    return this.yargs
      .detectLocale(false)
      .wrap(Math.min(120, this.yargs.terminalWidth()))
      .command('$0 [files...]', '', () => this.cmd)
      .parserConfiguration({
        'boolean-negation': false
      })
  }
}

module.exports = Options
