<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <div class="row resident-list-buttons">
                            <button class="btn btn-warning" data-toggle="modal" data-target="#import_as_system_modal" title="从Excel文件导入系统" role="button" @click="loadSubdistricts" v-if="companyType !== companyTypes.communityCompanyType">从Excel文件导入系统</button>
                            <div class="btn-group">
                                <button @click="exportExcel" :class="['btn', 'btn-secondary', companyType !== companyTypes.communityCompanyType ? 'is-dropdown' : '']" type="button">导出到Excel</button>
                                <button aria-expanded="false" aria-haspopup="true" class="btn btn-secondary dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" id="export_excel" type="button" v-if="companyType !== companyTypes.communityCompanyType">
                                    <span class="sr-only">选择导出的单位</span>
                                </button>
                                <div aria-labelledby="export_excel" class="dropdown-menu" x-placement="bottom-start" v-if="companyType !== companyTypes.communityCompanyType">
                                    <el-tree :check-strictly="isStrictly" :data="companies2" :highlight-current="true" @check-change="chooseExportExcel" node-key="value" ref="export_excel_tree" show-checkbox></el-tree>
                                </div>
                            </div>
                            <div class="btn-group">
                                <button @click="commitCommunity" :class="['btn', 'btn-primary', companyType !== companyTypes.communityCompanyType ? 'is-dropdown' : '']" type="button">提交社区保存</button>
                                <button class="btn btn-primary dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" aria-expanded="false" aria-haspopup="true" id="commit_community" type="button" v-if="companyType !== companyTypes.communityCompanyType">
                                    <span class="sr-only">提交社区保存</span>
                                </button>
                                <div aria-labelledby="commit_community" class="dropdown-menu" x-placement="bottom-start" v-if="companyType !== companyTypes.communityCompanyType">
                                    <el-tree :check-strictly="isStrictly" :data="companies2" :highlight-current="true" @check-change="chooseCommitCommunity" node-key="value" ref="commit_community_tree" show-checkbox></el-tree>
                                </div>
                            </div>
                            <a :href="publicParams.createUrl" :title="'添加社区' + dataTypeName" class="btn btn-primary" v-text="'添加社区' + dataTypeName"></a>
                        </div>
                        <div class="col-md-12 d-none d-md-block">
                            <div class="row">
                                <form action="javascript:" class="form-inline list-search" method="get">
                                    <div class="form-group">
                                        <label>单位</label>
                                        <el-cascader :clearable="true" :options="companies" :props="cascaderProps" placeholder="单位搜索" v-model="searchCompanyIds"></el-cascader>
                                    </div>
                                    <div class="form-group">
                                        <label for="name" v-text="'社区' + dataTypeName + '姓名'"></label>
                                        <input :placeholder="'请输入查找的社区' + dataTypeName + '姓名'" class="form-control" id="name" type="text" v-model="searchParams.name">
                                    </div>
                                    <div class="form-group" v-if="dataType === 1">
                                        <label for="sex">社区楼长性别</label>
                                        <select class="form-control" id="sex" v-model="searchParams.sex">
                                            <option :value="-1">请选择</option>
                                            <option :value="0">男</option>
                                            <option :value="1">女</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="address">家庭住址</label>
                                        <input :placeholder="'请输入查找的社区' + dataTypeName + '家庭住址'" class="form-control" id="address" type="text" v-model="searchParams.address">
                                    </div>
                                    <div class="form-group">
                                        <label for="phone" v-text="'社区' + dataTypeName + '联系方式'"></label>
                                        <input :placeholder="'请输入查找的社区' + dataTypeName + '联系方式'" class="form-control" id="phone" type="text" v-model="searchParams.phone">
                                    </div>
                                    <div class="form-group">
                                        <button class="btn btn-sm btn-danger" @click="searchClear">
                                            <i class="fa fa-ban"></i>重置
                                        </button>
                                        <button class="btn btn-sm btn-primary" @click="search">
                                            <i class="fa fa-search"></i>查询
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <el-table :data="data" border v-loading="isLoading">
                            <el-table-column :index="index => index + 1" label="序号" type="index">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.$index + 1" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column :label="dataType === 0 ? '社区居民姓名' : '社区楼长姓名'" prop="name">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.name" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="社区楼长性别" prop="sexName" v-if="dataType === 1">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.sexName" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="家庭住址" prop="address" v-if="dataType === 0">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.address" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="年龄" prop="age" v-if="dataType === 1">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.age" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="联系方式（多个联系方式以英文逗号分隔）" prop="phones">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.phones" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="所属社区居委会" prop="community.name">
                                <template v-slot="scope">
                                    <element-table-popover :data="scope.row.community.name" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作">
                                <template v-slot="scope">
                                    <div class="row" v-if="(!scope.row.community.dormitorySubmitted && dataType === 1) || (!scope.row.community.residentSubmitted && dataType === 0)">
                                        <div class="col-6">
                                            <a :href="publicParams.editUrl + scope.row.id" class="btn btn-sm btn-block btn-pill btn-secondary" title="点击修改">修改</a>
                                        </div>
                                        <div class="col-6">
                                            <button @click="deleteObject(scope.row.id)" class="btn btn-sm btn-block btn-pill btn-danger" title="点击删除" type="button">删除</button>
                                        </div>
                                    </div>
                                    <div class="row" v-if="(scope.row.community.dormitorySubmitted && dataType === 1) || (scope.row.community.residentSubmitted && dataType === 0)">
                                        <div class="col-12">
                                            <a :href="publicParams.editUrl + scope.row.id" class="btn btn-sm btn-block btn-pill btn-secondary" title="点击查看">查看</a>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>
                        </el-table>
                        <nav>
                            <el-pagination layout="total, prev, pager, next, jumper" :total="pageInfo.total" prev-text="上一页" next-text="下一页" :current-page="pagination" @current-change="paginationCurrentChange"></el-pagination>
                        </nav>
                    </div>
                </div>
            </div>
            <!-- /.col-->
        </div>
        <div class="modal fade" id="import_as_system_modal" tabindex="-1" role="dialog" aria-labelledby="import_as_system_modal_label" aria-hidden="true" v-if="companyType !== companyTypes.communityCompanyType">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">选择导入的街道</h4>
                        <button class="close" type="button" data-dismiss="modal" aria-label="关闭">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form class="form-inline" action="javascript:" method="get">
                            <div class="form-group">
                                <label for="subdistrict_id">选择导入的街道</label>
                                <select class="form-control" id="subdistrict_id" v-model="subdistrictId">
                                    <option :value="0">请选择</option>
                                    <option :value="subdistrict.id" v-for="subdistrict in subdistricts" v-text="subdistrict.name"></option>
                                </select>
                            </div>
                            <el-upload :action="publicParams.importAsSystemUrl" :auto-upload="false" :before-upload="beforeUploadExcel" :headers="{'X-CSRF-TOKEN': csrf.prop('content')}" :data="{subdistrictId}" :on-error="uploadError" :on-progress="uploadProgress" :on-success="uploadSuccess" :show-file-list="false" accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" class="upload-demo" ref="uploadExcel">
                                <el-button size="small" type="primary">浏览文件</el-button>
                            </el-upload>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-dismiss="modal">关闭</button>
                        <button class="btn btn-primary" type="button" @click="uploadExcel">导入</button>
                    </div>
                </div>
                <!-- /.modal-content-->
            </div>
            <!-- /.modal-dialog-->
        </div>
        <!-- /.modal-->
        <iframe :src="exportExcelUrl" class="hidden" v-if="isDownload"></iframe>
    </div>
