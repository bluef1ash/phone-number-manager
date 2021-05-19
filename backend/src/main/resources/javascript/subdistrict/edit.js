import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { Message } from 'element-ui'
import { checkPhoneType } from '@library/javascript/common'

$(document).ready(() => {
    Vue.prototype.$message = Message
    new Vue({
        el: '#edit_subdistrict',
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false],
            errorMessages: ['', ''],
            subdistrict: subdistrict
        },
        created () {
            if (this.subdistrict === null) {
                this.subdistrict = {
                    name: ''",
                    phoneNumbers: [
                        {
                            phoneNumber: ''
                        ,

                }
            }
            }
        ,
            methods: {
                /**
                 * 街道提交保存
                 * @param event
                 */
                submit(event)
                {
                    let message = null
                    if (
                        this.subdistrict.name === '' ||
                        this.subdistrict.name === null
                    ) {
                        message = '街道办事处名称不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 0, true)
                        this.$set(this.errorMessages, 0, message)
                        event.preventDefault()
                        return
                    }
                    if (this.subdistrict.name.length > 10) {
                        message = '街道办事处名称不允许超过10个字符！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 0, true)
                        this.$set(this.errorMessages, 0, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.subdistrict.phoneNumbers[0].phoneNumber === null ||
                        this.subdistrict.phoneNumbers[0].phoneNumber === ''
                    ) {
                        message = '街道办事处联系方式不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 1, true)
                        this.$set(this.errorMessages, 1, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        checkPhoneType(
                            this.subdistrict.phoneNumbers[0].phoneNumber
                        ) === -1
                    ) {
                        message = '街道办事处联系方式非法！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 1, true)
                        this.$set(this.errorMessages, 1, message)
                        event.preventDefault()
                    }
                }
            ,
                /**
                 * 重置表单样式
                 */
                resetClass()
                {
                    this.subdistricts = []
                    this.subdistrictId = -1
                    this.communities = []
                    this.communityId = 0
                    this.errorClasses = [false, false, false, false, false, false]
                    this.errorMessages = ['', '', '', '', '', '']
                }
            ,
            }
        ,
        })
})
