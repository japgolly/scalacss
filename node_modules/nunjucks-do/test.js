require('should');

var nunjucks = require('nunjucks');
var DoExtension = require('./')(nunjucks);

describe('nunjucks-do', function() {
    var env = new nunjucks.Environment(new nunjucks.FileSystemLoader('views'));
    env.addExtension('DoExtension', new DoExtension());

    it('should eval js', function() {
        env.renderString('Hello {% do %}{% enddo %}World').should.equal('Hello World');
    });

    it('should eval js in context', function() {
        env.renderString('{% do %}test = "So " + test{% enddo %}{{ test }}', {
            test: 'Cool'
        }).should.equal('So Cool');
    });

});
