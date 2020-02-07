const path = require("path");
const jsDistPath = path.resolve(
    __dirname,
    "src/main/resources/static/javascript"
);
const webpackMerge = require("webpack-merge");
const common = require("./webpack.common");
module.exports = webpackMerge(common, {
    mode: "development",
    devtool: "inline-source-map",
    devServer: {
        contentBase: jsDistPath,
        host: "localhost",
        port: 3000
    },
    performance: {
        hints: "warning",
        maxAssetSize: 50 * 1024 * 1024,
        maxEntrypointSize: 100 * 1024 * 1024,
        assetFilter(assetFilename) {
            return assetFilename.endsWith(".css") || assetFilename.endsWith(".js");
        }
    }
});
