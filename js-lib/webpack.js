const
  Path         = require('path'),
  TerserPlugin = require('terser-webpack-plugin'),
  Webpack      = require('webpack'),
  WebpackMerge = require('webpack-merge'),
  NodeModules  = Path.resolve(__dirname, 'node_modules');

const prod = true;

const mainConfig = {

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

  target: 'web',
  node: {
    fs: 'empty',
    path: true,
  },

  resolve: {
    alias: {
      cosmiconfig: Path.resolve(__dirname, 'src/hacks/cosmiconfig'),
    },
  },


  externals: {
    module: "module",
  },

  bail: true,
};

const devConfig = {
  mode: 'development',
};

const prodConfig = {

  mode: 'production',

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

};

const config = WebpackMerge(mainConfig, prod ? prodConfig : devConfig)

module.exports = config;
