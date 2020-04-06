const autoprefixer = require('autoprefixer')
const postcss = require('postcss')

const p = postcss([autoprefixer])

async function main(input) {
  const r = await p.process(input)
  return r.css;
}

export {
  main,
};