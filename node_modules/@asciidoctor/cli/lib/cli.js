'use strict'

const Options = require('./options')
const Invoker = require('./invoker')

module.exports = {
  processor: Invoker.asciidoctor,
  Options,
  Invoker
}
