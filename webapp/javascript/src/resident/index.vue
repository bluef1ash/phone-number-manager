<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <div class="row resident-list-buttons">
                            <a href="javascript:" class="btn btn-warning" data-toggle="modal" data-target="#import_as_system_modal" title="从Excel文件导入系统" @click="loadSubdistricts">从Excel文件导入系统</a>
                            <a href="javascript:" class="btn btn-secondary" title="导出到Excel" @click="exportExcel">导出到Excel</a>
                            <a :href="publicParams.createUrl" class="btn btn-primary" title="添加社区居民">添加社区居民</a>
                        </div>
                        <div class="col-md-12">
                            <div class="row">
                                <form class="form-inline resident-list-search" action="javascript:" method="get">
                                    <div class="form-group">
                                        <label for="company_name">单位</label>
                                        <input class="form-control" id="company_name" type="text" readonly data-toggle="modal" data-target="#search_company_modal" v-model="companyName" @click="loadCompanies">
                                        <button type="button" class="btn btn-secondary resident-list-company-search" data-toggle="modal" data-target="#search_company_modal" @click="loadCompanies">
                                            <i class="fa fa-search"></i></button>
                                    </div>
                                    <div class="form-group">
                                        <label for="resident_name">社区居民姓名</label>
                                        <input class="form-control" id="resident_name" type="text" placeholder="请输入查找的社区居民姓名" v-model="residentName">
                                    </div>
                                    <div class="form-group">
                                        <label for="resident_address">家庭住址</label>
                                        <input class="form-control" id="resident_address" type="text" placeholder="请输入查找的社区居民家庭住址" v-model="residentAddress">
                                    </div>
                                    <div class="form-group">
                                        <label for="resident_phone">社区居民联系方式</label>
                                        <input class="form-control" id="resident_phone" type="text" placeholder="请输入查找的社区居民联系方式" v-model="residentPhone">
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
                        <el-table :data="communityResidents" border v-loading="isLoading">
                            <el-table-column type="index" :index="index => index + 1" label="序号"></el-table-column>
                            <el-table-column prop="communityResidentName" label="社区居民姓名"></el-table-column>
                            <el-table-column prop="communityResidentAddress" label="家庭住址"></el-table-column>
                            <el-table-column prop="communityResidentPhones" label="联系方式（多个联系方式以英文逗号分隔）"></el-table-column>
                            <el-table-column prop="community.communityName" label="所属社区居委会"></el-table-column>
                            <el-table-column label="操作">
                                <template slot-scope="scope">
                                    <div class="row">
                                        <div class="col-6">
                                            <a :href="publicParams.editUrl + scope.row.communityResidentId" class="btn btn-sm btn-block btn-pill btn-secondary" title="点击修改">修改</a>
                                        </div>
                                        <div class="col-6">
                                            <button class="btn btn-sm btn-block btn-pill btn-danger" type="button" title="点击删除" @click="deleteObject(scope.row.communityResidentId)">删除</button>
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
                                <el-upload class="upload-demo" :action="publicParams.importAsSystemUrl" :show-file-list="false" :data="{_token: token, subdistrictId: subdistrictId}" accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :auto-upload="false" ref="uploadExcel" :before-upload="beforeUploadExcel" :on-progress="uploadProgress" :on-success="uploadSuccess">
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
        <div class="modal fade" id="search_company_modal" tabindex="-1" role="dialog" aria-labelledby="search_company_modal_label" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title">选择所在区域</h4>
                        <button class="close" type="button" data-dismiss="modal" aria-label="关闭" @click="() => this.companyName = ''">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <el-tree ref="tree" :data="companies" node-key="id" :highlight-current="true" @node-click="choseCompany"></el-tree>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-dismiss="modal" @click="() => this.companyName = ''">关闭</button>
                        <button class="btn btn-primary" type="button" data-dismiss="modal">选择</button>
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
    import commonFunction from "@base/lib/common";

    export default {
        data() {
            return {
                token: token,
                publicParams: publicPrams,
                communityResidents: communityResidentData.communityResidents,
                pageInfo: communityResidentData.pageInfo,
                roleLocationIds: communityResidentData.roleLocationIds,
                companies: [],
                subdistricts: [],
                companyName: null,
                companyId: null,
                companyLocationId: null,
                residentName: null,
                residentAddress: null,
                residentPhone: null,
                subdistrictId: 0,
                loading: null,
                isLoading: false,
                pagination: 1,
                searchParams: {}
            };
        },
        created() {
            if (this.pagination !== null && this.pagination > 1) {
                this.loadResidents();
            }
        },
        methods: {
            /**
             * 删除社区居民
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
                        async: false,
                        data: {
                            _token: this.token
                        }
                    }).then(data => {
                        if (data._token) {
                            this.token = data._token;
                        }
                        if (data.state === 1) {
                            data.subdistricts.forEach(item => {
                                let node = {id: item.subdistrictId, label: item.subdistrictName, roleLocationId: this.roleLocationIds.subdistrictRoleId};
                                if (item.communities) {
                                    node.children = [];
                                    item.communities.forEach(community => {
                                        let children = {id: community.communityId, label: community.communityName, roleLocationId: this.roleLocationIds.communityRoleId};
                                        node.children.push(children);
                                    });
                                }
                                this.companies.push(node);
                            });
                        }
                    });
                }
            },
            /**
             * 选择单位
             * @param data
             * @param node
             * @param nodeObj
             */
            choseCompany(data, node, nodeObj) {
                this.companyId = data.id;
                this.companyLocationId = data.roleLocationId;
                this.companyName = data.label;
            },
            /**
             * 社区居民搜索
             */
            search() {
                let companyIdBool = this.companyId !== null && this.companyId !== 0;
                let companyLocationIdBool = this.companyLocationId !== null && this.companyLocationId !== 0;
                let residentNameBool = this.residentName !== null && this.residentName !== "";
                let residentAddressBool = this.residentAddress !== null && this.residentAddress !== "";
                let residentPhoneBool = this.residentPhone !== null && this.residentPhone !== "";
                if (companyIdBool || companyLocationIdBool || residentNameBool || residentAddressBool || residentPhoneBool) {
                    if (companyIdBool) {
                        this.searchParams.companyId = this.companyId;
                    }
                    if (companyLocationIdBool) {
                        this.searchParams.companyLocationId = this.companyLocationId;
                    }
                    if (residentNameBool) {
                        this.searchParams.residentName = this.residentName;
                    }
                    if (residentAddressBool) {
                        this.searchParams.residentAddress = this.residentAddress;
                    }
                    if (residentPhoneBool) {
                        this.searchParams.residentPhone = this.residentPhone;
                    }
                    this.searchParams.isSearch = true;
                    this.searchParams.isFrist = true;
                    this.pagination = 1;
                    this.$router.push({path: "/"});
                    this.loadResidents();
                }
            },
            /**
             * 社区居民搜索重置
             */
            searchClear() {
                this.companyName = null;
                this.companyId = null;
                this.companyLocationId = null;
                this.residentName = null;
                this.residentAddress = null;
                this.residentPhone = null;
                this.searchParams = {};
                this.pagination = 1;
                this.$router.push("/");
                this.loadResidents();
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
                            _token: this.token
                        }
                    }).then(data => {
                        if (data._token) {
                            this.token = data._token;
                        }
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
                this.loadResidents();
            },
            /**
             * 加载社区居民
             */
            loadResidents() {
                this.pagination = this.$route.path.substring(1) === "" ? 1 : parseInt(this.$route.path.substring(1));
                let data = {
                    page: this.pagination,
                    _token: this.token
                };
                if (Object.keys(this.searchParams).length > 0 && this.searchParams.isSearch) {
                    this.searchParams.isFrist = false;
                    data = Object.assign(data, this.searchParams);
                }
                $.ajax({
                    url: this.publicParams.loadResidentsUrl,
                    method: "get",
                    data: data,
                    beforeSend: xmlHttpRequest => {
                        this.isLoading = true;
                    }
                }).then(data => {
                    this.isLoading = false;
                    if (data.state === 1) {
                        this.communityResidents = data.communityResidents;
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
                this.$cookie.set("exportExcel", 1, {expires: "5m", path: "/"});
                let loading = this.$loading({
                    lock: true,
                    text: "正在生成Excel，请稍后。。。",
                    spinner: "el-icon-loading",
                    background: "rgba(0, 0, 0, 0.8)"
                });
                location.href = this.publicParams.downloadExcelUrl;
                let interval = setInterval(() => {
                    console.log(this.$cookie.get("exportExcel"));
                    if (this.$cookie.get("exportExcel") === null) {
                        loading.close();
                        this.$message({
                            message: "导出Excel文件完成！",
                            type: "success"
                        });
                        clearInterval(interval);
                    }
                }, 5000);
            }
        },
        watch: {
            "$route"() {
                if (!this.searchParams.isFrist) {
                    this.loadResidents();
                }
            }
        }
    };
</script>
<style lang="scss" scoped>
    .resident-list-buttons {
        justify-content: flex-end;
        margin-bottom: 2rem;

        a {
            margin-right: 1.5rem;
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
