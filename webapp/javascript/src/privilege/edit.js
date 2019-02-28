import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_privilege",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false],
            errorMessages: ["", "", "", "", ""],
            userPrivilege: userPrivilege,
            userPrivileges: userPrivileges
        },
        created() {
            if (this.userPrivilege === null) {
                this.userPrivilege = {higherPrivilege: -1, isDisplay: 0};
            }
        },
        methods: {
            /**
             * 用户角色提交保存
             * @param event
             */
            userPrivilegeSubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.userPrivilege.privilegeName === "" || this.userPrivilege.privilegeName === null) {
                    message = "系统用户权限名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.userPrivilege.constraintAuth === null && this.userPrivilege.constraintAuth === "") {
                    message = "系统权限约束名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.userPrivilege.uri === null || this.userPrivilege.uri === "") {
                    message = "系统访问地址不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.userPrivilege.higherPrivilege === null || this.userPrivilege.higherPrivilege === -1) {
                    message = "请选择系统用户权限的上级权限！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 4, true);
                    this.$set(this.errorMessages, 4, message);
                    event.preventDefault();
                }
                this.userPrivilege.isDisplay = this.userPrivilege.isDisplay ? 1 : 0;
            },
            /**
             * 重置表单样式
             */
            resetClass: function() {
                this.userPrivilege = {higherPrivilege: -1};
                this.errorClasses = [false, false, false, false, false];
                this.errorMessages = ["", "", "", "", ""];
                this.privilegeIds = [];
            }
        }
    });
});
