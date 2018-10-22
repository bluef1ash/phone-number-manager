define("resident_list", ["commonFunction", "jquery", "bootstrap", "layui", "ztree"], function (commonFunction) {
    return function (url) {
        var _token = null;
        var isSuccess = {
            companyTree: false,
            subdistricts: false
        };
        var companyNameHidden = null;
        var companyName = null;
        $(function () {
            _token = $("input[name='_token']");
            var importAsSystemModal = $("#import_as_system_modal");
            companyNameHidden = $("input[name='companyName']");
            companyName = $("#company_name");
            if (companyNameHidden.val() != "") {
                companyName.val(decodeURI(companyNameHidden.val()));
            }
            /**
             * 查询
             */
            $("form[name='query_input']").submit(function () {
                if ($("input[name='communityResidentName']").val() == "" && $("input[name='communityResidentAddress']").val() == "" && $("input[name='communityResidentPhones']").val() == "") {
                    return false;
                }
            });
            /**
             * 选择单位模态框显示事件绑定
             */
            $("#search_company_modal").on("show.bs.modal", function (e) {
                if (!isSuccess.companyTree) {
                    $.fn.zTree.init($("#company_tree"), {
                        async: {
                            contentType: "application/x-www-form-urlencoded",
                            dataFilter: function (treeId, parentNode, responseData) {
                                _token.val(responseData._token);
                                if (responseData.treeMenus) {
                                    isSuccess.companyTree = true;
                                    return responseData.treeMenus;
                                }
                                layui.use("layer", function () {
                                    var layer = layui.layer;
                                    layer.msg(responseData.message, {icon: 5});
                                });
                            },
                            dataType: "json",
                            enable: true,
                            otherParam: {"_token": _token.val()},
                            type: "get",
                            url: url + "resident/ajax_select"
                        },
                        callback: {
                            onClick: function (event, treeId, treeNode, clickFlag) {
                                setCompany(treeNode);
                                $.fn.zTree.getZTreeObj("company_tree").expandNode(treeNode);
                            },
                            onDblClick: function (event, treeId, treeNode) {
                                setCompany(treeNode);
                                $("#search_company_modal").modal("hide");
                            }
                        },
                        view: {
                            selectedMulti: false,
                            dblClickExpand: false
                        }
                    }, null);
                }
            });
            /**
             * 上传Excel
             */
            layui.use(["layer", "upload"], function () {
                var layer = layui.layer;
                var upload = layui.upload;
                var layerIndex = null;
                var subdistrictId = $("#subdistrict_id");
                var uploadInst = upload.render({
                    elem: "#import_as_system_file",
                    url: url + "resident/import_as_system",
                    auto: false,
                    bindAction: "#confirm_upload",
                    accept: "file",
                    exts: "xls|xlsx",
                    data: {
                        subdistrictId: subdistrictId.val(),
                        _token: _token.val()
                    },
                    choose: function (obj) {
                        if (subdistrictId.val() == 0) {
                            layer.msg("请选择街道！", {icon: 5});
                            return;
                        }
                        var tipIndex = layer.open({
                            title: "友情提示",
                            content: "此操作会清空所选街道的所有数据！必须按照民政局下发的Excel表的格式上传，并且只能上传所选街道的数据，违反上述任意一条规则就会上传失败！",
                            btn: ["知道了，开始上传", "算了吧，放弃上传"],
                            yes: function (index, layero) {
                                importAsSystemModal.modal("hide");
                                layerIndex = layer.load(0, {shade: [0.3, "#000"]});
                                $("#confirm_upload").trigger("click");
                                layer.close(tipIndex);
                            }
                        })
                    },
                    done: function (res) {
                        //上传完毕回调
                        layer.close(layerIndex);
                        layer.msg("上传成功！等待3秒后自动刷新。", {icon: 6});
                        setTimeout(function () {
                            location.reload();
                        }, 3000);
                    },
                    error: function () {
                        //请求异常回调
                        layer.close(layerIndex);
                        layer.msg("上传失败！", {icon: 5});
                    }
                });
            });
            /**
             * 导入Excel模态框显示事件绑定
             */
            importAsSystemModal.on("show.bs.modal", function (e) {
                if (!isSuccess.subdistricts) {
                    $.ajax({
                        url: url + "subdistrict/ajax_load",
                        method: "get",
                        data: {
                            _token: _token.val()
                        },
                        success: function (data) {
                            if (data.state) {
                                isSuccess.subdistricts = true;
                                var options = "";
                                for (var i = 0; i < data.subdistricts.length; i++) {
                                    options += '<option value="' + data.subdistricts[i].subdistrictId + '">' + data.subdistricts[i].subdistrictName + "</option>";
                                }
                                $("#subdistrict_id").append(options);
                            } else {
                                layui.use("layer", function () {
                                    var layer = layui.layer;
                                    layer.msg(data.message, {icon: 5});
                                });
                            }
                            _token.val(data._token);
                        }
                    });
                }
            });
            $("[data-toggle='tooltip']").tooltip();
        });

        /**
         * 设置单位表单
         * @param treeNode
         */
        function setCompany(treeNode) {
            $("input[name='companyId']").val(treeNode.id);
            $("input[name='companyRid']").val(treeNode.roleLocationId);
            companyNameHidden.val(encodeURI(treeNode.name));
            companyName.val(treeNode.name);
        }
    }
});
