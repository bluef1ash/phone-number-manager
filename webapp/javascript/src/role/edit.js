import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message, Tree} from "element-ui";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.use(Tree);
    new Vue({
        el: "#edit_role",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false],
            errorMessages: ["", "", "", "", ""],
            userRole: userRole,
            userRoles: userRoles,
            userPrivileges: userPrivileges,
            privilegeIds: [],
            privilegeTree: null,
            checkedList: []
        },
        created() {
            if (this.userRole === null) {
                this.userRole = {higherRole: -1};
                this.privilegeIds = [];
            }
            this.privilegeTree = this.setTree(this.userPrivileges, 0);
        },
        mounted() {
            this.$refs.tree.setCheckedKeys(this.checkedList);
        },
        methods: {
            /**
             * 递归设置权限树形菜单
             * @param userPrivileges
             * @param higherPrivilegeId
             * @return {Array}
             */
            setTree(userPrivileges, higherPrivilegeId) {
                let nodes = [];
                for (let i = 0; i < userPrivileges.length; i++) {
                    let node = {id: userPrivileges[i].privilegeId, label: userPrivileges[i].privilegeName};
                    if (typeof this.userRole.userPrivileges !== "undefined" && this.userRole.userPrivileges !== null) {
                        for (let j = 0; j < this.userRole.userPrivileges.length; j++) {
                            if (userPrivileges[i].privilegeId === this.userRole.userPrivileges[j].privilegeId) {
                                this.checkedList.push(userPrivileges[i].privilegeId);
                                this.privilegeIds.push(userPrivileges[i].privilegeId);
                            } else if (userPrivileges[i].higherPrivilege === this.userRole.userPrivileges[j].privilegeId) {
                                this.checkedList.push(userPrivileges[i].privilegeId);
                            }
                        }
                    }
                    if (higherPrivilegeId === userPrivileges[i].higherPrivilege && userPrivileges[i].subUserPrivileges !== null && userPrivileges[i].subUserPrivileges.length > 0) {
                        node.children = this.setTree(userPrivileges[i].subUserPrivileges, userPrivileges[i].privilegeId);
                    }
                    nodes.push(node);
                }
                return nodes;
            },
            /**
             * 树形菜单复选框事件
             * @param obj
             * @param isChecked
             * @param childrenIsChecked
             */
            checkChange(obj, isChecked, childrenIsChecked) {
                if (isChecked) {
                    this.privilegeIds.push(obj.id);
                } else {
                    this.privilegeIds.splice(this.privilegeIds.findIndex(item => item === obj.id), 1);
                }
            },
            /**
             * 用户角色提交保存
             * @param event
             */
            userRoleSubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.userRole.roleName === "" || this.userRole.roleName === null) {
                    message = "系统用户角色名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.userRole.roleDescription === null && this.userRole.roleDescription === "") {
                    message = "系统用户角色描述不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.userRole.higherRole === null || this.userRole.higherRole === -1) {
                    message = "请选择系统用户角色的上级角色！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.privilegeIds === null || this.privilegeIds.length < 1) {
                    message = "请选择系统用户角色的权限！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass: function() {
                this.userRole = {higherRole: -1};
                this.errorClasses = [false, false, false, false, false];
                this.errorMessages = ["", "", "", "", ""];
                this.privilegeIds = [];
            }
        }
    });
});
