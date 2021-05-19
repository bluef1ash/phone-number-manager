import "@jsSrc/common/public";
import "@jsSrc/common/sidebar";
import Vue from "vue";
import { Message, MessageBox } from "element-ui";
import { deleteObject } from "@library/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$confirm = MessageBox.confirm;
    new Vue({
        el: "#subcontractor_list",
        data: {
            subcontractors: subcontractors,
        },
        methods: {
            /**
             * 删除社区分包人
             * @param id
             */
            deleteObject(id) {
                deleteObject(this, deleteUrl, id);
            },
        },
    });
});
