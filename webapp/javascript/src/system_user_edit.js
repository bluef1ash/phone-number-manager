require(["sidebar", "vue", "layui"], function(sidebar, Vue) {
    if (user === null) {
        user = {systemUserId: 0, username: null, password: null, roleId: 0, roleLocationId: -1, isLocked: 0};
    }
    new Vue({
        el: "#edit_user",
        data: {
            systemAdministratorId: systemAdministratorId,
            subdistrictRoleId: subdistrictRoleId,
            communityRoleId: communityRoleId,
            systemUserId: user.systemUserId,
            username: user.username,
            password: user.password,
            confirmPassword: user.password,
            roleId: user.roleId,
            roleLocationId: user.roleLocationId,
            isLocked: user.isLocked,
            token: token,
            subdistricts: [],
            subdistrictId: -1,
            communities: [],
            communityId: 0,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false, false],
            errorMessages: ["", "", "", "", "", ""]
        },
        created: function() {
            if (this.roleId === this.systemAdministratorId) {
                this.subdistrictId = 0;
                return;
            }
            this.loadCompanies(this.roleId, this.roleLocationId);
            if (this.roleId === this.communityRoleId) {
                this.loadCompanies(this.communities[0].subdistrictId, null);
                this.subdistrictId = this.communities[0].subdistrictId;
                this.communityId = this.roleLocationId;
            } else {
                this.subdistrictId = this.roleLocationId;
            }
        },
        methods: {
            /**
             * 加载单位数据
             * @param roleId
             * @param roleLocationId
             */
            loadCompanies: function(roleId, roleLocationId) {
                if (roleLocationId === null || roleLocationId === -1) {
                    this.communityId = 0;
                    this.subdistrictId = -1;
                }
                if (this.roleId !== null && this.roleId !== 0 && this.roleId !== 1) {
                    var _this = this;
                    $.ajax({
                        url: "/system/user_role/user/ajax_get_companies",
                        method: "get",
                        async: false,
                        data: {
                            roleId: roleId,
                            roleLocationId: roleLocationId,
                            _token: this.token
                        },
                        success: function(data) {
                            if (data) {
                                _this.token = data._token;
                                if (roleLocationId === null) {
                                    _this.subdistricts = data.data;
                                } else {
                                    _this.communities = data.data;
                                }
                            }
                        }
                    });
                }
            },
            /**
             * 用户提交保存
             * @param event
             */
            userSubmit: function(event) {
                var _this = this;
                var flag = false;
                var message = null;
                layui.use(["layer"], function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        if (_this.token === null || _this.token === "") {
                            location.reload();
                        }
                        if (_this.username === "" || _this.username === null) {
                            message = "系统用户名称不能为空！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 0, true);
                            _this.$set(_this.errorMessages, 0, message);
                            return;
                        }
                        if (_this.password !== null && _this.password !== "" && !/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$/.test(_this.password)) {
                            message = "系统用户密码需要在6位以上，且英文字母与数字混合！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 1, true);
                            _this.$set(_this.errorMessages, 1, message);
                        }
                        if (_this.password !== _this.confirmPassword) {
                            message = "系统用户密码与确认密码不一致！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 1, true);
                            _this.$set(_this.errorMessages, 1, message);
                            _this.$set(_this.errorClasses, 2, true);
                            _this.$set(_this.errorMessages, 2, message);
                            return;
                        }
                        if (_this.roleId === null || _this.roleId === 0) {
                            message = "请选择系统用户角色！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 3, true);
                            _this.$set(_this.errorMessages, 3, message);
                            return;
                        }
                        if (_this.subdistrictId === null || _this.subdistrictId === -1) {
                            message = "请选择系统用户所属街道！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 4, true);
                            _this.$set(_this.errorMessages, 4, message);
                            return;
                        }
                        if (_this.roleId === _this.communityRoleId && (_this.communityId === null || _this.communityId === 0)) {
                            message = "请选择系统用户所属社区！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 5, true);
                            _this.$set(_this.errorMessages, 5, message);
                            return;
                        }
                        if (_this.isLocked) {
                            _this.isLocked = 1;
                        } else {
                            _this.isLocked = 0;
                        }
                        if (_this.communityId === null || _this.communityId === 0) {
                            _this.roleLocationId = _this.subdistrictId;
                        } else {
                            _this.roleLocationId = _this.communityId;
                        }
                    });
                });
                if (!flag) {
                    event.preventDefault();
                }
            },
            /**
             * 重置表单样式
             */
            resetClass: function() {
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
