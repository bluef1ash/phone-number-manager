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
        el: "#configuration_list",
        data: {
            csrf: csrf,
            configurations: configurations
        },
        methods: {
            /**
             * 删除系统系统配置项
             * @param key
             */
            deleteObject(key) {
                commonFunction.deleteObject(this, deleteUrl, key, "key");
            }
        }
    })
    ;
})
;
