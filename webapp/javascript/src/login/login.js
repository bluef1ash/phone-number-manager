import "@base/javascript/src/common/public";
import Vue from "vue";
import {Loading, Message, MessageBox} from "element-ui";
import sha256 from "sha256";
import VueCookie from "vue-cookie";
import commonFunction from "@base/lib/javascript/common";
import "@base/lib/javascript/gt";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.prototype.$alert = MessageBox.alert;
    Vue.prototype.$loading = Loading;
    Vue.use(Loading);
    Vue.prototype.$cookie = VueCookie;
    Vue.use(VueCookie);
    new Vue({
        el: "#login",
        data: {
            username: "",
            password: "",
            csrf: csrf,
            isInvalids: [false, false, false, false],
            messages: ["", "", "正在加载验证码，请稍后。。。"],
            captchaObj: null,
            isCaptcha: "block",
            browserType: "",
            loading: null
        },
        directives: {
            Loading,
            Message,
            focus: {
                inserted(el) {
                    el.focus();
                }
            }
        },
        mounted() {
            this.browserType = commonFunction.browserType() ? "web" : "h5";
            $.ajax({
                url: captchaUrl + "?browserType=" + this.browserType + "&time=" + (new Date()).getTime(),
                type: "get",
                dataType: "json"
            }).then(data => {
                initGeetest({
                    gt: data.gt,
                    challenge: data.challenge,
                    product: "float",
                    offline: !data.success,
                    new_captcha: true,
                    width: "2rem"
                }, (captchaObj) => {
                    this.captchaObj = captchaObj;
                    this.captchaObj.bindForm("#login_form");
                    this.captchaObj.appendTo("#captcha");
                    this.captchaObj.onReady(() => {
                        this.isCaptcha = "none";
                    }).onSuccess(() => {
                        return this.login();
                    }).onError(() => {
                        this.$set(this.messages, 2, "加载图形验证码失败，请稍后再试！");
                    });
                });
            });
            if (this.$cookie.get("sessionExpired") !== null) {
                this.$cookie.delete("sessionExpired");
                this.$alert("不允许同一台设备同时登录该系统！", "警告", {
                    center: true,
                    confirmButtonText: "知道了"
                });
            }
        },
        methods: {
            /**
             * 获取密码输入框焦点
             */
            passwordFocus: function() {
                this.$refs.password.focus();
            },
            /**
             * 登录事件
             */
            login() {
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
                let captchaValid = this.captchaObj.getValidate();
                if (typeof captchaValid === "undefined" || !captchaValid) {
                    this.$set(this.messages, 2, "必须进行验证身份！");
                    this.isCaptcha = "block";
                    return false;
                } else {
                    this.$set(this.isInvalids, 0, false);
                }
                $.ajax({
                    url: this.$refs.loginForm.action,
                    method: "post",
                    async: true,
                    data: {
                        username: this.username,
                        password: sha256(this.password),
                        _csrf: this.csrf,
                        geetest_challenge: captchaValid.geetest_challenge,
                        geetest_validate: captchaValid.geetest_validate,
                        geetest_seccode: captchaValid.geetest_seccode,
                        browserType: this.browserType
                    },
                    beforeSend: xmlHttpRequest => {
                        this.loading = this.$loading({
                            lock: true,
                            text: "正在登录，请稍等。。。",
                            spinner: "el-icon-loading",
                            background: "rgba(0, 0, 0, 0.8)"
                        });
                    }
                }).then(data => {
                    if (data.state === 1) {
                        this.loading.close();
                        this.$message({
                            message: data.message,
                            type: "success"
                        });
                        setTimeout(location.href = "/", 3000);
                    } else {
                        this.loading.close();
                        let messageError = data.messageError;
                        this.$message.error(messageError.defaultMessage);
                        let invalidIndex = 0;
                        if (messageError.field === "password") {
                            invalidIndex = 1;
                        } else if (messageError.field === "captcha") {
                            invalidIndex = 2;
                            this.isCaptcha = "block";
                        }
                        this.$set(this.isInvalids, invalidIndex, true);
                        this.$set(this.messages, invalidIndex, messageError.defaultMessage);
                        this.captchaObj.reset();
                    }
                });
            }
        }
    });
});
