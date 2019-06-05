<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <div class="row resident-list-buttons">
                            <a href="javascript:" class="btn btn-warning" data-toggle="modal" data-target="#import_as_system_modal" title="从Excel文件导入系统" @click="loadSubdistricts">从Excel文件导入系统</a>
                            <button @click="exportExcel" class="btn btn-secondary" type="button">导出到Excel</button>
                            <button aria-expanded="false" aria-haspopup="true" class="btn btn-secondary dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" id="export_excel" type="button">
                                <span class="sr-only">选择导出的单位</span>
                            </button>
                            <div aria-labelledby="export_excel" class="dropdown-menu" x-placement="bottom-start">
                                <el-tree :check-strictly="isStrictly" :data="companies2" :highlight-current="true" @check-change="chooseExportExcel" node-key="value" ref="tree" show-checkbox></el-tree>
                            </div>
                            <iframe :src="exportExcelUrl" class="hidden" v-if="isDownload"></iframe>
                            <a :href="publicParams.createUrl" :title="'添加社区' + dataTypeName" class="btn btn-primary" v-text="'添加社区' + dataTypeName"></a>
                        </div>
                        <div class="col-md-12 d-none d-md-block">
                            <div class="row">
                                <form class="form-inline resident-list-search" action="javascript:" method="get">
                                    <div class="form-group">
                                        <label>单位</label>
                                        <el-cascader :change-on-select="true" :clearable="true" :options="companies" @change="chooseCompany" placeholder="单位搜索"></el-cascader>
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
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.$index + 1" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="社区居民姓名" prop="communityResidentName" v-if="dataType === 0">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.communityResidentName" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="社区楼长姓名" prop="name" v-if="dataType === 1">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.name" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="社区楼长性别" prop="sexName" v-if="dataType === 1">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.sexName" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="家庭住址" prop="communityResidentAddress" v-if="dataType === 0">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.communityResidentAddress" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="年龄" prop="age" v-if="dataType === 1">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.age" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="联系方式（多个联系方式以英文逗号分隔）" prop="communityResidentPhones" v-if="dataType === 0">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.communityResidentPhones" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="联系方式（多个联系方式以英文逗号分隔）" prop="telephones" v-if="dataType === 1">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.telephones" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="所属社区居委会" prop="community.communityName">
                                <template slot-scope="scope">
                                    <element-table-popover :data="scope.row.community.communityName" :row="scope.row"></element-table-popover>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作">
                                <template slot-scope="scope">
                                    <div class="row">
                                        <div class="col-6">
                                            <a :href="publicParams.editUrl + scope.row.communityResidentId" class="btn btn-sm btn-block btn-pill btn-secondary" title="点击修改" v-if="dataType === 0">修改</a>
                                            <a :href="publicParams.editUrl + scope.row.id" class="btn btn-sm btn-block btn-pill btn-secondary" title="点击修改" v-if="dataType === 1">修改</a>
                                        </div>
                                        <div class="col-6">
                                            <button @click="deleteObject(scope.row.communityResidentId)" class="btn btn-sm btn-block btn-pill btn-danger" title="点击删除" type="button" v-if="dataType === 0">删除</button>
                                            <button @click="deleteObject(scope.row.id)" class="btn btn-sm btn-block btn-pill btn-danger" title="点击删除" type="button" v-if="dataType === 1">删除</button>
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
        <div class="modal fade" id="import_as_system_modal" tabindex="-1" role="dialog" aria-labelledby="import_as_system_modal_label" aria-hidden="true">
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
                                    <option :value="subdistrict.subdistrictId" v-for="subdistrict in subdistricts" v-text="subdistrict.subdistrictName"></option>
                                </select>
                                <el-upload :action="publicParams.importAsSystemUrl" :auto-upload="false" :before-upload="beforeUploadExcel" :data="{_csrf: csrf, subdistrictId: subdistrictId}" :on-error="uploadError" :on-progress="uploadProgress" :on-success="uploadSuccess" :show-file-list="false" accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" class="upload-demo" ref="uploadExcel">
                                    <el-button size="small" type="primary">浏览文件</el-button>
                                </el-upload>
                            </div>
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
    </div>
