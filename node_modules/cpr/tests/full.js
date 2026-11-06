var assert = require('assert'),
    path = require('path'),
    fs = require('fs'),
    rimraf = require('rimraf'),
    mkdirp = require('mkdirp'),
    cpr = require('../lib'),
    exec = require('child_process').exec,
    to = path.join(__dirname, './out/'),
    from = path.join(__dirname, '../node_modules');

describe('cpr test suite', function() {
    this.timeout(55000);

    describe('loading', function() {
        before(function() {
            rimraf.sync(to);
        });

        it('should export raw method', function () {
            assert.equal(typeof cpr, 'function');
        });

        it('should export cpr method too', function () {
            assert.equal(typeof cpr.cpr, 'function');
        });
    });

    describe('should copy node_modules', function() {
        var out = path.join(to, '0');
        var data = {};
        var beforeStat = fs.statSync(path.join(from, '.bin/mocha'));

        before(function(done) {
            cpr(from, out, function(err, status) {
                data = {
                    from: fs.readdirSync(from).sort(),
                    to: fs.readdirSync(out).sort()
                };
                done();
            });
        });

        it('has ./out/0', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });

        it('dirs are equal', function() {
            assert.deepEqual(data.to, data.from);
        });

        it('from directory has graceful-fs dir', function() {
            var fromHasGFS = data.from.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, fromHasGFS);
        });

        it('to directory has graceful-fs dir', function() {
            var toHasGFS = data.to.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, toHasGFS);
        });

        it('preserves file mode of copied files', function () {
            var stat = fs.statSync(path.join(out, '.bin/mocha'));
            assert.equal(beforeStat.mode, stat.mode);
        });

    });

    describe('should NOT copy node_modules', function() {
        var out = path.join(to, '1'),
            data;

        before(function(done) {
            cpr(from, out, {
                filter: /node_modules/
            }, function(err) {
                fs.stat(out, function(e, stat) {
                    data = {
                        err: err,
                        stat: e
                    };
                    done();
                });
            });
        });

        it('does not have ./out/1', function() {
            assert.ok(data.stat); // Should be an error
        });
        it('threw an error', function() {
            assert(data.err instanceof Error); // Should be an error
            assert.equal(data.err.message, 'No files to copy');
        });

    });

    describe('should not copy yui-lint from regex', function() {
        var out = path.join(to, '2'),
            data;

        before(function(done) {
            cpr(from, out, {
                confirm: true,
                overwrite: true,
                filter: /yui-lint/
            }, function(err, status) {
                data = {
                    status: status,
                    dirs: {
                        from: fs.readdirSync(from).sort(),
                        to: fs.readdirSync(out).sort()
                    }
                };
                done();
            });
        });

        it('returns files array with confirm', function() {
            assert.ok(Array.isArray(data.status));
            assert.ok(data.status.length > 0);
        });
        it('has ./out/2', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });
        it('dirs are not equal', function() {
            assert.notDeepEqual(data.dirs.to, data.dirs.from);
        });
        it('from directory has yui-lint dir', function() {
            var fromHasLint = data.dirs.from.some(function(item) {
                return (item === 'yui-lint');
            });
            assert.equal(true, fromHasLint);
        });
        it('to directory does not have yui-lint dir', function() {
            var toHasLint = data.dirs.to.some(function(item) {
                return (item === 'yui-lint');
            });
            assert.equal(false, toHasLint);
        });
    });

    describe('should not copy directory from function', function() {
        var out = path.join(to, '3'),
            data;

        before(function(done) {
            cpr(from, out, {
                confirm: true,
                deleteFirst: true,
                filter: function (item) {
                    return !(/data/.test(item));
                }
            }, function(err, status) {
                data = {
                    status: status,
                    dirs: {
                        from: fs.readdirSync(path.join(from, 'jshint/')).sort(),
                        to: fs.readdirSync(path.join(out, 'jshint/')).sort()
                    }
                };
                done();
            });
        });

        it('has ./out/3', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });
        it('dirs are not equal', function() {
            assert.notDeepEqual(data.dirs.to, data.dirs.from);
        });
        it('from directory has data dir', function() {
            var fromHas = data.dirs.from.some(function(item) {
                return (item === 'data');
            });
            assert.equal(true, fromHas);
        });
        it('to directory does not have data dir', function() {
            var toHas = data.dirs.to.some(function(item) {
                return (item === 'data');
            });
            assert.equal(false, toHas);
        });

    });

    describe('should copy shelljs from bad filter', function() {
        var out = path.join(to, '4'),
            data;

        before(function(done) {
            cpr(from, out, {
                confirm: true,
                deleteFirst: true,
                filter: 'bs content'
            }, function(err, status) {
                data = {
                    status: status,
                    dirs: {
                        from: fs.readdirSync(path.join(from, 'jshint/node_modules')).sort(),
                        to: fs.readdirSync(path.join(out, 'jshint/node_modules')).sort()
                    }
                };
                done();
            });
        });
        it('has ./out/4', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });
        it('dirs are not equal', function() {
            assert.deepEqual(data.dirs.to, data.dirs.from);
        });
        it('from directory has shelljs dir', function() {
            var fromHas = data.dirs.from.some(function(item) {
                return (item === 'shelljs');
            });
            assert.equal(true, fromHas);
        });
        it('to directory does have shelljs dir', function() {
            var toHas = data.dirs.to.some(function(item) {
                return (item === 'shelljs');
            });
            assert.equal(true, toHas);
        });

    });

    describe('should copy node_modules with overwrite flag', function() {
        var out = path.join(to, '4'),
            data;

        before(function(done) {
            cpr(from, out, function() {
                cpr(from, out, {
                    overwrite: true,
                    confirm: true
                }, function(err, status) {
                    data = {
                        status: status,
                        dirs: {
                            from: fs.readdirSync(from).sort(),
                            to: fs.readdirSync(out).sort()
                        }
                    };
                    done();
                });
            });
        });

        it('should return files array', function() {
            assert.ok(Array.isArray(data.status));
            assert.ok(data.status.length > 0);
        });
        it('has ./out/0', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });
        it('dirs are equal', function() {
            assert.deepEqual(data.dirs.to, data.dirs.from);
        });
        it('from directory has graceful-fs dir', function() {
            var fromHasGFS = data.dirs.from.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, fromHasGFS);
        });
        it('to directory has graceful-fs dir', function() {
            var toHasGFS = data.dirs.to.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, toHasGFS);
        });

    });

    describe('error handling', function() {

        it('should fail on non-existant from dir', function(done) {
            cpr('./does/not/exist', path.join(to, 'does/not/matter'), function(err, status) {
                assert.equal(undefined, status);
                assert(err instanceof Error);
                assert.equal('From should be a file or directory', err.message);
                done();
            });
        });

        it('should fail on non-file', function(done) {
            cpr('/dev/null', path.join(to, 'does/not/matter'), function(err, status) {
                assert.equal(undefined, status);
                assert(err instanceof Error);
                assert.equal('From should be a file or directory', err.message);
                done();
            });
        });

        it('should return an error if a directory is to write over an existing file with the same name', function(done) {
            mkdirp.sync(path.join(to, 'empty-src2', 'a'));
            mkdirp.sync(path.join(to, 'empty-dest2'));
            fs.writeFileSync(path.join(to, 'empty-dest2', 'a'), 'FILE');
            cpr(path.join(to, 'empty-src2'), path.join(to, 'empty-dest2'), { overwrite: true }, function(errs) {
                var stat = fs.statSync(path.join(to, 'empty-dest2'));
                assert.ok(stat.isDirectory());
                assert.ok(errs);
                assert.ok(errs.list);
                assert.ok(errs.list[0]);
                assert.ok(errs.list[0].message.match(/exists and is not a directory, can not create/));
                assert.equal(errs.list[0].code, 'ENOTDIR');
                assert.equal(errs.list[0].errno, 27);
                done();
            });
        });


        it('should fail without write permissions', function(done) {
            /*istanbul ignore next - This is for docker perms*/
            (function() {
                if (process.getuid && process.getuid() === 0) {
                    console.log('Skipping this test as root has all access..');
                    return done();
                }
                var baddir = path.join(to, 'readonly');
                mkdirp.sync(baddir);
                fs.chmodSync(baddir, '555');
                cpr(path.join(from, '.bin'), baddir, function(errs, status) {
                    assert.ok(errs);
                    assert.ok(errs.list);
                    assert.ok(errs.list[0]);
                    assert.ok(errs.message.match(/Unable to copy directory entirely/));
                    done();
                });
            })();
        });

    });

    describe('validations', function() {

        it('should copy empty directory', function(done) {
            mkdirp.sync(path.join(to, 'empty-src'));
            cpr(path.join(to, 'empty-src'), path.join(to, 'empty-dest'), function() {
                var stat = fs.statSync(path.join(to, 'empty-dest'));
                assert.ok(stat.isDirectory());
                done();
            });
        });

        it('should not delete existing folders in out dir', function(done) {
            mkdirp.sync(path.join(to, 'empty-src', 'a'));
            mkdirp.sync(path.join(to, 'empty-dest', 'b'));
            cpr(path.join(to, 'empty-src'), path.join(to, 'empty-dest'), { overwrite: true }, function() {
                var stat = fs.statSync(path.join(to, 'empty-dest'));
                assert.ok(stat.isDirectory());
                var dirs = fs.readdirSync(path.join(to, 'empty-dest'));
                assert.equal(dirs[0], 'a');
                assert.equal(dirs[1], 'b');
                done();
            });
        });

        it('should copy one file', function(done) {
            cpr(__filename, path.join(to, 'one-file-test.js'), { overwrite: true }, function(err) {
                assert.equal(undefined, err);
                var stat = fs.statSync(path.join(to, 'one-file-test.js'));
                assert.ok(stat.isFile());
                done();
            });
        });

        it('should copy one file in dir if to has trailing sep', function(done) {
            cpr(__filename, path.join(to, 'one-file-dir'+path.sep), { overwrite: true }, function(err) {
                assert.equal(undefined, err);
                var stat = fs.statSync(path.join(to, 'one-file-dir','full.js'));
                assert.ok(stat.isFile());
                done();
            });
        });

        it('should not copy because file exists', function(done) {
            cpr(__filename, path.join(to, 'one-file-test.js'), function(err, status) {
                assert.equal(undefined, status);
                assert(err instanceof Error);
                assert.ok(/^File .* exists$/.test(err.message));
                assert.equal(err.code, 'EEXIST');
                assert.equal(err.errno, 47);
                done();
            });
        });

    });

    describe('should work as a standalone bin', function() {
        var out = path.join(to, '4'),
            data;

        before(function(done) {
            exec('node ./bin/cpr ' + from + ' ' + out, function(err) {
              data = {
                  dirs: {
                      from: fs.readdirSync(from).sort(),
                      to: fs.readdirSync(out).sort()
                  }
              };
              done();
            });
        });

        it('has ./out/4', function() {
            var stat = fs.statSync(out);
            assert.ok(stat.isDirectory());
        });
        it('dirs are equal', function() {
            assert.deepEqual(data.dirs.to, data.dirs.from);
        });
        it('from directory has graceful-fs dir', function() {
            var fromHasGFS = data.dirs.from.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, fromHasGFS);
        });
        it('to directory has graceful-fs dir', function() {
            var toHasGFS = data.dirs.to.some(function(item) {
                return (item === 'graceful-fs');
            });
            assert.equal(true, toHasGFS);
        });

    });

});
