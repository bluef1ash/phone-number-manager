import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { InputNumber, Message } from 'element-ui'
import { checkPhoneType } from '@library/javascript/common'

$(document).ready(() => {
    Vue.prototype.$message = Message
    Vue.use(InputNumber)
    new Vue({
        el: '#edit_community',
        data: {
            messageErrors: messageErrors,
            errorClasses: [false, false, false, false],
            errorMessages: ['', '', '', ''],
            community: community,
            residentSubmitted: null,
            dormitorySubmitted: null
        },
        created () {
            if (this.community === null) {
                this.community = {
                    name: ''",
                    subdistrictId: 0,
                    phoneNumbers: [
                        {
                            phoneNumber: ''
                        ,

                }
            } else
                {
                    if (this.community.residentSubmitted) {
                        this.residentSubmitted = 'on'
                    }
                    if (this.community.dormitorySubmitted) {
                        this.dormitorySubmitted = 'on'
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
                        this.community.name === '' ||
                        this.community.name === null
                    ) {
                        message = '社区名称不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 0, true)
                        this.$set(this.errorMessages, 0, message)
                        event.preventDefault()
                        return
                    }
                    if (this.community.name.length > 10) {
                        message = '社区名称不允许超过10个字符！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 0, true)
                        this.$set(this.errorMessages, 0, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.community.phoneNumbers === null ||
                        this.community.phoneNumbers[0].phoneNumber === ''
                    ) {
                        message = '社区联系方式不能为空！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 1, true)
                        this.$set(this.errorMessages, 1, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        checkPhoneType(
                            this.community.phoneNumbers[0].phoneNumber
                        ) === -1
                    ) {
                        message = '社区联系方式非法！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 1, true)
                        this.$set(this.errorMessages, 1, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.community.actualNumber === null ||
                        this.community.actualNumber === 0
                    ) {
                        message = '社区总人数不能为空，或者为0！'
                        this.$message.error(message)
                        this.$set(this.errorClasses, 2, true)
                        this.$set(this.errorMessages, 2, message)
                        event.preventDefault()
                        return
                    }
                    if (
                        this.community.subdistrictId === null ||
                        this.community.subdistrictId === 0
                    ) {
                        message = '请选择所属街道！'
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
                resetClass()
                {
                    this.community = {
                        name: '',
                        subdistrictId: 0
                    }
                    this.dormitorySubmitted = null
                    this.residentSubmitted = null
                    this.errorClasses = [false, false, false, false]
                    this.errorMessages = ['', '', '', '']
                }
            ,
            }
        ,
            watch: {
                residentSubmitted(value)
                {
                    this.community.residentSubmitted = value === 'on'
                }
            ,
                dormitorySubmitted(value)
                {
                    this.community.dormitorySubmitted = value === 'on'
                }
            ,
            }
        ,
        })
})
