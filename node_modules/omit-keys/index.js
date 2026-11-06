/*!
 * omit-key <https://github.com/jonschlinkert/omit-key>
 *
 * Copyright (c) 2014 Jon Schlinkert, contributors.
 * Licensed under the MIT License
 */

'use strict';

var isObject = require('isobject');
var difference = require('array-difference');

module.exports = function omit(obj, keys) {
  if (!isObject(obj)) {
    return {};
  }

  var props = Object.keys(obj);
  var len = props.length;

  keys = Array.isArray(keys) ? keys : [keys];
  var diff = difference(props, keys);
  var o = {};

  for (var i = 0; i < len; i++) {
    var key = diff[i];

    if (obj.hasOwnProperty(key)) {
      o[key] = obj[key];
    }
  }
  return o;
};