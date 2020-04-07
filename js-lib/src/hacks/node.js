// =======================================================================================

if (!process) process = {}
if (!process.versions) process.versions = {}
if (!process.versions.node) process.versions.node = '12.14.0'

// =======================================================================================

const fs = require("fs");

if (fs.readFile === undefined) {
  function fakeReadFile(file, cb) {
    cb(null, '');
  }
  fs.readFile = fakeReadFile;
}

if (fs.readFileSync === undefined) {
  function fakeReadFile(file) {
    // console.log("readFileSync: ", file)
    // throw {code:'ENOENT'}
    return '';
  }
  fs.readFileSync = fakeReadFile;
}

// =======================================================================================

const path = require("path");

if (path.posix === undefined) path.posix = path;
