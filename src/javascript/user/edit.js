import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import Vue from "vue";
import {Message} from "element-ui";
import sha256 from "sha256";

$(document).ready(() => {
    Vue.prototype.$message = Message;
    new Vue({
        el: "#edit_user",
        data: {
            systemAdministratorId: systemAdministratorId,
            subdistrictCompanyType: subdistrictCompanyType,
            communityCompanyType: communityCompanyType,
            user: user,
            confirmPassword: null,
            csrf: csrf,
            locked: null,
            subdistricts: [],
            subdistrictId: -1,
            communities: [],
            communityId: 0,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", ""]
        },
        created() {
            if (this.user === null) {
                this.user = {username: null, password: null, roleId: 0, companyType: -1, companyId: -1, locked: false};
            }
            if (this.user.companyType === this.subdistrictCompanyType) {
                this.subdistrictId = this.user.companyId;
                this.loadCommunities(this.subdistrictId);
            } else if (this.user.companyType === this.communityCompanyType) {
                $.ajax({
                    url: loadCommunityUrl,
                    data: {
                        id: this.user.companyId,
                        _csrf: this.csrf
                    }
                }).then(data => {
                    if (data.state) {
                        this.subdistrictId = data.community.subdistrictId;
                        this.loadCommunities(data.community.subdistrictId, () => {
                            this.communityId = this.user.companyId;
                        });
                    }
                });
            }
        },
        methods: {
            /**
             * 加载单位数据
             * @param subdistrictId
             * @param callback
             */
            loadCommunities(subdistrictId, callback = null) {
                if (subdistrictId > 0) {
                    $.ajax({
                        url: loadCompaniesUrl,
                        data: {
                            subdistrictId: subdistrictId,
                            _csrf: this.csrf
                        }
                    }).then(data => {
                        if (data.state) {
                            this.communities = data.communities;
                            callback && callback();
                        }
                    });
                }
            },
            /**
             * 用户提交保存
             * @param event
             */
            submit(event) {
                let message = null;
                if (this.csrf === null || this.csrf === "") {
                    location.reload();
                }
                if (this.user.username === "" || this.user.username === null) {
                    message = "系统用户名称不能为空！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 0, true);
                    this.$set(this.errorMessages, 0, message);
                    event.preventDefault();
                    return;
                }
                if (this.user.password !== null && this.user.password !== "" && !/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$/.test(this.user.password)) {
                    message = "系统用户密码需要在6位以上，且英文字母与数字混合！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    event.preventDefault();
                    return;
                }
                if (this.user.password !== this.confirmPassword) {
                    message = "系统用户密码与确认密码不一致！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 1, true);
                    this.$set(this.errorMessages, 1, message);
                    this.$set(this.errorClasses, 2, true);
                    this.$set(this.errorMessages, 2, message);
                    event.preventDefault();
                    return;
                }
                if (this.user.roleId === null || this.user.roleId === 0) {
                    message = "请选择系统用户角色！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 3, true);
                    this.$set(this.errorMessages, 3, message);
                    event.preventDefault();
                    return;
                }
                if (this.subdistrictId === null || this.subdistrictId === -1) {
                    message = "请选择系统用户所属街道！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 4, true);
                    this.$set(this.errorMessages, 4, message);
                    event.preventDefault();
                    return;
                }
                if (this.user.companyType === this.communityCompanyType && (this.communityId === null || this.communityId === 0)) {
                    message = "请选择系统用户所属社区！";
                    this.$message({
                        message: message,
                        type: "error"
                    });
                    this.$set(this.errorClasses, 5, true);
                    this.$set(this.errorMessages, 5, message);
                    event.preventDefault();
                    return;
                }
                this.user.password = this.confirmPassword = sha256(this.user.password);
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
        },
        watch: {
            "locked"(value) {
                this.user.locked = value === "on";
            },
            "subdistrictId"(value) {
                this.communityId = 0;
                if (value !== -1) {
                    this.user.companyType = this.subdistrictCompanyType;
                    this.user.companyId = value;
                }
            },
            "communityId"(value) {
                if (value !== 0) {
                    this.user.companyType = this.communityCompanyType;
                    this.user.companyId = value;
                }
            }
        }
    });
});
