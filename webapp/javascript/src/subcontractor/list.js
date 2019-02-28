import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message, MessageBox} from "element-ui";
import commonFunction from "@base/lib/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$confirm = MessageBox.confirm;
    new Vue({
        el: "#subcontractor_list",
        data: {
            token: token,
            subcontractors: subcontractors
        },
        methods: {
            /**
             * 删除社区分包人
             * @param id
             */
            deleteObject(id) {
                commonFunction.deleteObject(this, deleteUrl, id);
            }
        }
    })
    ;
})
;
