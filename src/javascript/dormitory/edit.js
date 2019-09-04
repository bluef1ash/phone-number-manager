import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import {DatePicker, InputNumber, Message} from "element-ui";
import "moment/locale/zh-cn";
import moment from "moment";
import commonFunction from "@base/lib/javascript/common";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    Vue.use(DatePicker);
    Vue.use(InputNumber);
    new Vue({
        el: "#edit_dormitory",
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false, false, false, false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", "", "", "", "", "", "", "", "", ""],
            dormitoryManager: dormitoryManager,
            communities: communities,
            subcontractors: [],
            subdistrictId: 0,
            subdistrictName: null,
            subdistricts: [],
            communityName: null,
            newCommunities: []
        },
        created() {
            if (this.dormitoryManager === null) {
                this.dormitoryManager = {
                    sex: -1,
                    politicalStatus: -1,
                    workStatus: -1,
                    education: -1,
                    communityId: 0,
                    subcontractorId: 0,
                    community: {
                        dormitorySubmitted: false
                    }
                };
            } else {
                if (this.dormitoryManager.community === null) {
                    this.dormitoryManager.community = {dormitorySubmitted: false};
                }
                this.dormitoryManager.birth = moment(this.dormitoryManager.birth).format("YYYY-MM-DD");
                this.loadSubcontractors();
            }
            let company = commonFunction.companyHandler(this.communities, this.dormitoryManager.communityId);
            this.subdistrictId = company.subdistrictId;
            this.subdistricts = company.subdistricts;
            this.newCommunities = company.newCommunities;
        },
        methods: {
            /**
             * 切换街道
             */
            chooseSubdistrict() {
                this.newCommunities = [];
                this.dormitoryManager.communityId = 0;
                this.subcontractors = [];
                this.dormitoryManager.subcontractorId = 0;
                this.$set(this.dormitoryManager, "id", null);
                if (this.subdistrictId !== 0) {
                    this.communities.forEach(item => {
                        if (item.subdistrict.id === this.subdistrictId) {
                            this.newCommunities.push({
                                id: item.id,
                                name: item.name
                            });
                            this.subdistrictName = item.subdistrict.name;
                        }
                    });
                }
            },
            /**
             * 切换社区
             */
            chooseCommunity() {
                this.$set(this.dormitoryManager, "id", null);
                this.subcontractors = [];
                this.dormitoryManager.subcontractorId = 0;
                this.loadSubcontractors();
                if (this.dormitoryManager.communityId !== 0) {
                    this.communities.forEach(item => {
                        if (item.id === this.dormitoryManager.communityId) {
                            this.communityName = item.name;
                        }
                    });
                    commonFunction.$ajax({
                        url: loadDormitoryManagerLastIdUrl,
                        data: {
                            communityId: this.dormitoryManager.communityId,
                            subdistrictName: this.subdistrictName,
                            communityName: this.communityName
                        }
                    }, result => {
                        if (result.state === 1) {
                            this.$set(this.dormitoryManager, "id", result.id);
                        }
                    });
                }
            },
            /**
             * 加载社区分包人
             */
            loadSubcontractors() {
                if (this.dormitoryManager.communityId !== 0) {
                    commonFunction.$ajax({
                        url: loadSubcontractorsUrl,
                        data: {
                            communityId: this.dormitoryManager.communityId
                        }
                    }, result => {
                        if (result.state) {
                            this.subcontractors = result.subcontractors;
                        }
                    });
                }
            },
            /**
             * 社区楼长提交保存
             * @param event
             */
            submit(event) {
                if (this.dormitoryManager.communityId === null || this.dormitoryManager.communityId === 0) {
                    return this.stopSubmit(event, "请选择所属社区！", 13);
                }
                if (this.dormitoryManager.id === "" || this.dormitoryManager.id === null) {
                    return this.stopSubmit(event, "社区楼长编号不能为空！", 0);
                }
                if (this.dormitoryManager.name === "" || this.dormitoryManager.name === null) {
                    return this.stopSubmit(event, "社区楼长姓名不能为空！", 1);
                }
                if (this.dormitoryManager.name.length > 10) {
                    return this.stopSubmit(event, "社区楼长姓名不允许超过10个字符！", 1);
                }
                if (this.dormitoryManager.sex === null || this.dormitoryManager.sex === -1) {
                    return this.stopSubmit(event, "请选择社区楼长的性别！", 2);
                }
                if (this.dormitoryManager.birth === null || this.dormitoryManager.birth === "") {
                    return this.stopSubmit(event, "请选择社区楼长的出生年月！", 3);
                }
                if (this.dormitoryManager.politicalStatus === null || this.dormitoryManager.politicalStatus === -1) {
                    return this.stopSubmit(event, "请选择社区楼长的政治面貌！", 4);
                }
                if (this.dormitoryManager.workStatus === null || this.dormitoryManager.workStatus === -1) {
                    return this.stopSubmit(event, "请选择社区楼长的工作状况！", 5);
                }
                if (this.dormitoryManager.education === null || this.dormitoryManager.education === -1) {
                    return this.stopSubmit(event, "请选择社区楼长的文化程度！", 6);
                }
                if (this.dormitoryManager.address === null || this.dormitoryManager.address === "") {
                    return this.stopSubmit(event, "社区楼长的家庭地址不能为空！", 7);
                }
                if (this.dormitoryManager.managerAddress === null || this.dormitoryManager.managerAddress === "") {
                    return this.stopSubmit(event, "社区楼长的分包楼栋不能为空！", 8);
                }
                if (this.dormitoryManager.managerCount === null || this.dormitoryManager.managerCount === "") {
                    return this.stopSubmit(event, "社区楼长的联系户数不能为空！", 9);
                }
                let isPhoneEmpty = (this.dormitoryManager.mobile === null || this.dormitoryManager.mobile === "") && (this.dormitoryManager.landline === null || this.dormitoryManager.landline === "");
                if (isPhoneEmpty) {
                    return this.stopSubmit(event, "社区楼长的联系方式必须填写一项！", [10, 11], true);
                }
                if (this.dormitoryManager.subcontractorId === null || this.dormitoryManager.subcontractorId === 0) {
                    this.stopSubmit(event, "请选择社区分包人！", 14);
                }
            },
            /**
             * 重置表单样式
             */
            resetClass() {
                this.dormitoryManager = {
                    sex: -1,
                    politicalStatus: -1,
                    workStatus: -1,
                    education: -1,
                    communityId: 0,
                    subcontractorId: 0
                };
                this.subdistrictId = 0;
                this.errorClasses = [false, false, false, false, false, false, false, false, false, false, false, false, false, false];
                this.errorMessages = ["", "", "", "", "", "", "", "", "", "", "", "", "", ""];
            },
            /**
             * 取消提交
             * @param event
             * @param message
             * @param errorIndex
             * @param isArray
             */
            stopSubmit(event, message, errorIndex, isArray = false) {
                this.$message.error(message);
                if (isArray) {
                    for (let i = 0; i < errorIndex; i++) {
                        this.$set(this.errorClasses, errorIndex[i], true);
                    }
                } else {
                    this.$set(this.errorClasses, errorIndex, true);
                }
                this.$set(this.errorMessages, errorIndex, message);
                event.preventDefault();
            }
        },
        watch: {}
    });
});
