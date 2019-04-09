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
        el: "#subdistrict_list",
        data: {
            csrf: csrf,
            subdistricts: subdistricts
        },
        methods: {
            /**
             * 删除街道办事处
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
