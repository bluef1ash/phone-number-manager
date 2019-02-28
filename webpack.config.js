const path = require("path");
const jsDistPath = path.resolve(__dirname, "webapp/javascript/dist");
const Webpack = require("webpack");
const HappyPack = require("happypack");
const happyThreadPool = HappyPack.ThreadPool({size: 5});
const glob = require("glob-all");
const ParallelUglifyPlugin = require("webpack-parallel-uglify-plugin");
const PurifyCssWebpack = require("purifycss-webpack");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
    entry: {
        "error": path.join(__dirname, "webapp/javascript/src/common/error.js"),
        "login": path.join(__dirname, "webapp/javascript/src/login/login.js"),
        "index": path.join(__dirname, "webapp/javascript/src/index/index.js"),
        "system-user-list": path.join(__dirname, "webapp/javascript/src/user/list.js"),
        "system-user-edit": path.join(__dirname, "webapp/javascript/src/user/edit.js"),
        "role-list": path.join(__dirname, "webapp/javascript/src/role/list.js"),
        "role-edit": path.join(__dirname, "webapp/javascript/src/role/edit.js"),
        "privilege-list": path.join(__dirname, "webapp/javascript/src/privilege/list.js"),
        "privilege-edit": path.join(__dirname, "webapp/javascript/src/privilege/edit.js"),
        "configuration-list": path.join(__dirname, "webapp/javascript/src/configuration/list.js"),
        "configuration-edit": path.join(__dirname, "webapp/javascript/src/configuration/edit.js"),
        "subdistrict-list": path.join(__dirname, "webapp/javascript/src/subdistrict/list.js"),
        "subdistrict-edit": path.join(__dirname, "webapp/javascript/src/subdistrict/edit.js"),
        "community-list": path.join(__dirname, "webapp/javascript/src/community/list.js"),
        "community-edit": path.join(__dirname, "webapp/javascript/src/community/edit.js"),
        "subcontractor-list": path.join(__dirname, "webapp/javascript/src/subcontractor/list.js"),
        "subcontractor-edit": path.join(__dirname, "webapp/javascript/src/subcontractor/edit.js"),
        "resident-list": path.join(__dirname, "webapp/javascript/src/resident/list.js"),
        "resident-edit": path.join(__dirname, "webapp/javascript/src/resident/edit.js")
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
                include: path.resolve(__dirname, "webapp/javascript/src"),
                use: "happypack/loader?id=babel"
            },
            {
                test: /\.css$/,
                use: "happypack/loader?id=css"
            },
            {
                test: /\.less$/,
                use: "happypack/loader?id=less"
            },
            {
                test: /\.(s[ac]ss)$/,
                // use: "happypack/loader?id=sass"
                use: ["style-loader", "css-loader", "postcss-loader", "sass-loader", "resolve-url-loader"]
            },
            {
                test: /\.(png|jp[e]?g|gif)$/,
                use: "happypack/loader?id=image"
            },
            {
                test: /\.(eot|woff[2]?|ttf|svg)(v=\d+)?$/,
                // use: "happypack/loader?id=font"
                use: [
                    {
                        loader: require.resolve("url-loader"),
                        options: {
                            name: "[name]-[hash:5].min.[ext]",
                            limit: 1024 * 10,
                            outputPath: path.relative(jsDistPath, path.resolve(__dirname, "webapp/fonts")),
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
        new HappyPack({
            id: "babel",
            threadPool: happyThreadPool,
            loaders: ["babel-loader?cacheDirectory"]
        }),
        new HappyPack({
            id: "css",
            threadPool: happyThreadPool,
            loaders: ["style-loader", "css-loader", "resolve-url-loader"]
        }),
        new HappyPack({
            id: "less",
            threadPool: happyThreadPool,
            loaders: ["style-loader", "css-loader", "postcss-loader", "less-loader", "resolve-url-loader"]
        }),
        /*new HappyPack({
            id: "sass",
            threadPool: happyThreadPool,
            loaders: ["style-loader", "css-loader", "postcss-loader", "sass-loader", "resolve-url-loader"]
        }),*/
        new HappyPack({
            id: "image",
            threadPool: happyThreadPool,
            loaders: [
                {
                    loader: require.resolve("url-loader"),
                    options: {
                        name: "[name]-[hash:5].min.[ext]",
                        limit: 1024 * 10,
                        outputPath: path.relative(jsDistPath, path.resolve(__dirname, "webapp/images")),
                        publicPath: "/images"
                    }
                }
            ]
        }),
        /*new HappyPack({
            id: "font",
            threadPool: happyThreadPool,
            loaders: [
                {
                    loader: require.resolve("url-loader"),
                    options: {
                        name: "[name]-[hash:5].min.[ext]",
                        limit: 1024 * 10,
                        outputPath: path.relative(jsDistPath, path.resolve(__dirname, "webapp/fonts")),
                        publicPath: "fonts"
                    }
                }
            ]
        }),*/
        new Webpack.BannerPlugin("@author 廿二月的天"),
        new ParallelUglifyPlugin({
            workerCount: 4,
            uglifyES: {
                output: {
                    beautify: false, // 不需要格式化
                    comments: false // 保留注释
                },
                compress: { // 压缩
                    warnings: false, // 删除无用代码时不输出警告
                    drop_console: true, // 删除console语句
                    collapse_vars: true, // 内嵌定义了但是只有用到一次的变量
                    reduce_vars: true // 提取出出现多次但是没有定义成变量去引用的静态值
                }
            }
        }),
        new PurifyCssWebpack({
            paths: glob.sync([
                path.join(__dirname, "webapp/html/!*.html"),
                path.join(__dirname, "webapp/javascript/dist/!*.js")
            ])
        }),
        new Webpack.ProvidePlugin({//在每个模块中注入$
            "$": "jquery"
        }),
        new CleanWebpackPlugin(path.resolve(__dirname, "webapp/javascript/dist")),
        new VueLoaderPlugin()
    ],
    resolve: {
        alias: {
            "vue$": "vue/dist/vue.esm.js",
            "@base": path.resolve(__dirname, "webapp")
        },
        modules: [
            path.resolve("node_modules")
        ],
        extensions: [".js", ".css", ".vue", ".json"]
    }
};
