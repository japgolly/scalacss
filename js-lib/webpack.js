const
  Path         = require('path'),
  TerserPlugin = require('terser-webpack-plugin'),
  Webpack      = require('webpack'),
  NodeModules  = Path.resolve(__dirname, 'node_modules');

const config = {

  entry: {
    'scalacss': [
      './src/main',
    ]
  },

  output: {
    path: Path.resolve(__dirname, 'output'),
    filename: '[name].js',
    libraryTarget: 'this',
  },

  resolve: { modules: [NodeModules] },
  resolveLoader: { modules: [NodeModules] },

  mode: 'production',
  target: 'node',

  performance: {
    hints: false
  },

  optimization: {
    minimizer: [new TerserPlugin({
      cache: true,
      parallel: true,
      terserOptions: {
        output: {
          comments: false,
        }
      },
    })]
  },

  plugins: [
    new Webpack.LoaderOptionsPlugin({
      minimize: true,
    }),
  ],

  bail: true,
};

module.exports = config;
