require(["sidebar", "vue", "jquery", "layui"], function(sidebar, Vue) {
    new Vue({
        el: "#user_list",
        data: {
            token: token,
            systemAdministratorId: systemAdministratorId,
            systemUsers: systemUsers
        },
        methods: {
            /**
             * 系统用户锁定与开锁
             * @param systemUserId
             * @param isLocked
             * @param index
             */
            userLock: function(systemUserId, isLocked, index) {
                var _this = this;
                layui.use(["layer"], function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        if (systemUserId === this.systemAdministratorId) {
                            layer.msg("不允许锁定超级管理员！", {icon: 5});
                            return;
                        }
                        $.ajax({
                            url: "/system/user_role/user/ajax_user_lock",
                            method: "get",
                            data: {
                                _token: _this.token,
                                systemUserId: systemUserId,
                                locked: isLocked
                            },
                            success: function(data) {
                                var message = null;
                                var icon = 6;
                                if (data.state === 1) {
                                    var oldSystemUser = _this.systemUsers[index];
                                    oldSystemUser.isLocked = !oldSystemUser.isLocked;
                                    _this.$set(_this.systemUsers, index, oldSystemUser);
                                    message = data.isLocked === 0 ? "解锁成功，该用户下次将能够登录系统！" : "锁定成功，该用户下次将无法登录系统！";
                                } else {
                                    icon = 5;
                                    message = isLocked ? "解锁失败，该用户下次将无法登录系统！" : "锁定失败，该用户下次将能够登录系统！";
                                }
                                layer.msg(message, {icon: icon});
                            }
                        });
                    });
                });
            },
            /**
             * 删除系统用户
             * @param systemUserId
             */
            deleteUser: function(systemUserId) {
                var _this = this;
                layui.use(["layer"], function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        if (systemUserId === _this.systemAdministratorId) {
                            layer.msg("不允许删除超级管理员！", {icon: 5});
                            return;
                        }
                        layer.confirm("是否确定要删除此数据？", {icon: 3, title: "警告"}, function() {
                            $.ajax({
                                url: deleteUrl,
                                method: "delete",
                                data: {
                                    id: systemUserId,
                                    _token: this.token
                                },
                                success: function(data) {
                                    _this.token = data._token;
                                    if (data.state) {
                                        layer.msg(data.message, {icon: 6});
                                        setTimeout(function() {
                                            location.reload();
                                        }, 5000);
                                    } else {
                                        layer.msg(data.message, {icon: 5});
                                    }
                                }
                            });
                        });
                    });
                });
            }
        }
    });
});
