import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { Message, Tree } from 'element-ui'

$(document).ready(() => {
    Vue.prototype.$message = Message
    Vue.use(Tree)
    new Vue({
        el: '#edit_role',
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false],
            errorMessages: ['', '', '', '', ''],
            userRole: userRole,
            userRoles: userRoles,
            userPrivileges: userPrivileges,
            privilegeIds: [],
            privilegeTree: null,
            checkedList: []
        },
        created () {
            if (this.userRole === null) {
                this.userRole = { name: 'ROLE_', parentId: -1 }
                this.privilegeIds = []
            }
            this.privilegeTree = this.setTree(this.userPrivileges, 0)
        },
        mounted () {
            this.$refs.tree.setCheckedKeys(this.checkedList)
        },
        methods: {
            /**
             * 递归设置权限树形菜单
             * @param userPrivileges
             * @param parentId
             * @return {Array}
             */
            setTree (userPrivileges, parentId) {
                let nodes = []
                for (let i = 0; i < userPrivileges.length; i++) {
                    let node = {
                        id: userPrivileges[i].id,
                        label: userPrivileges[i].name,
                        parentId: userPrivileges[i].parentI

                    if (
                        typeof this.userRole.userPrivileges !== 'undefined'{
                        " &&
                    }
                    this.userRole.userPrivileges !== null
                )
                    {
                        for (
                            let j = 0;
                            j < this.userRole.userPrivileges.length;
                            j++
                        ) {
                            if (
                                userPrivileges[i].id ===
                                this.userRole.userPrivileges[j].id
                            ) {
                                this.checkedList.push(userPrivileges[i].id
                                this.privilegeIds.push(userPrivileges[i].id
                            } else if (
                                userPrivileges[i].parent ===
                                this.userRole.userPrivileges[j].id
                            ) {
                                this.checkedList.push(userPrivileges[i].id
                            }
                        }
                    }
                    if (
                        parentId === userPrivileges[i].parentId &&
                        userPrivileges[i].subUserPrivileges !== null &&
                        userPrivileges[i].subUserPrivileges.length > 0
                    ) {
                        node.children = this.setTree(
                            userPrivileges[i].subUserPrivileges,
                            userPrivileges[i].id

                    }
                    nodes.push(node
                }
                    return nodes
                }
            ,
                /**
                 * 树形菜单复选框事件
                 * @param obj
                 * @param isChecked
                 * @param childrenIsChecked
                 */
                checkChange(obj, isChecked, childrenIsChecked)
                {
                    if (isChecked) {
                        this.privilegeIds.push(obj.id)
                    } else {
                        this.privilegeIds.splice(
                            this.privilegeIds.findIndex((item) => item === obj.id),
                            1
                        )
                    }
                }
            ,
                /**
                 * 用户角色提交保存
                 * @param event
                 */
                submit(event)
                {
                    let message = null
                    if (this.userRole.name === '' || this.userRole.name === null) {
                        message = '系统用户角色名称不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 0, true)
                        this.$set(this.errorMessages, 0, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.userRole.description === null &&
                        this.userRole.description === ''
                    ) {
                        message = '系统用户角色描述不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 1, true)
                        this.$set(this.errorMessages, 1, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.userRole.parentId === null ||
                        this.userRole.parentId === -1
                    ) {
                        message = '请选择系统用户角色的上级角色！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 2, true)
                        this.$set(this.errorMessages, 2, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.privilegeIds === null ||
                        this.privilegeIds.length < 1
                    ) {
                        message = '请选择系统用户角色的权限！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 3, true)
                        this.$set(this.errorMessages, 3, message)
                        event.preventDefault()
                    }
                }
            ,
                /**
                 * 重置表单样式
                 */
                resetClass: function () {
                    this.userRole = { name: 'ROLE_', parentId: -1 }
                    this.errorClasses = [false, false, false, false, false]
                    this.errorMessages = ['', '', '', '', '']
                    this.privilegeIds = []
                }
            ,
            },
        });
});
