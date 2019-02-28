import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    if (user === null) {
        user = {systemUserId: 0, username: null, password: null, roleId: 0, roleLocationId: -1, isLocked: 0};
    }
    new Vue({
        el: "#edit_user",
        data: {
            systemAdministratorId: systemAdministratorId,
            subdistrictRoleId: subdistrictRoleId,
            communityRoleId: communityRoleId,
            systemUserId: user.systemUserId,
            username: user.username,
            password: user.password,
            confirmPassword: user.password,
            roleId: user.roleId,
            roleLocationId: user.roleLocationId,
            isLocked: user.isLocked,
            token: token,
            subdistricts: [],
            subdistrictId: -1,
            communities: [],
            communityId: 0,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", ""]
        },
        created() {
            if (this.roleId === this.systemAdministratorId) {
                this.subdistrictId = 0;
                return;
            }
            this.loadCompanies(this.roleId, this.roleLocationId);
        },
        methods: {
            /**
             * 加载单位数据
             * @param roleId
             * @param roleLocationId
             * @param isChoosed
             */
            loadCompanies(roleId, roleLocationId, isChoosed = false) {
                if (roleLocationId === null || roleLocationId === -1) {
                    this.communityId = 0;
                    this.subdistrictId = -1;
                }
                if (this.roleId !== null && this.roleId !== 0 && this.roleId !== 1) {
                    $.ajax({
                        url: "/system/user_role/user/ajax_get_companies",
                        method: "get",
                        async: false,
                        data: {
                            roleId: roleId,
                            roleLocationId: roleLocationId,
                            _token: this.token
                        }
                    }).then((data) => {
                        if (data) {
                            this.token = data._token;
                            if (roleLocationId === null) {
                                this.subdistricts = data.data;
                            } else {
                                this.communities = data.data;
                                if (this.roleId === this.communityRoleId && !isChoosed) {
                                    this.loadCompanies(this.communities[0].subdistrictId, null);
                                    this.subdistrictId = this.communities[0].subdistrictId;
                                    this.communityId = this.roleLocationId;
                                } else if (this.roleId === this.subdistrictRoleId && !isChoosed) {
                                    this.subdistrictId = this.roleLocationId;
                                    this.communityId = 0;
                                } else {
                                    this.communityId = 0;
                                }
                            }
                        }
                    });
                }
            },
            /**
             * 用户提交保存
             * @param event
             */
            userSubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.username === "" || this.username === null) {
                    message = "系统用户名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.password !== null && this.password !== "" && !/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$/.test(this.password)) {
                    message = "系统用户密码需要在6位以上，且英文字母与数字混合！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.password !== this.confirmPassword) {
                    message = "系统用户密码与确认密码不一致！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.roleId === null || this.roleId === 0) {
                    message = "请选择系统用户角色！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                    return;
                }
                if (this.subdistrictId === null || this.subdistrictId === -1) {
                    message = "请选择系统用户所属街道！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 4, true);
                    this.$set(this.errorMessages, 4, message);
                    event.preventDefault();
                    return;
                }
                if (this.roleId === this.communityRoleId && (this.communityId === null || this.communityId === 0)) {
                    message = "请选择系统用户所属社区！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 5, true);
                    this.$set(this.errorMessages, 5, message);
                    event.preventDefault();
                    return;
                }
                if (this.isLocked) {
                    this.isLocked = 1;
                } else {
                    this.isLocked = 0;
                }
                if (this.communityId === null || this.communityId === 0) {
                    this.roleLocationId = this.subdistrictId;
                } else {
                    this.roleLocationId = this.communityId;
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.subdistricts = [];
                this.subdistrictId = -1;
                this.communities = [];
                this.communityId = 0;
                this.errorClasses = [false, false, false, false, false, false];
                this.errorMessages = ["", "", "", "", "", ""];
            }
        }
    });
});
