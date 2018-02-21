require.config({
    baseUrl: jsPath + "applications/",
    paths: {
        css: jsPath + "libraries/requirejs/plugin/css.min",
        domReady: jsPath + "libraries/requirejs/plugin/dom-ready.min",
        jquery: jsPath + "libraries/jquery/jquery-3.X.min",
        vue: jsPath + "libraries/vue.min",
        bootstrap: jsPath + "libraries/bootstrap.min",
        easyui: jsPath + "libraries/jquery/jquery-easyui/jquery.easyui.min",
        easyui_zhCN: jsPath + "libraries/jquery/jquery-easyui/locale/easyui-lang-zh_CN",
        ztree: jsPath + "libraries/ztree/js/jquery.ztree.all.min",
        lodash: jsPath + "libraries/lodash/lodash.min",
        layui: jsPath + "libraries/layui/layui",
        chart: jsPath + "libraries/chart.min",
        zeroClipboard: jsPath + "libraries/zeroclipboard/ZeroClipboard.min",
        md5: jsPath + "libraries/md5.min",
        sha: jsPath + "libraries/sha256.min",
        commonFunction: jsPath + "libraries/common"
    },
    shim: {
        jquery: {
            exports: "$"
        },
        vue: {
            exports: "Vue"
        },
        easyui: {
            deps: ["jquery", "css!" + jsPath + "libraries/jquery/jquery-easyui/themes/default/easyui.css", "css!" + jsPath + "libraries/jquery/jquery-easyui/themes/icon.css", "easyui_zhCN"]
        },
        easyui_zhCN: {
            deps: ["jquery"]
        },
        ztree: {
            deps: ["jquery", "css!" + jsPath + "libraries/ztree/css/zTreeStyle/zTreeStyle.css"]
        },
        bootstrap: {
            deps: ["jquery"]
        },
        layui: {
            deps: ["jquery", "css!" + jsPath + "libraries/layui/css/layui.css", "css!" + jsPath + "libraries/layui/css/layui.mobile.css"],
            exports: "layui",
            init: function () {
                return this.layui.config({
                    dir: jsPath + "libraries/layui/"
                });
            }
        },
        md5: {
            exports: "md5"
        },
        sha: {
            exports: "sha"
        }
    }
});
