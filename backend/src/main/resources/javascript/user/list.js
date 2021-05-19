import "@jsSrc/common/public";
import "@jsSrc/common/sidebar";
import Vue from "vue";
import { Message, MessageBox } from "element-ui";
import { $ajax, deleteObject } from "@library/javascript/common";
import "moment/locale/zh-cn";
import moment from "moment";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$confirm = MessageBox.confirm;
    new Vue({
        el: "#user_list",
        data: {
            systemAdministratorId: systemAdministratorId,
            systemUsers: systemUsers,
        },
        methods: {
            moment,
            /**
             * 系统用户锁定与开锁
             * @param id
             * @param locked
             * @param index
             */
            userLock(id, locked, index) {
                if (id === this.systemAdministratorId) {
                    this.$message.error("不允许锁定超级管理员！");
                    return;
                }
                $ajax(
                    {
                        url: userLockUrl,
                        method: "post",
                        headers: {
                            "X-CSRF-TOKEN": $("meta[name='X-CSRF-TOKEN']").prop(
                                "content"
                            ),
                        },
                        data: {
                            id: id,
                            locked: locked,
                        },
                    },
                    (data) => {
                        let message = null;
                        let messageType = "success";
                        if (data.state === 1) {
                            let oldSystemUser = this.systemUsers[index];
                            oldSystemUser.locked = !oldSystemUser.locked;
                            this.$set(this.systemUsers, index, oldSystemUser);
                            message =
                                data.locked === 0
                                    ? "解锁成功，该用户下次将能够登录系统！"
                                    : "锁定成功，该用户下次将无法登录系统！";
                        } else {
                            messageType = "error";
                            message = locked
                                ? "解锁失败，该用户下次将无法登录系统！"
                                : "锁定失败，该用户下次将能够登录系统！";
                        }
                        this.$message({
                            message: message,
                            type: messageType,
                        });
                    }
                );
            },
            /**
             * 删除系统用户
             * @param id
             */
            deleteObject(id) {
                if (id === this.systemAdministratorId) {
                    this.$message.error("不允许删除超级管理员！");
                    return;
                }
                deleteObject(this, deleteUrl, id);
            },
        },
    });
});
