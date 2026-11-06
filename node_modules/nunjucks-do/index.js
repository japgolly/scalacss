var vm = require('vm');

module.exports = function(nunjucks) {
    return function DoExtension(_env) {
        this.tags = ['do'];

        this.parse = function(parser, nodes, lexer) {
            var tok = parser.nextToken();
            var args = parser.parseSignature(null, true);
            parser.advanceAfterBlockEnd(tok.value);
            var body = parser.parseUntilBlocks('enddo');
            parser.advanceAfterBlockEnd();

            return new nodes.CallExtension(this, 'run', args, [body]);
        };

        this.run = function(context, body) {
            var js = body();

            vm.runInNewContext(js, context.ctx);
            return '';
        };
    };
};
