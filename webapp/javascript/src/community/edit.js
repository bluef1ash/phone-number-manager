import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import commonFunction from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_community",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false],
            errorMessages: ["", "", "", ""],
            community: community
        },
        created() {
            if (this.community === null) {
                this.community = {
                    communityName: "",
                    subdistrictId: 0
                };
            }
        },
        methods: {
            /**
             * 街道提交保存
             * @param event
             */
            communitySubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.community.communityName === "" || this.community.communityName === null) {
                    message = "社区名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.community.communityName.length > 10) {
                    message = "社区名称不允许超过10个字符！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.community.communityTelephone === null || this.community.communityTelephone === "") {
                    message = "社区联系方式不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (commonFunction.checkPhoneType(this.community.communityTelephone) === -1) {
                    message = "社区联系方式非法！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.community.actualNumber === null || this.community.actualNumber === 0) {
                    message = "社区总人数不能为空，或者为0！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (/\d+/.test(this.community.actualNumber)) {
                    message = "社区总人数只能为数字！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.community.subdistrictId === null || this.community.subdistrictId === 0) {
                    message = "请选择所属街道！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.community = {
                    communityName: "",
                    subdistrictId: 0
                };
                this.errorClasses = [false, false, false, false];
                this.errorMessages = ["", "", "", ""];
            }
        }
    });
});
