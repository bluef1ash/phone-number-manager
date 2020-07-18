const path = require("path");
const resourcesPath = path.resolve(__dirname, "src/main/resources");
const staticPath = path.resolve(__dirname, "src/main/resources/static");
const jsSrcPath = path.join(resourcesPath, "javascript");
const jsLibPath = path.resolve(__dirname, "lib/javascript");
const jsDistPath = path.join(staticPath, "javascript");
const Webpack = require("webpack");
const VueLoaderPlugin = require("vue-loader/lib/plugin");
const devMode = process.env.NODE_ENV === "development";

/**
 * 文件加载
 * @param type
 * @param limit
 * @return {{loader: string, options: {outputPath: string, name: string, limit: number, publicPath: string}}}
 */
function fileLoaderOptions(type, limit = 1024 * 10) {
    let publicPath = "/" + type;
    if (devMode) {
        limit = 100000000000000;
    }
    return {
        loader: "url-loader",
        options: {
            name: "[name]-[hash:5].min.[ext]",
            limit,
            outputPath: path.relative(jsDistPath, staticPath + "/" + type),
            publicPath
        }
    };
}

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
                test: /\.(s[ac]ss)$/,
                use: ["style-loader", "css-loader", "postcss-loader", "sass-loader", "resolve-url-loader"]
            },
            {
                test: /\.(png|jp[e]?g|gif)$/,
                use: [fileLoaderOptions("images")]
            },
            {
                test: /\.(eot|woff[2]?|ttf|svg)(v=\d+)?$/,
                use: [fileLoaderOptions("fonts")]
            },
            {
                test: /\.vue$/,
                use: ["vue-loader"]
            }
        ]
    },
    plugins: [
        new Webpack.BannerPlugin("@author 廿二月的天"),
        new Webpack.ProvidePlugin({
            "$": "jquery"
        }),
        new VueLoaderPlugin()
    ],
    resolve: {
        alias: {
            "vue$": "vue/dist/vue.esm.js",
            "@base": __dirname,
            "@baseSrc": resourcesPath,
            "@scss": path.join(resourcesPath, "scss"),
            "@jsSrc": jsSrcPath,
            "@library": path.join(__dirname, "library")
        },
        modules: [
            path.resolve("node_modules")
        ],
        extensions: [".js", ".css", ".vue", ".scss", ".json"]
    }
};
