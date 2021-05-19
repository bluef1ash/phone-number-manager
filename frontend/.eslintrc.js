module.exports = {
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 6,
    sourceType: 'module',
    ecmaFeatures: {
      tsx: true
    }
  },
  extends: ['prettier'],
  plugins: ['@typescript-eslint'],
  rules: {
    // 禁止使用 var
    'no-var': 'error',
    // 优先使用 interface 而不是 type
    '@typescript-eslint/consistent-type-definitions': ['error', 'interface'],
    'no-submodule-imports': 'off',
    'object-literal-sort-keys': 'off',
    'jsx-no-lambda': 'off',
    //  Enforces function overloads to be consecutive.
    '@typescript-eslint/adjacent-overload-signatures': 'error',
    //禁止逗号运算符。
    'ban-comma-operator': 'off',
    //禁止类型
    'ban-type': 'off',
    //不需使用any类型
    'no-any': 'off',
    //禁止导入带有副作用的语句
    'no-import-side-effect': 'off',
    //不允许将变量或参数初始化为数字，字符串或布尔值的显式类型声明。
    'no-inferrable-types': 'off',
    //不允许内部模块
    'no-internal-module': 'off',
    //不允许在变量赋值之外使用常量数值。当没有指定允许值列表时，默认允许-1,0和1
    'no-magic-numbers': ['error', { ignore: [-1, 0, 1, 2, 3] }],
    //类型声明的冒号之前是否需要空格
    'typedef-whitespace': 'off',
    //for if do while 要有括号
    curly: 'error',
    //不允许使用按位运算符
    'no-bitwise': 'off',
    //不允许在do-while/for/if/while判断语句中使用赋值语句
    'no-conditional-assignment': 'off',
    //不能使用console
    'no-console': 'off',
    //不允许使用 String/Number/Boolean的构造函数
    'no-construct': 'off',
    //不允许使用debugger
    'no-debugger': 'error',
    //不允许空的块
    'no-empty': 'error',
    //不允许使用eval
    'no-eval': 'error',
    //不允许在非class中使用 this关键字
    'no-invalid-this': 'error',
    //禁止定义构造函数或new class
    'no-misused-new': 'off',
    //禁止object出现在类型断言表达式中
    'no-object-literal-type-assertion': 'off',
    //不允许return await
    'no-return-await': 'error',
    //箭头函数定义的参数需要括号
    'arrow-parens': ['error', 'as-needed'],
    //引号的使用规则
    quotes: ['error', 'single', { avoidEscape: true }],
    //分号的使用规则
    semi: ['error', 'never'],
    //使用Tab进行缩进，每次强制缩进2个字符
    indent: ['error', 2, { SwitchCase: 1 }],
    //空格的校验
    whitespace: 'off',
    //要求指定的标记与它们之前的表达式位于同一行
    'one-line': 'off',
    eqeqeq: ['error', 'always'],
    'jsx-quotes': ['error', 'prefer-double']
  },
  env: {
    browser: true,
    node: true,
    commonjs: true,
    'shared-node-browser': true,
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
}
