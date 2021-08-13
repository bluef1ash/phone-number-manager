import '@jsSrc/common/public'
import '@jsSrc/common/sidebar'
import Vue from 'vue'
import { Message } from 'element-ui'
import { $ajax, companyHandler } from '@library/javascript/common'

$(document).ready(() => {
    Vue.prototype.$message = Message
    new Vue({
        el: '#edit_resident',
        data: {
            csrf: $('meta[name=\'X-CSRF-TOKEN\']'),
            csrfToken: null,
            messageErrors: messageErrors,
            errorClasses: [
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
            ],
            errorMessages: ['', '', '', '', '', '', '', ''],
            communityResident: communityResident,
            communities: communities,
            subcontractors: [],
            subdistrictId: 0,
            subdistricts: [],
            newCommunities: [],
            showPhoneInputs: [false, false]
        },
        created () {
            this.csrfToken = this.csrf.prop('content')
            if (this.communityResident === null) {
                this.communityResident = {
                    communityId: 0,
                    subcontractorId: 0,
                    community: {
                        residentSubmitted: false
                    },
                    phoneNumbers: [
                        {
                            phoneNumber: ''
                        },
                        {
                            phoneNumber: ''
                        },
                        {
                            phoneNumber: ''
                        }
                    ]
                }
            } else {
                if (this.communityResident.community === null) {
                    this.communityResident.community = {
                        residentSubmitted: false
                    }
                }
                this.loadSubcontractors()
                this.showPhoneInput(this.communityResident.phoneNumbers)
            }
            let company = companyHandler(
                this.communities,
                this.communityResident.communityId
            )
            this.newCommunities = company.newCommunities
            this.subdistrictId = company.subdistrictId
            this.subdistricts = company.subdistricts
        },
        methods: {
            /**
             * 判断是否显示联系方式输入框
             * @param phoneNumbers
             */
            showPhoneInput (phoneNumbers) {
                for (let i = 0; i < phoneNumbers.length; i++) {
                    if (
                        phoneNumbers[i] !== null &&
                        phoneNumbers[i].phoneNumber !== null &&
                        phoneNumbers[i].ph{
                        ''
                    }
                    eNumber !== ''
                )
                    {
                        this.showPhoneInuts[i] = true
                    }
                }
            },
            /**
             * 切换街道
             */
            chooseSubdistrict () {
                this.newCommunities = []
                this.communityResident.communityId = 0
                this.subcontractors = []
                this.communityResident.subcontractorId = 0
                if (this.subdistrictId !== 0) {
                    this.communities.forEach((item) => {
                        if (item.subdistrict.id === this.subdistrictId) {
                            this.newCommunities.push({
                                id: item.id,
                                name: item.name
                            })
                        }
                    })
                }
            },
            /**
             * 切换社区
             */
            chooseCommunity () {
                this.subcontractors = []
                this.communityResident.subcontractorId = 0
                this.loadSubcontractors()
            },
            /**
             * 加载社区分包人
             */
            loadSubcontractors () {
                if (this.communityResident.communityId !== 0) {
                    $ajax(
                        {
                            url:
                                loadSubcontractorsUrl +
                                '/' +
                                this.communityResident.communityId,
                            headers: {
                                'X-CSRF-TOKEN': this.csrf.prop('content')
                            }
                        },
                        (item) => {
                            if (item.state) {
                                this.subcontractors = item.subcontractors
                            }
                        },
                        null,
                        (csrfToken) => (this.csrfToken = csrfToken)
                    )
                }
            },
            /**
             * 社区居民提交保存
             * @param event
             */
            submit (event) {
                let message = null
                if (
                    this.communityResident.name === '' ||
                    this.communityResident.name === null
                ) {
                    message = '社区居民姓名不能为空！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 0, true)
                    this.$set(this.errorMessages, 0, message)
                    event.preventDefault()
                    return
                }
                if (this.communityResident.name.length > 10) {
                    message = '社区居民姓名不允许超过10个字符！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 0, true)
                    this.$set(this.errorMessages, 0, message)
                    event.preventDefault()
                    return
                }
                if (
                    this.communityResident.address === null ||
                    this.communityResident.address === ''
                ) {
                    message = '社区居民家庭地址不能为空！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 1, true)
                    this.$set(this.errorMessages, 1, message)
                    event.preventDefault()
                    return
                }
                let isEmptyPhone1 =
                    typeof this.communityResident.phoneNumbers[0].phoneNumber === 'undefined' ||
                    this.communityResident.phoneNumbers[0].phoneNumber ===
                    null ||
                    this.communityResident.phoneNumbers[0].phoneNumber === ''
                let isEmptyPhone2 =
                    typeof this.communityResident.phoneNumbers[1].phoneNumber === 'undefined' ||
                    this.communityResident.phoneNumbers[1].phoneNumber ===
                    null ||
                    this.communityResident.phoneNumbers[1].phoneNumber === ''
                let isEmptyPhone3 =
                    typeof this.communityResident.phoneNumbers[2].phoneNumber === 'undefined' ||
                    this.communityResident.phoneNumbers[2].phoneNumber ===
                    null ||
                    this.communityResident.phoneNumbers[2].phoneNumber === ''
                if (isEmptyPhone1 && isEmptyPhone2 && isEmptyPhone3) {
                    message = '至少填写一个社区居民联系方式！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 2, true)
                    this.$set(this.errorMessages, 2, message)
                    this.$set(this.errorClasses, 3, true)
                    this.$set(this.errorMessages, 3, message)
                    this.$set(this.errorClasses, 4, true)
                    this.$set(this.errorMessages, 4, message)
                    event.preventDefault()
                    return
                }
                let phoneNumbers = []
                for (
                    let i = 0;
                    i < this.communityResident.phoneNumbers.length;
                    i++
                ) {
                    if (
                        this.communityResident.phoneNumbers[i].phoneNumber !==
                        null &&
                        this.communityResident.phoneNumbers[i].phoneNumber !==
                        ''
                    ) {
                        phoneNumbers.push(
                            this.communityResident.phoneNumbers[i]
                        )
                    }
                }
                this.communityResident.phoneNumbers = phoneNumbers
                if (
                    checkPhoneType(
                        this.communityResident.phoneNumbers[0].phoneNumber
                    ) === -1
                ) {
                    message = '社区居民联系方式一非法！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 2, true)
                    this.$set(this.errorMessages, 2, message)
                    event.preventDefault()
                    return
                }
                if (
                    checkPhoneType(
                        this.communityResident.phoneNumbers[1].phoneNumber
                    ) === -1
                ) {
                    message = '社区居民联系方式二非法！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 3, true)
                    this.$set(this.errorMessages, 3, message)
                    event.preventDefault()
                    return
                }
                if (
                    checkPhoneType(
                        this.communityResident.phoneNumbers[2].phoneNumber
                    ) === -1
                ) {
                    message = '社区居民联系方式三非法！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 4, true)
                    this.$set(this.errorMessages, 4, message)
                    event.preventDefault()
                    return
                }
                if (
                    this.communityResident.communityId === null ||
                    this.communityResident.communityId === 0
                ) {
                    message = '请选择所属社区！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 6, true)
                    this.$set(this.errorMessages, 6, message)
                    event.preventDefault()
                    return
                }
                if (
                    this.communityResident.subcontractorId === null ||
                    this.communityResident.subcontractorId === 0
                ) {
                    message = '请选择社区分包人！'
                    this.$message.error(message)
                    this.$set(this.errorClasses, 7, true)
                    this.$set(this.errorMessages, 7, message)
                    event.preventDefault()
                }
            },
            /**
             * 重置表单样式
             */
            resetClass () {
                this.communityResident = {
                    communityId: 0,
                    subcontractorId: 0,
                    phoneNumbers: [
                        {
                            phoneNumber: ''
                        },
                        {
                            phoneNumber: ''
                        },
                        {
                            phoneNumber: ''
                        }
                    ]
                }
                this.subdistrictId = 0
                this.errorClasses = [
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
                ]
                this.errorMessages = ['', '', '', '', '', '', '', '']
            }
        }
    })
})
