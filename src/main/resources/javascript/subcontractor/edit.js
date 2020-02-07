import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import { checkPhoneType } from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_subcontractor",
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false, false],
            errorMessages: ["", "", ""],
            subcontractor: subcontractor
        },
        created() {
            if (this.subcontractor === null) {
                this.subcontractor = {
                    name: "",
                    communityId: 0
                };
            }
        },
        methods: {
            /**
             * 社区分包人提交保存
             * @param event
             */
            submit(event) {
                let message = null;
                if (this.subcontractor.name === "" || this.subcontractor.name === null) {
                    message = "社区分包人姓名不能为空！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.subcontractor.name.length > 10) {
                    message = "社区分包人姓名不允许超过10个字符！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.subcontractor.mobile === null || this.subcontractor.mobile === "") {
                    message = "社区分包人联系方式不能为空！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (checkPhoneType(this.subcontractor.mobile) === -1) {
                    message = "社区分包人联系方式非法！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.subcontractor.actualNumber === null || this.subcontractor.actualNumber === 0) {
                    message = "请选择所属社区！";
                    this.$message.error(message);
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.subcontractor = {
                    name: "",
                    communityId: 0
                };
                this.errorClasses = [false, false, false];
                this.errorMessages = ["", "", ""];
            }
        }
    });
});
