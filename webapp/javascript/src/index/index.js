import "@base/javascript/src/common/public";
import "@base/javascript/src/common/sidebar";
import "bootstrap";
import Vue from "vue";
import countTo from "vue-count-to";
import VeHistogram from "v-charts/lib/bar.common";

$(document).ready(() => {
    $("[data-toggle=\"tooltip\"]").tooltip();
    new Vue({
        el: "#index",
        data: {
            csrf: csrf,
            systemUser: systemUser,
            systemRoleId: systemRoleId,
            communityRoleId: communityRoleId,
            subdistrictRoleId: subdistrictRoleId,
            baseMessageSubdistrictId: -1,
            baseMessageCommunityId: -1,
            barChartSubdistrictId: -1,
            companyBarChart: null,
            isDisplayChooseChart: true,
            allSubdistricts: [],
            subdistricts: [],
            communities: [],
            addCount: 0,
            haveToCount: 0,
            remnantCount: 0,
            percentCount: 0,
            percentCountTitle: null,
            barChart: null,
            barChartExtend: {
                grid: {
                    height: "100%",
                    width: "100%"
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
                            formatter: "{c}人"
                        }
                    }
                },
                xAxis: {
                    type: "category",
                    show: true,
                    position: "bottom",
                    axisLabel: {
                        show: true,
                        nameLocation: "end",
                        interval: 0,
                        rotate: 45
                    }
                },
                yAxis: {
                    show: true,
                    type: "value",
                    axisLabel: {
                        formatter: "{value}人"
                    }
                },
                color: []
            }
        },
        created() {
            $.ajax({
                url: companySelectUrl,
                method: "get",
                data: {
                    _csrf: this.csrf
                }
            }).then(data => {
                if (data.state === 1) {
                    this.allSubdistricts = data.subdistricts;
                    data.subdistricts.forEach(item => {
                        this.subdistricts.push({
                            id: item.subdistrictId,
                            name: item.subdistrictName
                        });
                    });
                }
            });
            if (this.systemUser.roleId === this.systemRoleId) {
                this.baseMessageSubdistrictId = 0;
                this.baseMessageCommunityId = 0;
                this.barChartSubdistrictId = 0;
                this.loadMessageAndChart();
            } else if (this.systemUser.roleId === this.subdistrictRoleId) {
                this.baseMessageSubdistrictId = this.systemUser.roleLocationId;
                this.baseMessageCommunityId = 0;
                this.isDisplayChooseChart = false;
                this.loadMessageAndChart(0, this.systemUser.roleLocationId, this.systemUser.roleId);
            } else if (this.systemUser.roleId === this.communityRoleId) {
                this.allSubdistricts.forEach(item => {
                    item.communities.some(community => {
                        if (community.communityId === this.systemUser.roleLocationId) {
                            this.baseMessageSubdistrictId = item.subdistrictId;
                            this.communities = community;
                        }
                    });
                });
                this.baseMessageCommunityId = this.systemUser.roleLocationId;
                this.isDisplayChooseChart = false;
                this.loadMessageAndChart(null, this.systemUser.roleLocationId, this.systemUser.roleId);
            }
        },
        mounted() {
        },
        components: {
            countTo,
            VeHistogram
        },
        methods: {
            /**
             * 切换街道获取对应社区
             */
            loadCommunity() {
                if (this.allSubdistricts.length > 0 && this.baseMessageSubdistrictId > 0) {
                    this.baseMessageCommunityId = 0;
                    this.loadMessageAndChart(1, this.baseMessageSubdistrictId, this.subdistrictRoleId);
                    this.allSubdistricts.some(item => {
                        if (this.baseMessageSubdistrictId === item.subdistrictId) {
                            this.communities = item.communities;
                            return true;
                        }
                    });
                } else if (this.baseMessageSubdistrictId === -1) {
                    this.baseMessageCommunityId = -1;
                    this.communities = [];
                } else {
                    this.loadMessageAndChart(1);
                    this.baseMessageCommunityId = this.baseMessageSubdistrictId;
                    this.communities = [];
                }
            },
            /**
             * 加载居民录入基本信息与图表
             * @param getType
             * @param id
             * @param companyType
             */
            loadMessageAndChart(getType = null, id = null, companyType = null) {
                $.ajax({
                    url: getComputedUrl,
                    method: "get",
                    async: false,
                    data: {
                        _csrf: this.csrf,
                        getType,
                        companyType,
                        id
                    }
                }).then(data => {
                    this.percentCountTitle = "";
                    if (data.state === 1) {
                        if (data.baseMessage) {
                            this.addCount = data.baseMessage.addCount;
                            this.haveToCount = data.baseMessage.haveToCount;
                            this.remnantCount = (this.haveToCount - this.addCount) * 0.8;
                            this.percentCount = this.addCount / this.haveToCount * 100;
                            if (this.percentCount < 80) {
                                this.percentCountTitle = "录入与核定比例不能低于80%！";
                            }
                        }
                        if (data.barChart) {
                            this.barChartExtend.color = [];
                            for (let i = 0; i < 10; i++) {
                                let color = "#" + ("00000" + (Math.random() * 0x1000000 << 0).toString(16)).substr(-6);
                                this.barChartExtend.color.push(color);
                            }
                            this.barChart = data.barChart.data;
                            this.barChartExtend.xAxis.data = data.barChart.titleLabel;
                        }
                    }
                });
            },
            /**
             * 切换图标显示
             */
            chooseChart() {
                if (this.barChartSubdistrictId !== -1) {
                    let companyType = this.barChartSubdistrictId === 0 ? this.systemRoleId : this.subdistrictRoleId;
                    this.loadMessageAndChart(2, this.barChartSubdistrictId, companyType);
                }
            }
        }
    });
});
