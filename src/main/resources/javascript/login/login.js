import "@baseSrc/javascript/common/public";
import Vue from "vue";
import { Loading, Message, MessageBox } from "element-ui";
import sha256 from "sha256";
import VueCookie from "vue-cookie";
import { $ajax, browserType } from "@base/lib/javascript/common";
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
            isInvalids: [false, false, false, false],
            messages: ["", "", "正在加载验证码，请稍后。。。"],
            captchaObj: null,
            isCaptcha: "block",
            browserType: "",
            csrf: $("meta[name='X-CSRF-TOKEN']"),
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
            this.browserType = browserType() ? "web" : "h5";
            $ajax({
                url: captchaUrl + "?browserType=" + this.browserType + "&time=" + (new Date()).getTime(),
                dataType: "json"
            }, result => {
                if (result.success === 1) {
                    initGeetest({
                        gt: result.gt,
                        challenge: result.challenge,
                        product: "bind",
                        offline: !result.success,
                        new_captcha: true
                    }, captchaObj => {
                        this.captchaObj = captchaObj;
                        this.captchaObj.bindForm("#login_form");
                        this.captchaObj.onReady(() => {
                            this.isCaptcha = "none";
                        }).onSuccess(() => {
                            $ajax({
                                url: this.$refs.loginForm.action,
                                method: "post",
                                data: {
                                    username: this.username,
                                    password: sha256(this.password),
                                    browserType: this.browserType
                                },
                                beforeSend: xmlHttpRequest => {
                                    xmlHttpRequest.setRequestHeader("X-CSRF-TOKEN", this.csrf.prop("content"));
                                    this.loading = this.$loading({
                                        lock: true,
                                        text: "正在登录，请稍等。。。",
                                        spinner: "el-icon-loading",
                                        background: "rgba(0, 0, 0, 0.8)"
                                    });
                                }
                            }, data => {
                                let timeout = 5000;
                                this.loading.close();
                                this.$message.success(data.message + timeout / 1000 + "秒后进入系统。。。");
                                setTimeout(location.href = "/", timeout);
                            }, xhr => {
                                this.loading.close();
                                let responseJSON = xhr.responseJSON;
                                this.$message.error(responseJSON.message);
                                let invalidIndex = 0;
                                if (responseJSON.fieldName === "password") {
                                    invalidIndex = 1;
                                } else if (responseJSON.fieldName === "captcha") {
                                    invalidIndex = 2;
                                    this.isCaptcha = "block";
                                }
                                this.$set(this.isInvalids, invalidIndex, true);
                                this.$set(this.messages, invalidIndex, responseJSON.message);
                            });
                        }).onError(() => {
                            this.$set(this.messages, 2, "加载图形验证码失败，请稍后再试！");
                        });
                    });
                }
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
                this.captchaObj.verify();
            }
        }
    });
});
