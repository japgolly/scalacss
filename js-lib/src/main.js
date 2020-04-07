require('./hacks/node')

const autoprefixer = require('autoprefixer')
const postcss      = require('postcss')
const cssnano      = require('cssnano')
const stylelint    = require('stylelint')

function stylelintConfig() {
  const std = require('stylelint-config-standard').rules
  const rcm = require('stylelint-config-recommended').rules
  const rules = Object.assign({}, rcm, std)
  return {rules};
}

const postcssInstance = postcss([
  stylelint({ config: stylelintConfig() }),
  autoprefixer,
  cssnano({preset: 'default'}),
])

// "If your runner does not handle writing to disk (for example, a gulp transform), you should set both options to point to the same file"
const opts = {from: "input.css", to: "input.css"}

async function main(input) {
  const r = await postcssInstance.process(input, opts)

  const css = r.css

  const warnings = []
  for (const w of r.warnings()) {
    warnings.push(w.text)
  }

  return {css, warnings}
}

export {
  main,
};