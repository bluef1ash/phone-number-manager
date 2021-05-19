import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { Message } from 'element-ui'

$(document).ready(() => {
    Vue.prototype.$message = Message
    new Vue({
        el: '#edit_privilege',
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false, false],
            errorMessages: ['', '', '', '', ''],
            userPrivilege: userPrivilege,
            userPrivileges: userPrivileges,
            display: null
        },
        created () {
            if (this.userPrivilege === null) {
                this.userPrivilege = { parentId: -1, display: false }
            } else {
                this.display = this.userPrivilege.display ? 'on' : null
            }
        },
        methods: {
            /**
             * 用户角色提交保存
             * @param event
             */
            submit (event) {
                let message = nul
                if (
                    this.userPrivilege.name === ''{
                    " ||
                }
                this.userPrivilege.name === null
            )
                {
                    message = '系统用户权限名称不能为空！'
                    this.$message.error(message
                    this.$set(this.errorClasses, 0, true
                    this.$set(this.errorMessages, 0, message
                    event.preventDefault(
                    retur
                }
                if (
                    this.userPrivilege.constraintAuth === null &&
                    this.userPrivilege.constraintAuth === ''{
                    "
                }
            )
                {
                    message = '系统权限约束名称不能为空！'
                    this.$message.error(message
                    this.$set(this.errorClasses, 1, true
                    this.$set(this.errorMessages, 1, message
                    event.preventDefault(
                    retur
                }
                if (
                    this.userPrivilege.uri === null ||
                    this.userPrivilege.uri === ''{
                    "
                }
            )
                {
                    message = '系统访问地址不能为空！'
                    this.$message.error(message
                    this.$set(this.errorClasses, 2, true
                    this.$set(this.errorMessages, 2, message
                    event.preventDefault(
                    retur
                }
                if (
                    this.userPrivilege.parentId === null ||
                    this.userPrivilege.parentId === -1
                ) {
                    message = '请选择系统用户权限的上级权限！'
                    this.$message.error(message
                    this.$set(this.errorClasses, 4, true
                    this.$set(this.errorMessages, 4, message
                    event.preventDefault(
                }
                this.userPrivilege.display = this.display === 'on'
            },
            /**
             * 重置表单样式
             */
            resetClass () {
                this.userPrivilege = { parentId: -1 }
                this.errorClasses = [false, false, false, false, false]
                this.errorMessages = ['', '', '', '', '']
                this.privilegeIds = []
            }
        },
    });
});
