const fs = require('fs')
const ospath = require('path')
const asciidoctor = require('@asciidoctor/core')()
const pkg = require('../package.json')
const stdin = require('./stdin')

const DOT_RELATIVE_RX = new RegExp(`^\\.{1,2}[/${ospath.sep.replace('/', '').replace('\\', '\\\\')}]`)

class Invoker {
  constructor (options) {
    this.options = options
  }

  async invoke () {
    const processArgs = this.options.argv.slice(2)
    const { args } = this.options
    const { verbose, version, files } = args
    if (version || (verbose && processArgs.length === 1)) {
      this.showVersion()
      process.exit(0)
    }
    Invoker.prepareProcessor(args, asciidoctor)
    const options = this.options.options
    const failureLevel = options.failure_level
    if (this.options.stdin) {
      await Invoker.convertFromStdin(options, args)
      Invoker.exit(failureLevel)
    } else if (files && files.length > 0) {
      Invoker.processFiles(files, verbose, args.timings, options)
      Invoker.exit(failureLevel)
    } else {
      this.showHelp()
      process.exit(0)
    }
  }

  showHelp () {
    if (this.options.args.help === 'syntax') {
      console.log(fs.readFileSync(ospath.join(__dirname, '..', 'data', 'reference', 'syntax.adoc'), 'utf8'))
    } else {
      this.options.yargs.showHelp()
    }
  }

  showVersion () {
    console.log(this.version())
  }

  version () {
    const releaseName = process.release ? process.release.name : 'node'
    return `Asciidoctor.js ${asciidoctor.getVersion()} (Asciidoctor ${asciidoctor.getCoreVersion()}) [https://asciidoctor.org]
Runtime Environment (${releaseName} ${process.version} on ${process.platform})
CLI version ${pkg.version}`
  }

  /**
   * @deprecated Use {#showVersion}. Will be removed in version 4.0.
   */
  static printVersion () {
    console.log(new Invoker().version())
  }

  static async readFromStdin () {
    return stdin.read()
  }

  static async convertFromStdin (options, args) {
    const data = await Invoker.readFromStdin()
    if (args.timings) {
      const timings = asciidoctor.Timings.create()
      const instanceOptions = Object.assign({}, options, { timings })
      Invoker.convert(asciidoctor.convert, data, instanceOptions)
      timings.printReport(process.stderr, '-')
    } else {
      Invoker.convert(asciidoctor.convert, data, options)
    }
  }

  static convert (processorFn, input, options) {
    try {
      processorFn.apply(asciidoctor, [input, options])
    } catch (e) {
      if (e && e.name === 'NotImplementedError' && e.message === `asciidoctor: FAILED: missing converter for backend '${options.backend}'. Processing aborted.`) {
        console.error(`> Error: missing converter for backend '${options.backend}'. Processing aborted.`)
        console.error('> You might want to require a Node.js package with --require option to support this backend.')
        process.exit(1)
      }
      throw e
    }
  }

  static convertFile (file, options) {
    Invoker.convert(asciidoctor.convertFile, file, options)
  }

  static processFiles (files, verbose, timings, options) {
    files.forEach((file) => {
      if (verbose) {
        console.log(`converting file ${file}`)
      }
      if (timings) {
        const timings = asciidoctor.Timings.create()
        const instanceOptions = Object.assign({}, options, { timings })
        Invoker.convertFile(file, instanceOptions)
        timings.printReport(process.stderr, file)
      } else {
        Invoker.convertFile(file, options)
      }
    })
  }

  static requireLibrary (requirePath, cwd = process.cwd()) {
    if (requirePath.charAt(0) === '.' && DOT_RELATIVE_RX.test(requirePath)) {
      // NOTE require resolves a dot-relative path relative to current file; resolve relative to cwd instead
      requirePath = ospath.resolve(requirePath)
    } else if (!ospath.isAbsolute(requirePath)) {
      // NOTE appending node_modules prevents require from looking elsewhere before looking in these paths
      const paths = [cwd, ospath.dirname(__dirname)].map((start) => ospath.join(start, 'node_modules'))
      requirePath = require.resolve(requirePath, { paths })
    }
    return require(requirePath)
  }

  static prepareProcessor (argv, asciidoctor) {
    const requirePaths = argv.require
    if (requirePaths) {
      requirePaths.forEach((requirePath) => {
        const lib = Invoker.requireLibrary(requirePath)
        if (lib && typeof lib.register === 'function') {
          // REMIND: it could be an extension or a converter.
          // the register function on a converter does not take any argument
          // but the register function on an extension expects one argument (the extension registry)
          // Until we revisit the API for extension and converter, we pass the registry as the first argument
          lib.register(asciidoctor.Extensions)
        }
      })
    }
  }

  static exit (failureLevel) {
    let code = 0
    const logger = asciidoctor.LoggerManager.getLogger()
    if (logger && typeof logger.getMaxSeverity === 'function' && logger.getMaxSeverity() && logger.getMaxSeverity() >= failureLevel) {
      code = 1
    }
    process.exit(code)
  }
}

module.exports = Invoker
module.exports.asciidoctor = asciidoctor
