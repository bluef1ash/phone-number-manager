import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import commonFunction from "@base/lib/javascript/common";

$(document).ready(() => {
    if (configuration === null) {
        configuration = {key: "", type: 0, value: null};
    }
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_configuration",
        data: {
            csrf: $("meta[name='X-CSRF-TOKEN']"),
            csrfToken: null,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", ""],
            configuration: configuration,
            users: []
        },
        created() {
            this.csrfToken = this.csrf.prop("content");
            if (this.configuration.keyChanged === null || typeof this.configuration.keyChanged === "undefined") {
                this.configuration.keyChanged = true;
            }
        },
        methods: {
            /**
             * 加载用户数据
             */
            loadUsers() {
                if (this.configuration.type === 4 && this.users.length === 0) {
                    this.configuration.value = 0;
                    commonFunction.$ajax({
                        url: loadUsersUrl,
                        method: "post",
                        headers: {
                            "X-CSRF-TOKEN": this.csrf.prop("content")
                        }
                    }, data => {
                        this.users = data.systemUsers.map(item => ({id: item.id, name: item.username}));
                    }, null, csrfToken => this.csrfToken = csrfToken);
                } else {
                    this.configuration.value = null;
                }
            },
            /**
             * 系统配置项提交保存
             * @param event
             */
            submit(event) {
                let message = null;
                if (this.configuration.key === "" || this.configuration.key === null) {
                    message = "系统配置项关键字名称不能为空！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.description === null || this.configuration.description === "") {
                    message = "系统配置项描述不能为空！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.type === null || this.configuration.type === 0) {
                    message = "请选择系统配置项值类别！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.value === null) {
                    message = "请设置配置项的值！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.errorClasses = [false, false, false, false, false, false];
                this.errorMessages = ["", "", "", "", "", ""];
            }
        }
    });
});
