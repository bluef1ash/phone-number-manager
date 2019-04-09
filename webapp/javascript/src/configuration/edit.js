import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";

$(document).ready(() => {
    if (configuration === null) {
        configuration = {key: "", type: 0, value: null};
    }
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_configuration",
        data: {
            csrf: csrf,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", ""],
            configuration: configuration,
            users: []
        },
        methods: {
            /**
             * 加载用户数据
             */
            loadUsers() {
                if (this.configuration.type === 4 && this.users.length === 0) {
                    this.configuration.value = 0;
                    $.ajax({
                        url: loadUsersUrl,
                        method: "get",
                        data: {
                            _csrf: this.csrf
                        }
                    }).then(data => {
                        this.users = data.systemUsers.map(item => ({id: item.systemUserId, name: item.username}));
                    });
                } else {
                    this.configuration.value = null;
                }
            },
            /**
             * 系统配置项提交保存
             * @param event
             */
            configurationSubmit(event) {
                let message = null;
                if (this.csrf === null || this.csrf === "") {
                    location.reload();
                }
                if (this.configuration.key === "" || this.configuration.key === null) {
                    message = "系统配置项关键字名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.description === null || this.configuration.description === "") {
                    message = "系统配置项描述不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.type === null || this.configuration.type === 0) {
                    message = "请选择系统配置项值类别！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.configuration.value === null || (this.configuration.type === 4 && this.configuration === 0)) {
                    message = "请选择系统用户！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                    return;
                }
                this.configuration.keyIsChanged = this.configuration.keyIsChanged ? 1 : 0;
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
