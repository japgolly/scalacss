var path = require('path');
var assert = require('assert');

var I18n = require('../');

describe('I18n', function() {
    var i18n = I18n();

    describe('.load', function() {
        it('should read from directory', function() {
            i18n.load(path.resolve(__dirname, 'locales'));

            assert.deepEqual(i18n.locales(), ['en', 'fr']);
        });
    });

    describe('.set', function() {
        it('should extend locales', function() {
            i18n.set({
                'en-gb': {
                    'HELLO': 'Hello Sir {{name}}'
                }
            });

            assert.deepEqual(i18n.locales(), ['en', 'fr', 'en-gb']);
        });
    });

    describe('.resolve', function() {
        it('should resolve non-existing locales', function() {
            assert.equal(i18n.resolve('en-us'), 'en');
            assert.equal(i18n.resolve('fr-ca'), 'fr');
        });

        it('should resolve existing locales', function() {
            assert.equal(i18n.resolve('en-gb'), 'en-gb');
            assert.equal(i18n.resolve('en'), 'en');
        });

        it('should default to en', function() {
            assert.equal(i18n.resolve('zh'), 'en');
        });
    });

    describe('.t', function() {
        it('should translate without args', function() {
            assert.equal(i18n.t('en', 'WORLD'), 'World');
        });

        it('should translate with kwargs', function() {
            assert.equal(i18n.t('en', 'HELLO', { name: 'Samy' }), 'Hello Samy');
        });

        it('should translate with args', function() {
            assert.equal(i18n.t('en', 'CATS', 10), '10 cats');
        });

        it('should default non-existing locales to en', function() {
            assert.equal(i18n.t('zh', 'WORLD'), 'World');
        });

        it('should default non-existing translation to en', function() {
            assert.equal(i18n.t('fr', 'CATS', 10), '10 cats');
        });
    });

});


