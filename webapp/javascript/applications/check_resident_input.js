define("check_resident_input", [
    "commonFunction",
    "vue",
    "jquery",
    "bootstrap",
    "layui"], function(commonFunction, Vue) {
    return function(communityResident, communities, subcontractors) {
        var vm = new Vue({
            el: "#resident",
            data: {
                hasErrors: [false, false, false, false, false, false, false],
                communityResident: communityResident,
                communities: communities,
                subcontractors: subcontractors,
                communityResidentName: "",
                communityResidentAddress: "",
                communityResidentPhone1: "",
                communityResidentPhone2: "",
                communityResidentPhone3: "",
                communityId: 0,
                subdistrictId: 0,
                subcontractorId: 0,
                newSubcontractors: [],
                subdistricts: [],
                newCommunities: []
            },
            created: function() {
                if (communityResident !== null) {
                    this.subcontractorId = communityResident.subcontractorId;
                }
                this.subdistricts.push(this.communities[0].subdistrict);
                for (var i = 0; i < this.communities.length; i++) {
                    for (var j = 0; j < this.subdistricts.length; j++) {
                        if (this.subdistricts[j].subdistrictId !==
                            this.communities[i].subdistrict.subdistrictId) {
                            this.subdistricts.push(this.communities[i].subdistrict);
                        }
                    }
                }
            },
            watch: {
                communityResidentName: function(newValue, oldValue) {
                    newValue = commonFunction.trim(newValue);
                    if (newValue.length > 10) {
                        this.communityResidentName = newValue.substring(0, 10);
                        this.useLayer("社区居民姓名不能超过10个字符！");
                    }
                },
                communityResidentAddress: function(newValue, oldValue) {
                    newValue = commonFunction.trim(newValue);
                    if (newValue.length > 10) {
                        this.communityResidentAddress = newValue.substring(0, 255);
                        this.useLayer("社区居民地址不能超过255个字符！");
                    }
                },
                communityResidentPhone1: function(newValue, oldValue) {
                    this.communityResidentPhone1 = this.checkInputPhone(newValue, oldValue, "社区居民联系方式一");
                },
                communityResidentPhone2: function(newValue, oldValue) {
                    this.communityResidentPhone2 = this.checkInputPhone(newValue, oldValue, "社区居民联系方式二");
                },
                communityResidentPhone3: function(newValue, oldValue) {
                    this.communityResidentPhone3 = this.checkInputPhone(newValue, oldValue, "社区居民联系方式三");
                }
            },
            methods: {
                /**
                 * 输入检查
                 * @param newValue
                 * @param oldValue
                 * @param message
                 * @return
                 */
                checkInputPhone: function(newValue, oldValue, message) {
                    var val = commonFunction.trim(newValue);
                    var value = "";
                    if (!/^\d*$/.test(val)) {
                        value = oldValue;
                        this.useLayer(message + "请输入数字！");
                    } else if (newValue.length > 11) {
                        value = newValue.substring(0, 11);
                        this.useLayer(message + "不能超过11个数字！");
                    } else {
                        value = val;
                    }
                    return value;
                },
                /**
                 * 切换街道
                 */
                chooseSubdistrict: function() {
                    this.newCommunities = [];
                    this.communityId = 0;
                    this.newSubcontractors = [];
                    this.subcontractorId = 0;
                    for (var i = 0; i < this.communities.length; i++) {
                        if (this.communities[i].subdistrict.subdistrictId ===
                            this.subdistrictId) {
                            this.newCommunities.push({
                                communityId: this.communities[i].communityId,
                                communityName: this.communities[i].communityName
                            });
                        }
                    }
                },
                /**
                 * 切换社区展示社区分包人
                 */
                chooseSubcontractor: function() {
                    this.newSubcontractors = [];
                    this.subcontractorId = 0;
                    for (var i = 0; i < this.subcontractors.length; i++) {
                        if (this.communityId ===
                            this.subcontractors[i].communityId) {
                            this.newSubcontractors.push({
                                id: this.subcontractors[i].subcontractorId,
                                name: this.subcontractors[i].name
                            });
                        }
                    }
                },
                /**
                 * 提交表单
                 * @param event
                 * @returns {*|boolean}
                 */
                submit: function(event) {
                    this.communityResidentName = commonFunction.trim(this.communityResidentName);
                    this.communityResidentAddress = commonFunction.trim(this.communityResidentAddress);
                    this.communityResidentPhone1 = commonFunction.trim(this.communityResidentPhone1);
                    this.communityResidentPhone2 = commonFunction.trim(this.communityResidentPhone2);
                    this.communityResidentPhone3 = commonFunction.trim(this.communityResidentPhone3);
                    if (this.communityResidentName === "") {
                        return this.stopSubmit(event, "社区居民姓名不能为空！", [0]);
                    } else if (this.communityResidentAddress === "") {
                        return this.stopSubmit(event, "社区居民家庭地址不能为空！", [1]);
                    } else if ((this.communityResidentPhone1 +
                        this.communityResidentPhone2 +
                        this.communityResidentPhone3) === "") {
                        return this.stopSubmit(event, "社区居民联系方式必须至少填写一项！", [
                            3,
                            4,
                            5]);
                    } else if (this.communityResidentPhone1 +
                        this.communityResidentPhone2 !== "" &&
                        this.communityResidentPhone1 ===
                        this.communityResidentPhone2) {
                        return this.stopSubmit(event, "社区居民联系方式一与社区居民联系方式二不能相同！", [
                            3,
                            4]);
                    } else if (this.communityResidentPhone1 +
                        this.communityResidentPhone3 !== "" &&
                        this.communityResidentPhone1 ===
                        this.communityResidentPhone3) {
                        return this.stopSubmit(event, "社区居民联系方式一与社区居民联系方式三不能相同！", [
                            3,
                            5]);
                    } else if (this.communityResidentPhone2 +
                        this.communityResidentPhone3 !== "" &&
                        this.communityResidentPhone2 ===
                        this.communityResidentPhone3) {
                        return this.stopSubmit(event, "社区居民联系方式二与社区居民联系方式三不能相同！", [
                            4,
                            5]);
                    } else if (commonFunction.checkPhoneType(this.communityResidentPhone1) ===
                        -1) {
                        return this.stopSubmit(event, "社区居民联系方式一输入不合法！", [3]);
                    } else if (commonFunction.checkPhoneType(this.communityResidentPhone2) ===
                        -1) {
                        return this.stopSubmit(event, "社区居民联系方式二输入不合法！", [4]);
                    } else if (commonFunction.checkPhoneType(this.communityResidentPhone3) ===
                        -1) {
                        return this.stopSubmit(event, "社区居民联系方式三输入不合法！", [5]);
                    } else if (this.communityId === 0) {
                        return this.stopSubmit(event, "必须选择所属社区！", [6]);
                    } else if (this.subcontractorId === 0) {
                        return this.stopSubmit(event, "必须选择所属社区分包人！", [7]);
                    } else {
                        return true;
                    }
                },
                /**
                 * 阻止表单默认提交
                 * @param event   事件对象
                 * @param message 提示信息
                 * @param indexes 控件索引号
                 * @returns {boolean} 不允许提交表单
                 */
                stopSubmit: function(event, message, indexes) {
                    this.useLayer(message);
                    for (var i = 0; i < indexes.length; i++) {
                        this.$set(this.hasErrors, indexes[i], true);
                    }
                    event.preventDefault();
                    return false;
                },
                /**
                 * 使用弹出框组件
                 * @param message
                 */
                useLayer: function(message) {
                    layui.use("layer", function() {
                        var layer = layui.layer;
                        layer.ready(function() {
                            layer.msg(message, {icon: 5});
                        });
                    });
                }
            }
        });
    };
});
