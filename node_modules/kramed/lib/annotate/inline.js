var rules = require('../rules/inline').gitbook;
var engine = require('./engine');

// List of all the regexes we want to run
var ruleTypes = [
    'escape', 'autolink', 'url', 'html', 'link', 'reflink',
    'nolink', 'reffn', 'strong', 'em', 'code', 'br',
    'del', 'tplexpr', 'tplvar', 'text'
];

// Mapping if rule type is different from token type
var ruleMap = {};

function annotate(src) {
    return engine(src, rules, ruleTypes, ruleMap);
}

module.exports = annotate;
