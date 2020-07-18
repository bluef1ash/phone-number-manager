const path = require("path");
const jsDistPath = path.resolve(
    __dirname,
    "src/main/resources/static/javascript"
);
const webpackMerge = require("webpack-merge");
const common = require("./webpack.common");
const PurifyCssWebpack = require("purifycss-webpack");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const glob = require("glob-all");
const TerserWebpackPlugin = require("terser-webpack-plugin");
const OptimizeCssAssetsWebpackPlugin = require("optimize-css-assets-webpack-plugin");
module.exports = webpackMerge(common, {
    mode: "production",
    plugins: [
        new PurifyCssWebpack({
            paths: glob.sync([
                path.join(__dirname, "src/main/resources/templates/**/!*.html"),
                path.join(jsDistPath, "!*.js")
            ])
        }),
        new CleanWebpackPlugin({
            cleanOnceBeforeBuildPatterns: [
                "**/*",
                path.join(process.cwd(), "Assets/static/css/*"),
                path.join(process.cwd(), "Assets/static/fonts/*")
            ],
            dangerouslyAllowCleanPatternsOutsideProject: true,
            dry: true,
            protectWebpackAssets: true,
            verbose: true
        })
    ],

    optimization: {
        minimize: true,
        moduleIds: "hashed",
        splitChunks: {
            minChunks: 1,
            cacheGroups: {}
        },
        minimizer: [
            new TerserWebpackPlugin({
                test: /\.js.*$/i,
                parallel: true,
                sourceMap: false,
                extractComments: false,
                terserOptions: {
                    compress: {
                        warnings: false,
                        drop_console: true,
                        drop_debugger: true,
                        pure_funcs: []
                    },
                    output: {
                        comments: false
                    }
                }
            }),
            new OptimizeCssAssetsWebpackPlugin({
                assetNameRegExp: /\.css$/g,
                cssProcessor: require("cssnano"),
                canPrint: true
            })
        ]
    }
});