</template>
<script>
    import commonFunction from "@base/lib/javascript/common";
    import {Base64} from "js-base64";
    import elementTablePopover from "@baseSrc/javascript/component/element-table-popover";

    export default {
        data() {
            return {
                csrf: $("meta[name='X-CSRF-TOKEN']"),
                publicParams: publicPrams,
                data: data.data,
                dataType: data.dataType,
                dataTypeName: null,
                pageInfo: data.pageInfo,
                companyTypes: data.companyTypes,
                companyType: data.companyType,
                companies: [],
                cascaderProps: {
                    checkStrictly: true
                },
                companies2: [],
                subdistricts: [],
                subdistrictId: 0,
                loading: null,
                isLoading: false,
                pagination: 1,
                searchParams: {
                    sex: -1
                },
                searchCompanyIds: null,
                isDownload: false,
                timeout: new Date().getTime(),
                exportExcelIds: [],
                commitCommunityIds: [],
                exportExcelUrl: null,
                isStrictly: false
            };
        },
        created() {
            this.dataTypeName = this.dataType === 0 ? "居民" : "楼长";
            if (this.pagination !== null && this.pagination > 1) {
                this.loadData();
            }
            this.loadCompanies();
        },
        components: {
            elementTablePopover
        },
        methods: {
            /**
             * 删除条目
             * @param id
             */
            deleteObject(id) {
                commonFunction.deleteObject(this, this.publicParams.deleteUrl, id);
            },
            /**
             * 加载单位
             */
            loadCompanies() {
                if (this.companies.length === 0) {
                    commonFunction.$ajax({
                        url: this.publicParams.companySelectUrl,
                        method: "post",
                        headers: {
                            "X-CSRF-TOKEN": this.csrf.prop("content")
                        }
                    }, result => {
                        if (result.state === 1) {
                            let disabled = false;
                            this.isStrictly = false;
                            if (this.companyType === this.companyTypes.communityCompanyType) {
                                disabled = true;
                                this.isStrictly = true;
                            }
                            result.subdistricts.forEach(item => {
                                let node1 = {value: item.id, label: item.name, companyType: this.companyTypes.subdistrictCompanyType};
                                let node2 = {value: item.id, label: item.name, companyType: this.companyTypes.subdistrictCompanyType, disabled};
                                if (item.communities) {
                                    node1.children = [];
                                    node2.children = [];
                                    item.communities.forEach(community => {
                                        let children = {value: community.id, label: community.name, companyType: this.companyTypes.communityCompanyType};
                                        node1.children.push(children);
                                        node2.children.push(children);
                                    });
                                }
                                this.companies.push(node1);
                                this.companies2.push(node2);
                            });
                        }
                    });
                }
            },
            /**
             * 搜索
             */
            search() {
                this.searchParams.isSearch = true;
                this.searchParams.isFrist = true;
                if (this.searchCompanyIds !== null) {
                    if (this.searchCompanyIds.length === 1) {
                        this.searchParams.companyId = this.searchCompanyIds[0];
                        this.searchParams.companyType = this.companyTypes.subdistrictCompanyType;
                    } else if (this.searchCompanyIds.length > 1) {
                        this.searchParams.companyId = this.searchCompanyIds[1];
                        this.searchParams.companyType = this.companyTypes.communityCompanyType;
                    }
                }
                this.pagination = 1;
                if (this.$route.path !== "/") {
                    this.$router.push({path: "/"});
                }
                this.loadData();
            },
            /**
             * 搜索重置
             */
            searchClear() {
                this.searchParams = {
                    sex: -1
                };
                this.searchCompanyIds = null;
                this.pagination = 1;
                this.$router.push("/");
                this.loadData();
            },
            /**
             * 加载街道
             */
            loadSubdistricts() {
                if (this.subdistricts.length === 0) {
                    commonFunction.$ajax({
                        url: this.publicParams.loadSubdistrictsUrl,
                        method: "post",
                        headers: {
                            "X-CSRF-TOKEN": this.csrf.prop("content")
                        }
                    }, result => {
                        if (result.state === 1) {
                            this.subdistricts = result.subdistricts;
                        }
                    });
                }
            },
            /**
             * 上传Excel
             */
            uploadExcel() {
                this.$refs.uploadExcel.submit();
            },
            /**
             * Excel上传前
             * @param file
             * @return {boolean}
             */
            beforeUploadExcel(file) {
                if (this.subdistrictId === 0) {
                    this.$message({
                        message: "请选择导入的街道！",
                        type: "error"
                    });
                    return false;
                }
                if (!file) {
                    this.$message({
                        message: "请选择需要导入的Excel文件！",
                        type: "error"
                    });
                    return false;
                }
                this.loading = this.$loading({
                    lock: true,
                    text: "正在上传数据，请稍等。。。",
                    spinner: "el-icon-loading",
                    background: "rgba(0, 0, 0, 0.8)"
                });
            },
            /**
             * 上传时回调
             * @param event
             * @param file
             * @param fileList
             */
            uploadProgress(event, file, fileList) {
                if (event.percent < 100) {
                    this.loading.text = "正在上传数据，请稍等。。。已上传" + Math.round(event.percent) + "%";
                } else {
                    this.loading.text = "已上传完成，正在处理数据，请稍等。。。";
                }
            },
            /**
             * 上传成功时回调
             * @param response
             * @param file
             * @param fileList
             */
            uploadSuccess(response, file, fileList) {
                this.loading.close();
                if (response.state === 0) {
                    this.$message.error(response.message);
                } else {
                    this.$message.success(response.message);
                    $("#import_as_system_modal").modal("hide");
                    this.loadData();
                }
            },
            /**
             * 上传Excel失败回调
             * @param error
             * @param file
             * @param fileList
             */
            uploadError(error, file, fileList) {
                this.loading.close();
                this.$message({
                    message: "上传Excel文件失败！",
                    type: "error"
                });
            },
            /**
             * 加载数据
             */
            loadData() {
                this.pagination = this.$route.path.substring(1) === "" ? 1 : parseInt(this.$route.path.substring(1));
                let data = {
                    page: this.pagination
                };
                if (Object.keys(this.searchParams).length > 0 && this.searchParams.isSearch) {
                    this.searchParams.isFrist = false;
                    data = Object.assign(data, this.searchParams);
                }
                commonFunction.$ajax({
                    url: this.publicParams.loadDataUrl,
                    method: "post",
                    data: data,
                    beforeSend: xmlHttpRequest => {
                        xmlHttpRequest.setRequestHeader("X-CSRF-TOKEN", this.csrf.prop("content"));
                        this.isLoading = true;
                    }
                }, result => {
                    this.isLoading = false;
                    if (result.state === 1) {
                        this.data = result.data;
                        this.pageInfo = result.pageInfo;
                    } else {
                        this.$message.error(result.messageErrors[0].defaultMessage);
                    }
                });
            },
            /**
             * 页码切换回调
             * @param currentPageNumber
             */
            paginationCurrentChange(currentPageNumber) {
                this.pagination = currentPageNumber;
                this.$router.push({path: "/" + currentPageNumber});
            },
            /**
             * 导出Excel
             */
            exportExcel() {
                if (this.companyType === this.companyTypes.communityCompanyType) {
                    this.exportExcelIds = [
                        {
                            companyType: this.companyTypes.communityCompanyType,
                            companyId: this.data[0].communityId
                        }];
                }
                if (this.exportExcelIds.length === 0) {
                    this.$message.error("请选择导出单位！");
                    return;
                }
                this.$cookie.set("exportExcel", "wait", {expires: "5m"});
                this.loading = this.$loading({
                    lock: true,
                    text: "正在生成Excel，请稍后。。。",
                    spinner: "el-icon-loading",
                    background: "rgba(0, 0, 0, 0.8)"
                });
                this.exportExcelUrl = this.publicParams.downloadExcelUrl + "?data=" + Base64.encodeURI(JSON.stringify(this.exportExcelIds));
                this.isDownload = true;
                let interval = setInterval(() => {
                    if (this.isDownload && this.$cookie.get("exportExcel") === "success") {
                        this.stopExportExcel(interval, "导出Excel文件完成！", "success");
                    } else if (this.isDownload && this.$cookie.get("exportExcel") === "error") {
                        this.stopExportExcel(interval, "导出Excel文件失败！", "error");
                    }
                }, 5000);
            },
            /**
             * 停止导出Excel页面
             * @param interval
             * @param message
             * @param messageType
             */
            stopExportExcel(interval, message, messageType) {
                this.loading.close();
                this.$message({
                    message: message,
                    type: messageType
                });
                this.isDownload = false;
                this.$cookie.delete("exportExcel");
                clearInterval(interval);
            },
            /**
             * 选择导出Excel
             * @param obj
             * @param isChecked
             * @param childrenIsChecked
             */
            chooseExportExcel(obj, isChecked, childrenIsChecked) {
                this.chooseIds(obj, this.exportExcelIds, isChecked, childrenIsChecked);
            },
            /**
             * 选择提交社区
             * @param obj
             * @param isChecked
             * @param childrenIsChecked
             */
            chooseCommitCommunity(obj, isChecked, childrenIsChecked) {
                this.chooseIds(obj, this.commitCommunityIds, isChecked, childrenIsChecked);
            },
            /**
             * 选择编号
             * @param obj
             * @param objIds
             * @param isChecked
             * @param childrenIsChecked
             */
            chooseIds(obj, objIds, isChecked, childrenIsChecked) {
                if (isChecked) {
                    new Date().getTime() - this.timeout > 5 && objIds.push({companyId: obj.value, companyType: obj.companyType});
                    this.timeout = new Date().getTime();
                } else {
                    objIds.splice(objIds.findIndex(item => item.companyId === obj.value && item.companyType === obj.companyType), 1);
                }
            },
            /**
             * 提交社区停止增删改操作
             */
            commitCommunity() {
                if (this.companyType === this.companyTypes.communityCompanyType) {
                    this.commitCommunityIds = {
                        companyType: this.companyTypes.communityCompanyType,
                        companyId: this.data[0].communityId
                    };
                }
                if (this.commitCommunityIds.length === 0) {
                    this.$message.error("请选择提交单位！");
                    return;
                }
                this.$confirm("是否确定提交更改？一经提交，将无法更改、删除。", "警告", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {
                    commonFunction.$ajax({
                        url: this.publicParams.chooseSubmitUrl,
                        method: "post",
                        headers: {
                            "X-CSRF-TOKEN": this.csrf.prop("content")
                        },
                        data: {
                            data: Base64.encodeURI(JSON.stringify(this.commitCommunityIds)),
                            changeType: this.dataType
                        }
                    }, result => {
                        if (result.state) {
                            this.$message.success(result.message);
                            setTimeout(() => {
                                location.reload();
                            }, 3000);
                        } else {
                            this.$message.error(result.message);
                        }
                    });
                });
            }
        },
        watch: {
            "$route"() {
                if (!this.searchParams.isFrist) {
                    this.loadData();
                }
            }
        }
    };
