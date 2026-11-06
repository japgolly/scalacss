var rules = require('../rules/block').tables;

var engine = require('./engine');

// List of all the regexes we want to run
var ruleTypes = [
    'newline', 'code', 'fences', 'footnote', 'heading',
    'nptable', 'lheading', 'hr', 'blockquote', 'list',
    'html', 'def', 'table', 'paragraph', 'text',
];

// Mapping if rule type is different from token type
var ruleMap = {
    'nptable': 'table',
    'lheading': 'heading',
    'newline': 'space',
    'fences': 'code',
};

function annotate(src) {
    return engine(src, rules, ruleTypes, ruleMap);
}

module.exports = annotate;
