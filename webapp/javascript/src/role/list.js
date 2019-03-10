import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message, MessageBox} from "element-ui";
import commonFunction from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$confirm = MessageBox.confirm;
    new Vue({
        el: "#role_table",
        data: {
            token: token,
            userRoles: userRoles
        },
        methods: {
            /**
             * 删除系统用户角色
             * @param id
             */
            deleteObject(id) {
                if (id === 1) {
                    this.$message({
                        message: "不允许删除管理员角色！",
                        type: "error"
                    });
                    return;
                }
                commonFunction.deleteObject(this, deleteUrl, id);
            }
        }
    });
});
