require(["sidebar", "vue", "jquery", "ztree", "layui"], function(sidebar, Vue) {
    new Vue({
        el: "#edit_role",
        data: {
            token: token,
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false],
            errorMessages: ["", "", "", "", ""],
            userRole: userRole,
            userRoles: userRoles,
            userPrivileges: userPrivileges,
            privilegeIds: [],
            zNodes: [],
            zTree: null
        },
        created: function() {
            if (this.userRole === null) {
                this.userRole = {higherRole: -1};
                this.privilegeIds = [];
            }
            this.zNodes = this.setZTree(this.userPrivileges, 0);
        },
        mounted: function() {
            var _this = this;
            this.zTree = $.fn.zTree.init($("#user_privileges"), {
                check: {
                    enable: true,
                    chkboxType: {"Y": "s", "N": "s"}
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    /**
                     * 单击事件
                     * @param event
                     * @param treeId
                     * @param treeNode
                     * @param clickFlag
                     */
                    onClick: function(event, treeId, treeNode, clickFlag) {
                        //zTree.expandNode(treeNode);
                        _this.zTree.checkNode(treeNode, !treeNode.checked, true);
                        _this.checkTree();
                    },

                    onCheck: function(event, treeId, treeNode, clickFlag) {
                        _this.checkTree();
                    }
                }
            }, this.zNodes);
        },
        methods: {
            /**
             * 递归设置权限树形菜单
             * @param userPrivileges
             * @param higherPrivilegeId
             * @return {Array}
             */
            setZTree: function(userPrivileges, higherPrivilegeId) {
                var zNodes = [];
                for (var i = 0; i < userPrivileges.length; i++) {
                    var zNode = {id: userPrivileges[i].privilegeId, name: userPrivileges[i].privilegeName};
                    if (typeof this.userRole.userPrivileges !== "undefined" && this.userRole.userPrivileges !== null) {
                        for (var j = 0; j < this.userRole.userPrivileges.length; j++) {
                            if (userPrivileges[i].privilegeId === this.userRole.userPrivileges[j].privilegeId) {
                                zNode.checked = true;
                                this.privilegeIds.push({id: userPrivileges[i].privilegeId});
                            } else if (userPrivileges[i].higherPrivilege === this.userRole.userPrivileges[j].privilegeId) {
                                zNode.checked = true;
                            }
                        }
                    }
                    if (higherPrivilegeId === userPrivileges[i].higherPrivilege && userPrivileges[i].subUserPrivileges !== null && userPrivileges[i].subUserPrivileges.length > 0) {
                        zNode.children = this.setZTree(userPrivileges[i].subUserPrivileges, userPrivileges[i].privilegeId);
                    }
                    zNodes.push(zNode);
                }
                return zNodes;
            },
            /**
             * 树形菜单复选框事件
             */
            checkTree: function() {
                var changedNodes = this.zTree.getChangeCheckedNodes();
                var i = 0;
                if (!changedNodes[0].checked) {
                    for (i = 0; i < this.privilegeIds.length; i++) {
                        if (this.privilegeIds[i].id === changedNodes[0].id) {
                            this.privilegeIds.splice(i, 1);
                            break;
                        }
                    }
                } else {
                    this.privilegeIds.push({id: changedNodes[0].id});
                }
                var nodes = this.zTree.getChangeCheckedNodes();
                for (i = 0; i < nodes.length; i++) {
                    nodes[i].checkedOld = nodes[i].checked;
                }
            },
            /**
             * 用户角色提交保存
             * @param event
             */
            userRoleSubmit: function(event) {
                var _this = this;
                var flag = false;
                var message = null;
                layui.use(["layer"], function() {
                    var layer = layui.layer;
                    layer.ready(function() {
                        if (_this.token === null || _this.token === "") {
                            location.reload();
                        }
                        if (_this.userRole.roleName === "" || _this.userRole.roleName === null) {
                            message = "系统用户角色名称不能为空！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 0, true);
                            _this.$set(_this.errorMessages, 0, message);
                            return;
                        }
                        if (_this.userRole.roleDescription === null && _this.userRole.roleDescription === "") {
                            message = "系统用户角色描述不能为空！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 1, true);
                            _this.$set(_this.errorMessages, 1, message);
                        }
                        if (_this.userRole.higherRole === null || _this.userRole.higherRole === -1) {
                            message = "请选择系统用户角色的上级角色！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 2, true);
                            _this.$set(_this.errorMessages, 2, message);
                            return;
                        }
                        if (_this.privilegeIds === null || _this.privilegeIds.length < 1) {
                            message = "请选择系统用户角色的权限！";
                            layer.msg(message, {icon: 5});
                            _this.$set(_this.errorClasses, 3, true);
                            _this.$set(_this.errorMessages, 3, message);
                        }
                        flag = true;
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
                this.userRole = {higherRole: -1};
                this.errorClasses = [false, false, false, false, false];
                this.errorMessages = ["", "", "", "", ""];
                this.privilegeIds = [];
                this.zTree.checkAllNodes(false);
                this.zTree.cancelSelectedNode();
            }
        }
    });
});
