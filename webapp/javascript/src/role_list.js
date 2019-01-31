require(["sidebar", "vue", "jquery", "layui"], function(sidebar, Vue) {
    new Vue({
        el: "#role_table",
        data: {
            token: token,
            userRoles: userRoles
        },
        methods: {
            deleteRole: function(roleId) {
                var _this = this;
                layui.use(["layer"], function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        if (roleId === 1) {
                            layer.msg("不允许删除超级管理员！", {icon: 5});
                            return;
                        }
                        layer.confirm("是否确定要删除此数据？", {icon: 3, title: "警告"}, function() {
                            $.ajax({
                                url: deleteUrl,
                                method: "delete",
                                data: {
                                    id: roleId,
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
