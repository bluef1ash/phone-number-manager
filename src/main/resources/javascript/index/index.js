import "@baseSrc/javascript/common/public";
import "@baseSrc/javascript/common/sidebar";
import "bootstrap";
import Vue from "vue";
import {$ajax, generateHexadecimalColors, formatNumber} from "@base/lib/javascript/common";
import countTo from "vue-count-to";
import {Loading, Switch} from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import {VeHistogram, VePie} from "v-charts/lib/index.esm";

$(document).ready(() => {
    Vue.prototype.$loading = Loading;
    Vue.use(Loading);
    Vue.use(Switch);
    $("[data-toggle=\"tooltip\"]").tooltip();
    new Vue({
        el: "#index",
        data: {
            csrf: $("meta[name='X-CSRF-TOKEN']"),
            systemUser: systemUser,
            systemCompanyType: systemCompanyType,
            communityCompanyType: communityCompanyType,
            subdistrictCompanyType: subdistrictCompanyType,
            loadings: [true, true, true, true, true],
            baseMessageSubdistrictId: -1,
            baseMessageCommunityId: -1,
            barChartSubdistrictId: -1,
            barChartSubcontractorSubdistrictId: -1,
            barChartSubcontractorCommunityId: -1,
            baseMessageDormitorySubdistrictId: -1,
            baseMessageDormitoryCommunityId: -1,
            barChartDormitorySubdistrictId: -1,
            barChartDormitoryType: true,
            companyBarChart: null,
            isDisplayChooseChart: true,
            allSubdistricts: [],
            subdistricts: [],
            communities: [],
            disabledCommunityId: true,
            disabledSubcontractorCommunityId: true,
            disabledDormitoryCommunityId: true,
            addCount: 0,
            haveToCount: 0,
            remnantCount: 0,
            percentCount: 0,
            percentCountTitle: null,
            barChart: null,
            barChartSubcontractor: null,
            barChartExtend: {
                grid: {
                    height: "auto",
                    width: "100%",
                    containLabel: true,
                    bottom: "5%"
                },
                tooltip: {
                    show: true,
                    trigger: "axis",
                    axisPointer: {
                        show: true,
                        type: "shadow"
                    }
                },
                series: {
                    label: {
                        normal: {
                            show: true,
                            position: "top",
                            formatter: "{c}"
                        }
                    }
                },
                xAxis: {
                    type: "category",
                    show: true,
                    position: "bottom",
                    axisLabel: {
                        show: true,
                        interval: 0,
                        formatter(value) {
                            let ret = value;
                            let maxLength = 2;
                            let valLength = value.length;
                            let rowN = Math.ceil(valLength / maxLength);
                            if (rowN > 1) {
                                ret = "";
                                for (let i = 0; i < rowN; i++) {
                                    let start = i * maxLength;
                                    ret += value.substring(start, start + maxLength) + "\n";
                                }
                            }
                            return ret;
                        }
                    }
                },
                yAxis: {
                    show: true,
                    type: "value",
                    axisLabel: {
                        formatter: "{value}"
                    }
                },
                color: []
            },
            dormitorySex: {
                male: 0,
                female: 0,
                maleParent: "0%",
                femaleParent: "0%"
            },
            dormitoryPieGrid: {
                height: "100%",
                width: "100%",
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            },
            dormitoryPieExtend: {
                legend: {
                    type: "plain",
                    orient: "vertical",
                    align: "left",
                    left: 0
                },
                series: {
                    center: ["80%", "50%"],
                    radius: [0, "75%"],
                    label: {
                        show: false
                    }
                },
                color: []
            },
            dormitoryAge: null,
            dormitoryEducation: null,
            dormitoryPoliticalStatus: null,
            dormitoryWorkStatus: null,
            dormitoryBarChart: null,
            dormitoryBarChartExtend: {
                color: [],
                series: {
                    label: {
                        normal: {
                            show: true,
                            position: "top",
                            formatter: "{c}"
                        }
                    }
                },
                xAxis: {
                    type: "category",
                    show: true,
                    position: "bottom",
                    axisLabel: null
                },
                yAxis: {
                    show: true,
                    type: "value",
                    axisLabel: {
                        formatter: "{value}"
                    }
                }
            },
            subcontractorBarChartExtend: {
                color: [],
                series: {
                    label: {
                        normal: {
                            show: true,
                            position: "top",
                            formatter: "{c}"
                        }
                    }
                },
                xAxis: {
                    type: "category",
                    show: true,
                    position: "bottom",
                    axisLabel: null
                },
                yAxis: {
                    show: true,
                    type: "value",
                    axisLabel: {
                        formatter: "{value}"
                    }
                }
            }
        },
        created() {
            this.dormitoryBarChartExtend.grid = this.subcontractorBarChartExtend.grid = this.barChartExtend.grid;
            this.dormitoryBarChartExtend.tooltip = this.subcontractorBarChartExtend.tooltip = this.barChartExtend.tooltip;
            this.dormitoryBarChartExtend.xAxis.axisLabel = this.subcontractorBarChartExtend.xAxis.axisLabel = this.barChartExtend.xAxis.axisLabel;
            $ajax({
                url: companySelectUrl,
                method: "post",
                headers: {
                    "X-CSRF-TOKEN": this.csrf.prop("content")
                }
            }, data => {
                if (data.state === 1) {
                    this.allSubdistricts = data.subdistricts;
                    data.subdistricts.forEach(item => {
                        this.subdistricts.push({
                            id: item.id,
                            name: item.name
                        });
                    });
                    if (this.systemUser.companyType === this.subdistrictCompanyType) {
                        this.communities = this.allSubdistricts[0].communities;
                    }
                }
            });
            if (this.systemUser.companyType === this.systemCompanyType) {
                this.baseMessageSubdistrictId = this.baseMessageCommunityId = this.barChartSubdistrictId = this.barChartSubcontractorSubdistrictId = this.barChartSubcontractorCommunityId = this.baseMessageDormitorySubdistrictId =  this.baseMessageDormitoryCommunityId = this.barChartDormitorySubdistrictId = 0;
                this.loadMessageAndChart();
            } else if (this.systemUser.companyType === this.subdistrictCompanyType) {
                this.baseMessageSubdistrictId = this.baseMessageDormitorySubdistrictId = this.barChartSubdistrictId = this.barChartDormitorySubdistrictId = this.barChartSubcontractorSubdistrictId = this.systemUser.companyId;
                this.baseMessageCommunityId = this.barChartSubcontractorCommunityId = this.baseMessageDormitoryCommunityId = 0;
                this.isDisplayChooseChart = this.disabledCommunityId = false;
                this.loadMessageAndChart(0, this.systemUser.companyId, this.systemUser.companyType);
            } else if (this.systemUser.companyType === this.communityCompanyType) {
                this.allSubdistricts.forEach(item => {
                    item.communities.some(community => {
                        if (community.id === this.systemUser.companyId) {
                            this.baseMessageSubdistrictId = item.subdistrictId;
                            this.communities = community;
                        }
                    });
                });
                this.baseMessageCommunityId = this.baseMessageDormitoryCommunityId = this.barChartSubcontractorCommunityId = this.systemUser.companyId;
                this.isDisplayChooseChart = false;
                this.loadMessageAndChart(null, this.systemUser.companyId, this.systemUser.companyType);
            }
        },
        components: {
            countTo,
            VeHistogram,
            VePie
        },
        methods: {
            /**
             * 切换街道获取对应社区
             * @param getType
             * @param subdistrictId
             * @param communityIdVar
             * @param communityDisabledVar
             */
            loadCommunities(getType, subdistrictId, communityIdVar, communityDisabledVar) {
                eval("this." + communityIdVar + " = 0");
                eval("this." + communityDisabledVar + " = true");
                if (this.allSubdistricts.length > 0 && subdistrictId > 0) {
                    eval("this." + communityDisabledVar + " = false");
                    this.loadMessageAndChart(getType, subdistrictId, this.subdistrictCompanyType);
                    this.allSubdistricts.some(item => {
                        if (subdistrictId === item.id) {
                            this.communities = item.communities;
                            return true;
                        }
                    });
                } else if (subdistrictId === -1) {
                    this.communities = [];
                } else {
                    eval("this." + communityIdVar + " = subdistrictId");
                    this.communities = [];
                    this.loadMessageAndChart(getType);
                }
            },
            /**
             * 加载居民录入基本信息与图表
             * @param getType
             * @param companyId
             * @param companyType
             * @param parentId
             */
            loadMessageAndChart(getType = null, companyId = null, companyType = null, parentId = null) {
                let params = {
                    getType,
                    companyType,
                    companyId
                };
                if (companyId === 0) {
                    if (companyType === this.subdistrictCompanyType) {

                    } else if (companyType === this.communityCompanyType) {
                        params.companyType = this.subdistrictCompanyType;
                        params.companyId = parentId;
                    }
                }
                switch (getType) {
                    case null:
                        this.$set(this.loadings, 0, true);
                        this.$set(this.loadings, 1, true);
                        this.$set(this.loadings, 2, true);
                        this.$set(this.loadings, 3, true);
                        this.$set(this.loadings, 4, true);
                        params.barChartTypeParam = this.barChartDormitoryType;
                        break;
                    case 1:
                        this.$set(this.loadings, 0, true);
                        break;
                    case 2:
                        this.$set(this.loadings, 1, true);
                        break;
                    case 3:
                        this.$set(this.loadings, 2, true);
                        break;
                    case 4:
                        this.$set(this.loadings, 3, true);
                        params.barChartTypeParam = this.barChartDormitoryType;
                        break;
                    case 5:
                        this.$set(this.loadings, 4, true);
                        break;
                    default:
                        params.barChartTypeParam = this.barChartDormitoryType;
                        break;
                }
                $ajax({
                    url: getComputedUrl,
                    method: "post",
                    headers: {
                        "X-CSRF-TOKEN": this.csrf.prop("content")
                    },
                    data: params
                }, data => {
                    this.percentCountTitle = "";
                    if (data.state === 1) {
                        if (data.resident.baseMessage) {
                            this.addCount = data.resident.baseMessage.addCount;
                            this.haveToCount = data.resident.baseMessage.haveToCount;
                            this.remnantCount = (this.haveToCount - this.addCount) * 0.8;
                            this.percentCount = this.addCount / this.haveToCount * 100;
                            if (this.percentCount < 80) {
                                this.percentCountTitle = "录入与核定比例不能低于80%！";
                            }
                            this.$set(this.loadings, 0, false);
                        }
                        if (data.resident.barChart) {
                            this.barChartExtend.color = generateHexadecimalColors();
                            this.barChart = data.resident.barChart.data;
                            this.barChartExtend.xAxis.data = data.resident.barChart.titleLabel;
                            this.barChartExtend.series.label.normal.formatter = (formatterData) => formatNumber(formatterData.value) + data.resident.barChart.formatter;
                            this.barChartExtend.yAxis.axisLabel.formatter = (formatterData) => formatNumber(formatterData) + data.resident.barChart.formatter;
                            this.$set(this.loadings, 1, false);
                        }
                        if (data.subcontractor.barChart) {
                            this.subcontractorBarChartExtend.color = generateHexadecimalColors();
                            this.barChartSubcontractor = data.subcontractor.barChart.data;
                            this.subcontractorBarChartExtend.xAxis.data = data.subcontractor.barChart.titleLabel;
                            this.subcontractorBarChartExtend.series.label.normal.formatter = (formatterData) => formatNumber(formatterData.value) + data.subcontractor.barChart.formatter;
                            this.subcontractorBarChartExtend.yAxis.axisLabel.formatter = (formatterData) => formatNumber(formatterData) + data.subcontractor.barChart.formatter;
                            this.$set(this.loadings, 4, false);
                        }
                        if (data.dormitory.baseMessage) {
                            this.dormitorySex = data.dormitory.baseMessage.sex;
                            let sexCount = this.dormitorySex.male + this.dormitorySex.female;
                            if (sexCount > 0) {
                                this.dormitorySex.maleParent = Math.ceil(this.dormitorySex.male / sexCount * 100);
                                this.dormitorySex.femaleParent = 100 - this.dormitorySex.maleParent;
                            }
                            this.dormitoryAge = data.dormitory.baseMessage.age;
                            this.dormitoryEducation = data.dormitory.baseMessage.education;
                            this.dormitoryPoliticalStatus = data.dormitory.baseMessage.politicalStatus;
                            this.dormitoryWorkStatus = data.dormitory.baseMessage.workStatus;
                            this.dormitoryPieExtend.color = generateHexadecimalColors();
                            this.$set(this.loadings, 2, false);
                        }
                        if (data.dormitory.barChart) {
                            this.dormitoryBarChartExtend.color = generateHexadecimalColors();
                            this.dormitoryBarChart = data.dormitory.barChart.data;
                            this.dormitoryBarChartExtend.yAxis.formatter = (formatterData) => formatNumber(formatterData) + data.dormitory.barChart.formatter;
                            this.dormitoryBarChartExtend.series.label.normal.formatter = (formatterData) => formatNumber(formatterData.value) + data.dormitory.barChart.formatter;
                            this.dormitoryBarChartExtend.xAxis.data = data.dormitory.barChart.titleLabel;
                            this.$set(this.loadings, 3, false);
                        }
                    }
                });
            },
            /**
             * 切换饼图显示
             * @param subdistrictId
             * @param getType
             */
            choosePieChart(subdistrictId, getType) {
                if (subdistrictId !== -1) {
                    let companyType = subdistrictId === 0 ? this.systemCompanyType : this.subdistrictCompanyType;
                    this.loadMessageAndChart(getType, subdistrictId, companyType);
                }
            },
            /**
             * 改变开关事件
             * @param newValue
             * @param companyId
             */
            changeSwitch(newValue, companyId) {
                this.choosePieChart(companyId, 4);
            }
        }
    });
});
