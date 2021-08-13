import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { Message, MessageBox } from 'element-ui'
import { deleteObject } from '@library/javascript/common'

$(document).ready(() => {
    Vue.prototype.$message = Message
    Vue.directive(MessageBox.name, MessageBox)
    Vue.prototype.$confirm = MessageBox.confirm
    new Vue({
        el: '#role_table',
        data: {
            userRoles: userRoles
        },
        methods: {
            /**
             * 删除系统用户角色
             * @param id
             */
            deleteObject (id) {
                if (id === 1) {
                    this.$message.error('不允许删除管理员角色！')
                    return
                }
                deleteObject(this, deleteUrl, id)
            }
        }
    })
})