</script>
<style lang="scss" scoped>
    .resident-list-buttons {
        justify-content: flex-end;
        margin-bottom: 2rem;

        a, button {
            margin-right: 1.5rem;
        }

        .is-dropdown {
            margin-right: 0;
            border-bottom-right-radius: unset;
            border-top-right-radius: unset;
        }

        .dropdown-toggle {
            border-radius: unset;
        }

        .dropdown-menu {
            position: absolute;
            will-change: transform;
            top: 0;
            left: 0;
            transform: translate3d(0px, 35px, 0);
            padding: 0.4rem;
        }
    }

    .list-search {
        justify-content: space-between;

        div.form-group {
            margin: 0 0.5rem 0.8rem 0.5rem;

            label {
                margin-right: 0.5rem;
            }

            &:first-of-type {
                position: relative;

                input {
                    cursor: pointer;
                }

                button {
                    position: absolute;
                    right: 0;
                }
            }

            &:last-of-type {
                button {
                    margin: 0 0.4rem;
                }
            }
        }
    }

    nav {
        margin-top: 2rem;
    }

    .modal {
        .modal-dialog {
            .modal-content {
                .modal-body {
                    width: 85%;
                    margin: 0 auto;

                    .form-inline {
                        .form-group {
                            label, select {
                                margin-right: 1.6rem;
                            }
                        }
                    }
                }
            }
        }
    }
</style>
