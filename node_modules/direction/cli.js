#!/usr/bin/env node
'use strict';

/*
 * Dependencies.
 */

var direction,
    pack;

direction = require('./');
pack = require('./package.json');

/*
 * Arguments.
 */

var argv;

argv = process.argv.slice(2);

/*
 * Command.
 */

var command;

command = Object.keys(pack.bin)[0];

/**
 * Help.
 *
 * @return {string}
 */
function help() {
    return [
        '',
        'Usage: ' + command + ' [options] words...',
        '',
        pack.description,
        '',
        'Options:',
        '',
        '  -h, --help           output usage information',
        '  -v, --version        output version number',
        '',
        'Usage:',
        '',
        '# output direction of given values',
        '$ ' + command + ' @',
        '# neutral',
        '',
        '# output direction from stdin',
        '$ echo "الانجليزية" | ' + command,
        '# rtl'
    ].join('\n  ') + '\n';
}

/*
 * Program.
 */

if (
    argv.indexOf('--help') === 0 ||
    argv.indexOf('-h') === 0
) {
    console.log(help());
} else if (
    argv.indexOf('--version') === 0 ||
    argv.indexOf('-v') === 0
) {
    console.log(pack.version);
} else if (argv[0]) {
    console.log(direction(argv.join(' ')));
} else {
    process.stdin.resume();
    process.stdin.setEncoding('utf8');
    process.stdin.on('data', function (data) {
        console.log(direction(data.trim()));
    });
}
