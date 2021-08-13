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
        el: '#configuration_list',
        data: {
            configurations: configurations
        },
        methods: {
            /**
             * 删除系统系统配置项
             * @param key
             */
            deleteObject (key) {
                deleteObject(this, deleteUrl, key, 'key')
            }
        }
    })
})
