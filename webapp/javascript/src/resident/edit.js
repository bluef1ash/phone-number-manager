import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import commonFunction from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_resident",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", "", "", ""],
            communityResident: communityResident,
            communities: communities,
            subcontractors: [],
            subdistrictId: 0,
            subdistricts: [],
            newCommunities: [],
            isShowPhone2: false,
            isShowPhone3: false
        },
        created() {
            if (this.communityResident === null) {
                this.communityResident = {
                    communityId: 0,
                    subcontractorId: 0
                };
            } else {
                this.loadSubcontractors();
                let isEmptyPhone1 = typeof this.communityResident.communityResidentPhone1 === "undefined" || this.communityResident.communityResidentPhone1 === null || this.communityResident.communityResidentPhone1 === "";
                let isEmptyPhone2 = typeof this.communityResident.communityResidentPhone2 === "undefined" || this.communityResident.communityResidentPhone2 === null || this.communityResident.communityResidentPhone2 === "";
                if (!isEmptyPhone1) {
                    this.isShowPhone2 = true;
                }
                if (!isEmptyPhone2) {
                    this.isShowPhone3 = true;
                }
            }
            this.subdistricts.push(this.communities[0].subdistrict);
            this.communities.forEach(item => {
                if (item.communityId === this.communityResident.communityId) {
                    this.subdistrictId = item.subdistrictId;
                }
                if (this.subdistrictId === item.subdistrictId) {
                    this.newCommunities.push({
                        id: item.communityId,
                        name: item.communityName
                    });
                }
                this.subdistricts.forEach(subdistrict => {
                    if (subdistrict.subdistrictId !== item.subdistrict.subdistrictId) {
                        this.subdistricts.push(item.subdistrict);
                    }
                });
            });
        },
        methods: {
            /**
             * 切换街道
             */
            chooseSubdistrict() {
                this.newCommunities = [];
                this.communityResident.communityId = 0;
                this.subcontractors = [];
                this.communityResident.subcontractorId = 0;
                if (this.subdistrictId !== 0) {
                    this.communities.forEach(item => {
                        if (item.subdistrict.subdistrictId === this.subdistrictId) {
                            this.newCommunities.push({
                                id: item.communityId,
                                name: item.communityName
                            });
                        }
                    });
                }
            },
            /**
             * 切换社区
             */
            chooseCommunity() {
                this.subcontractors = [];
                this.communityResident.subcontractorId = 0;
                this.loadSubcontractors();
            },
            /**
             * 加载社区分包人
             */
            loadSubcontractors() {
                if (this.communityResident.communityId !== 0) {
                    $.ajax({
                        url: loadSubcontractorsUrl,
                        method: "get",
                        data: {
                            _token: this.token,
                            communityId: this.communityResident.communityId
                        }
                    }).then(item => {
                        this.token = item._token;
                        if (item.state) {
                            this.subcontractors = item.subcontractors;
                        }
                    });
                }
            },
            /**
             * 社区居民提交保存
             * @param event
             */
            residentSubmit(event) {
                let message = null;
                if (this.token === null || this.token === "") {
                    location.reload();
                }
                if (this.communityResident.communityResidentName === "" || this.communityResident.communityResidentName === null) {
                    message = "社区居民姓名不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.communityResident.communityResidentName.length > 10) {
                    message = "社区居民姓名不允许超过10个字符！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.communityResident.communityResidentAddress === null || this.communityResident.communityResidentAddress === "") {
                    message = "社区居民家庭地址不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                let isEmptyPhone1 = typeof this.communityResident.communityResidentPhone1 === "undefined" || this.communityResident.communityResidentPhone1 === null || this.communityResident.communityResidentPhone1 === "";
                let isEmptyPhone2 = typeof this.communityResident.communityResidentPhone2 === "undefined" || this.communityResident.communityResidentPhone2 === null || this.communityResident.communityResidentPhone2 === "";
                let isEmptyPhone3 = typeof this.communityResident.communityResidentPhone3 === "undefined" || this.communityResident.communityResidentPhone3 === null || this.communityResident.communityResidentPhone3 === "";
                if (isEmptyPhone1 && isEmptyPhone2 && isEmptyPhone3) {
                    message = "至少填写一个社区居民联系方式！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    this.$set(this.errorClasses, 4, true);
                    this.$set(this.errorMessages, 4, message);
                    event.preventDefault();
                    return;
                }
                console.log(commonFunction.checkPhoneType(this.communityResident.communityResidentPhone1));
                if (commonFunction.checkPhoneType(this.communityResident.communityResidentPhone1) === -1) {
                    message = "社区居民联系方式一非法！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (commonFunction.checkPhoneType(this.communityResident.communityResidentPhone2) === -1) {
                    message = "社区居民联系方式二非法！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                    return;
                }
                if (commonFunction.checkPhoneType(this.communityResident.communityResidentPhone3) === -1) {
                    message = "社区居民联系方式三非法！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 4, true);
                    this.$set(this.errorMessages, 4, message);
                    event.preventDefault();
                    return;
                }
                if (this.communityResident.communityId === null || this.communityResident.communityId === 0) {
                    message = "请选择所属社区！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 6, true);
                    this.$set(this.errorMessages, 6, message);
                    event.preventDefault();
                    return;
                }
                if (this.communityResident.subcontractorId === null || this.communityResident.subcontractorId === 0) {
                    message = "请选择社区分包人！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 7, true);
                    this.$set(this.errorMessages, 7, message);
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.communityResident = {
                    communityId: 0,
                    subcontractorId: 0
                };
                this.subdistrictId = 0;
                this.errorClasses = [false, false, false, false, false, false, false, false];
                this.errorMessages = ["", "", "", "", "", "", "", ""];
            }
        },
        watch: {
            "communityResident.communityResidentPhone1"(value, oldValue) {
                this.isShowPhone2 = typeof value !== "undefined" && value !== null && value !== "";
            },
            "communityResident.communityResidentPhone2"(value, oldValue) {
                this.isShowPhone3 = typeof value !== "undefined" && value !== null && value !== "";
            }
        }
    });
});
