import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import {Message, MessageBox} from "element-ui";
import { deleteObject } from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.directive(MessageBox.name, MessageBox);
    Vue.prototype.$confirm = MessageBox.confirm;
    new Vue({
        el: "#subdistrict_list",
        data: {
            subdistricts: subdistricts
        },
        methods: {
            /**
             * 删除街道办事处
             * @param id
             */
            deleteObject(id) {
                deleteObject(this, deleteUrl, id);
            }
        }
    })
    ;
})
;