</template>
<script>
    import commonFunction from "@base/lib/javascript/common";
    import {Base64} from "js-base64";
    import elementTablePopover from "@baseSrc/javascript/component/element-table-popover";

    export default {
        data() {
            return {
                csrf: csrf,
                publicParams: publicPrams,
                data: data.data,
                dataType: data.dataType,
                dataTypeName: null,
                pageInfo: data.pageInfo,
                roleIds: data.roleIds,
                roleId: data.roleId,
                companies: [],
                companies2: [],
                subdistricts: [],
                subdistrictId: 0,
                loading: null,
                isLoading: false,
                pagination: 1,
                searchParams: {
                    sex: -1
                },
                searchCompanyName: null,
                isDownload: false,
                timeout: new Date().getTime(),
                exportIds: [],
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
                    $.ajax({
                        url: this.publicParams.companySelectUrl,
                        method: "get",
                        data: {
                            _csrf: this.csrf
                        }
                    }).then(data => {
                        if (data.state === 1) {
                            let disabled = false;
                            this.isStrictly = false;
                            if (this.roleId === this.roleIds.communityRoleId) {
                                disabled = true;
                                this.isStrictly = true;
                            }
                            data.subdistricts.forEach(item => {
                                let node = {value: item.subdistrictId, label: item.subdistrictName, roleId: this.roleIds.subdistrictRoleId};
                                let node2 = {value: item.subdistrictId, label: item.subdistrictName, roleId: this.roleIds.subdistrictRoleId, disabled};
                                if (item.communities) {
                                    node.children = [];
                                    node2.children = [];
                                    item.communities.forEach(community => {
                                        let children = {value: community.communityId, label: community.communityName, roleId: this.roleIds.communityRoleId};
                                        node.children.push(children);
                                        node2.children.push(children);
                                    });
                                }
                                this.companies.push(node);
                                this.companies2.push(node2);
                            });
                        }
                    });
                }
            },
            /**
             * 选择单位
             * @param data
             */
            chooseCompany(data) {
                if (data.length === 1) {
                    this.searchParams.companyId = data[0];
                    this.searchParams.companyRoleId = this.roleIds.subdistrictRoleId;
                } else {
                    this.searchParams.companyId = data[1];
                    this.searchParams.companyRoleId = this.roleIds.communityRoleId;
                }
            },
            /**
             * 搜索
             */
            search() {
                this.searchParams.isSearch = true;
                this.searchParams.isFrist = true;
                this.pagination = 1;
                this.$router.push({path: "/"});
                this.loadData();
            },
            /**
             * 搜索重置
             */
            searchClear() {
                this.searchParams = {
                    sex: -1
                };
                this.pagination = 1;
                this.$router.push("/");
                this.loadData();
            },
            /**
             * 加载街道
             */
            loadSubdistricts() {
                if (this.subdistricts.length === 0) {
                    $.ajax({
                        url: this.publicParams.loadSubdistrictsUrl,
                        method: "get",
                        data: {
                            _csrf: this.csrf
                        }
                    }).then(data => {
                        if (data.state === 1) {
                            this.subdistricts = data.subdistricts;
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
                    this.$message({
                        message: response.message,
                        type: "error"
                    });
                    return;
                }
                this.$message({
                    message: response.message,
                    type: "success"
                });
                this.loadData();
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
                    page: this.pagination,
                    _csrf: this.csrf
                };
                if (Object.keys(this.searchParams).length > 0 && this.searchParams.isSearch) {
                    this.searchParams.isFrist = false;
                    data = Object.assign(data, this.searchParams);
                }
                $.ajax({
                    url: this.publicParams.loadDataUrl,
                    method: "get",
                    data: data,
                    beforeSend: xmlHttpRequest => {
                        this.isLoading = true;
                    }
                }).then(data => {
                    this.isLoading = false;
                    if (data.state === 1) {
                        this.data = data.data;
                        this.pageInfo = data.pageInfo;
                    } else {
                        this.$message({
                            message: data.messageErrors[0].defaultMessage,
                            type: "error"
                        });
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
                if (this.exportIds.length === 0) {
                    this.$message({
                        message: "请选择导出单位！",
                        type: "error"
                    });
                    return;
                }
                this.$cookie.set("exportExcel", "wait", {expires: "5m"});
                this.loading = this.$loading({
                    lock: true,
                    text: "正在生成Excel，请稍后。。。",
                    spinner: "el-icon-loading",
                    background: "rgba(0, 0, 0, 0.8)"
                });
                this.exportExcelUrl = this.publicParams.downloadExcelUrl + "?data=" + Base64.encodeURI(JSON.stringify(this.exportIds));
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
                if (isChecked) {
                    new Date().getTime() - this.timeout > 5 && this.exportIds.push({roleLocationId: obj.value, roleId: obj.roleId});
                    this.timeout = new Date().getTime();
                } else {
                    this.exportIds.splice(this.exportIds.findIndex(item => item.roleLocationId === obj.value && item.roleId === obj.roleId), 1);
                }
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

        button {
            &:first-of-type {
                margin-right: 0;
                border-bottom-right-radius: unset;
                border-top-right-radius: unset;
            }

            &:last-of-type {
                border-top-left-radius: unset;
                border-bottom-left-radius: unset;
            }
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

    .resident-list-search {
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
</style>
