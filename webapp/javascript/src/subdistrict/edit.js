import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import commonFunction from "@base/lib/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_subdistrict",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false],
            errorMessages: ["", ""],
            subdistrict: subdistrict
        },
        created() {
            if (this.subdistrict === null) {
                this.subdistrict = {
                    subdistrictName: ""
                };
            }
        },
        methods: {
            /**
             * 街道提交保存
             * @param event
             */
            subdistrictSubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.subdistrict.subdistrictName === "" || this.subdistrict.subdistrictName === null) {
                    message = "街道办事处名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.subdistrict.subdistrictName.length > 10) {
                    message = "街道办事处名称不允许超过10个字符！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.subdistrict.subdistrictTelephone === null || this.subdistrict.subdistrictTelephone === "") {
                    message = "街道办事处联系方式不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (commonFunction.checkPhoneType(this.subdistrict.subdistrictTelephone) === -1) {
                    message = "街道办事处联系方式非法！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.subdistricts = [];
                this.subdistrictId = -1;
                this.communities = [];
                this.communityId = 0;
                this.errorClasses = [false, false, false, false, false, false];
                this.errorMessages = ["", "", "", "", "", ""];
            }
        }
    });
});
