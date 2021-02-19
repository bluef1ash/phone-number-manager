module.exports = {
    parser: "@babel/eslint-parser",
    parserOptions: {
        ecmaVersion: 6,
        sourceType: "module",
        ecmaFeatures: {
            jsx: true
        }
    },
    extends: [
        "eslint:recommended",
        "prettier",
        "prettier/babel",
        "prettier/flowtype",
        "prettier/standard",
        "prettier/unicorn",
        "prettier/vue"
    ],
    plugins: [
        "babel",
        "import",
        "prettier"
    ],
    settings: {
        "import/resolver": {
            webpack: {
                config: "./webpack-common-config.js"
            }
        }
    },
    rules: {
        // 禁止使用 var
        "no-var": "error",
        //不允许在变量赋值之外使用常量数值。当没有指定允许值列表时，默认允许-1,0和1
        "no-magic-numbers": "off",
        //for if do while 要有括号
        curly: "error",
        //不允许使用按位运算符
        "no-bitwise": "off",
        //不允许在do-while/for/if/while判断语句中使用赋值语句
        "no-conditional-assignment": "off",
        //不能使用console
        "no-console": "off",
        //不允许使用debugger
        "no-debugger": "off",
        //不允许空的块
        "no-empty": "error",
        //不允许使用eval
        "no-eval": "off",
        //不允许在非class中使用 this关键字
        "no-invalid-this": "error",
        //不允许return await
        "no-return-await": "error",
        //箭头函数定义的参数需要括号
        "arrow-parens": ["error", "as-needed"],
        //引号的使用规则
        quotes: ["error", "double", { avoidEscape: true }],
        //分号的使用规则
        semi: ["error", "always"],
        //使用Tab进行缩进，每次强制缩进2个字符
        indent: ["error", 4, { SwitchCase: 1 }],
        eqeqeq: ["error", "always"]
    },
    env: {
        browser: true,
        node: true,
        commonjs: true,
        "shared-node-browser": true,
        es6: true,
        worker: true,
        mocha: true,
        jasmine: true,
        jest: true,
        phantomjs: true,
        protractor: true,
        qunit: true,
        jquery: true,
        prototypejs: true,
        shelljs: true,
        meteor: true,
        mongo: true,
        applescript: true,
        nashorn: true,
        serviceworker: true,
        atomtest: true,
        embertest: true,
        webextensions: true,
        greasemonkey: true
    }
};
