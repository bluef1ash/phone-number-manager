const path = require("path");
const jsSrcPath = path.resolve(__dirname, "src/main/resources/javascript");
const jsLibPath = path.resolve(__dirname, "lib/javascript");
const jsDistPath = path.resolve(__dirname, "src/main/resources/static/javascript");
const fontDistPath = path.resolve(__dirname, "src/main/resources/static/fonts");
const Webpack = require("webpack");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
    entry: {
        "error": path.join(jsSrcPath, "common/error.js"),
        "login": path.join(jsSrcPath, "login/login.js"),
        "index": path.join(jsSrcPath, "index/index.js"),
        "user-list": path.join(jsSrcPath, "user/list.js"),
        "user-edit": path.join(jsSrcPath, "user/edit.js"),
        "role-list": path.join(jsSrcPath, "role/list.js"),
        "role-edit": path.join(jsSrcPath, "role/edit.js"),
        "privilege-list": path.join(jsSrcPath, "privilege/list.js"),
        "privilege-edit": path.join(jsSrcPath, "privilege/edit.js"),
        "configuration-list": path.join(jsSrcPath, "configuration/list.js"),
        "configuration-edit": path.join(jsSrcPath, "configuration/edit.js"),
        "subdistrict-list": path.join(jsSrcPath, "subdistrict/list.js"),
        "subdistrict-edit": path.join(jsSrcPath, "subdistrict/edit.js"),
        "community-list": path.join(jsSrcPath, "community/list.js"),
        "community-edit": path.join(jsSrcPath, "community/edit.js"),
        "subcontractor-list": path.join(jsSrcPath, "subcontractor/list.js"),
        "subcontractor-edit": path.join(jsSrcPath, "subcontractor/edit.js"),
        "resident-list": path.join(jsSrcPath, "resident/list.js"),
        "resident-edit": path.join(jsSrcPath, "resident/edit.js"),
        "dormitory-list": path.join(jsSrcPath, "dormitory/list.js"),
        "dormitory-edit": path.join(jsSrcPath, "dormitory/edit.js")
    },
    output: {
        path: jsDistPath,
        filename: "[name]-bundle.js"
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                include: [jsSrcPath, jsLibPath],
                use: "babel-loader?cacheDirectory"
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader", "resolve-url-loader"]
            },
            {
                test: /\.less$/,
                use: ["style-loader", "css-loader", "postcss-loader", "less-loader", "resolve-url-loader"]
            },
            {
                test: /\.(s[ac]ss)$/,
                use: ["style-loader", "css-loader", "postcss-loader", "sass-loader", "resolve-url-loader"]
            },
            {
                test: /\.(png|jp[e]?g|gif)$/,
                use: [
                    {
                        loader: require.resolve("url-loader"),
                        options: {
                            name: "[name]-[hash:5].min.[ext]",
                            limit: 1024 * 10,
                            outputPath: path.relative(jsDistPath, path.resolve(__dirname, "src/main/resources/static/images")),
                            publicPath: "/images"
                        }
                    }
                ]
            },
            {
                test: /\.(eot|woff[2]?|ttf|svg)(v=\d+)?$/,
                use: [
                    {
                        loader: "url-loader",
                        options: {
                            name: "[name]-[hash:5].min.[ext]",
                            limit: 1024 * 10,
                            outputPath: path.relative(jsDistPath, fontDistPath),
                            publicPath: "/fonts"
                        }
                    }
                ]
            },
            {
                test: /\.vue$/,
                use: ["vue-loader"]
            }
        ]
    },
    node: {
        fs: "empty"
    },
    plugins: [
        new Webpack.BannerPlugin("@author 廿二月的天"),
        new Webpack.ProvidePlugin({
            "$": "jquery"
        }),
        new CleanWebpackPlugin([jsDistPath, fontDistPath]),
        new VueLoaderPlugin()
    ],
    resolve: {
        alias: {
            "vue$": "vue/dist/vue.esm.js",
            "@base": __dirname,
            "@baseSrc": path.resolve(__dirname, "src/main/resources")
        },
        modules: [
            path.resolve("node_modules")
        ],
        extensions: [".js", ".css", ".vue", ".json"]
    }
};
