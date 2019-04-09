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
        el: "#privilege_table",
        data: {
            csrf: csrf,
            userPrivileges: userPrivileges
        },
        methods: {
            /**
             * 删除系统权限
             * @param id
             */
            deleteObject(id) {
                commonFunction.deleteObject(this, deleteUrl, id);
            }
        }
    });
});
