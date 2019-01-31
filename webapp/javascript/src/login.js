require([
    "vue",
    "commonFunction",
    "pace",
    "bootstrap",
    "layui",
    "sha",
    "perfect-scrollbar",
    "coreUi",
    "geeTest"], function(Vue, commonFunction, pace) {
    pace.start({
        document: false
    });
    new Vue({
        el: "#login",
        data: {
            username: "",
            password: "",
            token: token,
            isInvalids: [false, false, false, false],
            messages: ["", "", "正在加载验证码，请稍后。。。"],
            captchaObj: null,
            isCaptcha: "block",
            isSubmit: false,
            browserType: ""
        },
        directives: {
            focus: {
                // 指令的定义
                inserted: function(el) {
                    el.focus();
                }
            }
        },
        mounted: function() {
            var _this = this;
            this.browserType = commonFunction.browserType() ? "web" : "h5";
            $.ajax({
                url: "/login/captcha?browserType=" +
                    this.browserType + "&time=" +
                    (new Date()).getTime(),
                type: "get",
                dataType: "json",
                success: function(data) {
                    // 使用initGeetest接口
                    initGeetest({
                        gt: data.gt,
                        challenge: data.challenge,
                        product: "float",
                        offline: !data.success,
                        new_captcha: true,
                        width: "2rem"
                    }, function(captchaObj) {
                        _this.captchaObj = captchaObj;
                        _this.captchaObj.bindForm("#login_form");
                        _this.captchaObj.appendTo("#captcha");
                        _this.captchaObj.onReady(function() {
                            _this.isCaptcha = "none";
                        }).onSuccess(function() {
                            return _this.login();
                        }).onError(function() {
                            _this.$set(_this.messages, 2, "加载图形验证码失败，请稍后再试！");
                        });
                    });
                }
            });
        },
        methods: {
            enterNext: function() {

            },
            /**
             * 登录事件
             */
            login: function() {
                var _this = this;
                if (this.username === "") {
                    this.$set(this.isInvalids, 0, true);
                    this.$set(this.messages, 0, "用户名称不能为空！");
                    return false;
                } else {
                    this.$set(this.isInvalids, 0, false);
                }
                if (this.username.length > 10) {
                    this.$set(this.isInvalids, 0, true);
                    this.$set(this.messages, 0, "用户名称不能超过10个字符！");
                    return false;
                } else {
                    this.$set(this.isInvalids, 0, false);
                }
                if (this.password === "") {
                    this.$set(this.isInvalids, 1, true);
                    this.$set(this.messages, 1, "用户密码不能为空！");
                    return false;
                } else if (this.password.length < 6) {
                    this.$set(this.isInvalids, 1, true);
                    this.$set(this.messages, 1, "用户密码不能少于6个字符！");
                    return false;
                } else {
                    this.$set(this.isInvalids, 1, false);
                }
                var captchaValid = this.captchaObj.getValidate();
                if (typeof captchaValid === "undefined" ||
                    !captchaValid) {
                    this.$set(this.messages, 2, "必须进行验证身份！");
                    this.isCaptcha = "block";
                    return false;
                } else {
                    this.$set(this.isInvalids, 0, false);
                }
                layui.use("layer", function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        var indexLayer = 0;
                        $.ajax({
                            url: "/login/ajax",
                            method: "post",
                            async: true,
                            data: {
                                username: _this.username,
                                password: sha256(_this.password),
                                _token: _this.token,
                                geetest_challenge: captchaValid.geetest_challenge,
                                geetest_validate: captchaValid.geetest_validate,
                                geetest_seccode: captchaValid.geetest_seccode,
                                browserType: _this.browserType
                            },
                            beforeSend: function(xmlHttpRequest) {
                                indexLayer = layer.msg("正在登录。。。", {
                                    time: 0,
                                    shade: [
                                        0.8,
                                        "#393D49"]
                                });
                            },
                            success: function(data) {
                                layer.close(indexLayer);
                                if (data.state === 1) {
                                    layer.msg(data.message, {
                                        icon: 6,
                                        shade: [0.8, "#393D49"]
                                    });
                                    location.href = "/";
                                } else {
                                    var messageError = data.messageError;
                                    layer.msg(messageError.defaultMessage, {icon: 5});
                                    var invalidIndex = 0;
                                    if (messageError.field === "password") {
                                        invalidIndex = 1;
                                    } else if (messageError.field ===
                                        "captcha") {
                                        invalidIndex = 2;
                                        _this.isCaptcha = "block";
                                    }
                                    _this.$set(_this.isInvalids, invalidIndex, true);
                                    _this.$set(_this.messages, invalidIndex, messageError.defaultMessage);
                                    _this.captchaObj.reset();
                                }
                            }
                        });
                    });
                });
            }
        }
    });
});
