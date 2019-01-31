require.config({
    baseUrl: "/javascript/dist/",
    paths: {
        css: "../../lib/requirejs/plugin/css.min",
        domReady: "../../lib/requirejs/plugin/dom-ready.min",
        jquery: "../../lib/jquery/jquery-3.X.min",
        vue: "../../lib/vue",
        bootstrap: "../../lib/bootstrap/js/bootstrap.bundle.min",
        easyui: "../../lib/jquery/jquery-easyui/jquery.easyui.min",
        ztree: "../../lib/ztree/js/jquery.ztree.all.min",
        lodash: "../../lib/lodash/lodash.min",
        layui: "../../lib/layui/layui",
        chart: "../../lib/chart.min",
        zeroClipboard: "../../lib/zeroclipboard/ZeroClipboard.min",
        md5: "../../lib/md5.min",
        sha: "../../lib/sha256.min",
        coreUi: "../../lib/coreui/js/coreui.min",
        popper: "../../lib/popper.min",
        pace: "../../lib/pace/pace.min",
        "perfect-scrollbar": "../../lib/perfect-scrollbar.min",
        geeTest: "../../lib/gt",
        commonFunction: "../../lib/common"
    },
    shim: {
        jquery: {
            exports: "$"
        },
        vue: {
            exports: "Vue"
        },
        easyui: {
            deps: [
                "jquery",
                "../../lib/jquery/jquery-easyui/locale/easyui-lang-zh_CN"]
        },
        ztree: {
            deps: ["jquery"]
        },
        bootstrap: {
            deps: ["jquery"]
        },
        popper: {
            exports: "popper"
        },
        layui: {
            deps: ["jquery"],
            exports: "layui",
            init: function() {
                return this.layui.config({
                    dir: "/lib/layui/"
                });
            }
        },
        md5: {
            exports: "md5"
        },
        sha: {
            exports: "sha"
        },
        coreUi: {
            exports: "coreUi",
            deps: ["jquery", "bootstrap"]
        },
        pace: {
            exports: "pace",
            init: function() {
                return pace.start({
                    document: false
                });
            }
        }
    }
});
