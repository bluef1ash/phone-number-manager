<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/jsp/layouts/header.jsp" %>
<title>社区居民列表 - 社区居民管理 - 社区居民联系电话管理系统</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content-search.css">
</head>
<body>
	<div class="content-title">
		您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">社区居民管理</a> > <a href="${pageContext.request.contextPath}/resident/list.action">社区居民列表</a>
	</div>
	<div class="resident-excel">
		<a href="${pageContext.request.contextPath}/resident/create.action" class="btn btn-default float-right margin-br-10 menu-tab" role="button">添加社区居民</a>
		<a href="${pageContext.request.contextPath}/resident/save_as_excel.action" class="btn btn-default float-right margin-br-10" role="button">导出到Excel</a>
        <a href="javascript:;" class="btn btn-default float-right margin-br-10" id="import_as_system" role="button">从Excel文件导入系统</a>
        <button id="confirm_upload" class="hidden"></button>
	</div>
	<form class="form-horizontal" role="form" action="${pageContext.request.contextPath}/resident/list.action" method="get">
		<div class="query-input">
			<div class="form-group">
				<label class="col-md-4 control-label">单位</label>
				<span class="col-md-8 search-company">
                    <input type="text" name="company" class="form-control" value="${company}" readonly="readonly" onclick="selectCompany();" style="background-color: #eee;cursor: pointer;">
					<button type="button" class="btn btn-default" onclick="selectCompany();"><span class="glyphicon glyphicon-search"></span></button>
                </span>
			</div>
			<div class="form-group">
				<label class="col-md-5 control-label">社区居民姓名</label>
				<span class="col-md-7"><input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名" value="${communityResident.communityResidentName}"></span>
			</div>
			<div class="form-group">
				<label class="col-md-4 control-label">家庭住址</label>
				<span class="col-md-8"><input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭住址" value="${communityResident.communityResidentAddress}"></span>
			</div>
			<div class="form-group">
				<label class="col-md-4 control-label">联系方式</label>
				<span class="col-md-8"><input type="text" name="communityResidentPhones" class="form-control" placeholder="请输入社区居民联系方式" value="${communityResident.communityResidentPhones}"></span>
			</div>
            <input type="hidden" name="_token" value="${_token}">
            <input type="submit" value="查询" class="btn btn-primary search-company-submit">
		</div>
	</form>
	<table class="table table-bordered font-size-14">
		<thead>
			<tr>
				<th>序号</th>
				<th>社区居民姓名</th>
				<th style="width: 30%">家庭住址</th>
				<th style="width: 25%">联系方式（多个方式以英文逗号分隔）</th>
                <th>所属社区居委会</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${communityResidents}" var="communityResident" varStatus="status">
				<tr>
					<td>${status.count}</td>
					<td>${communityResident.communityResidentName}</td>
					<td>${communityResident.communityResidentAddress}</td>
					<td>${communityResident.communityResidentPhones}</td>
					<td>${communityResident.community.communityName}</td>
					<td>
						<a href="${pageContext.request.contextPath}/resident/edit.action?id=${communityResident.communityResidentId}" class="btn btn-default operation" role="button">修改</a>
						<a href="javascript:;" class="btn btn-default operation delete-resident" onclick="commonFunction.deleteObject('${pageContext.request.contextPath}/resident/ajax_delete.action', ${communityResident.communityResidentId}, '${_token}')" role="button">删除</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div id="pagination_parent">
		<ul class="pagination">
			<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
				<a href="${pageContext.request.contextPath}/resident/list.action?${queryString}">&laquo;</a>
			</li>
			<c:choose>
				<c:when test="${pageInfo.getPages() gt 5}">
					<c:choose>
						<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
							<c:set var="i" value="${pageInfo.getPages() - 4}" />
							<c:forEach begin="1" end="5" varStatus="status">
								<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
									<a href="${pageContext.request.contextPath}/resident/list.action?page=${i}&${queryString}">${i}</a>
								</li>
								<c:set var="i" value="${i + 1}" />
							</c:forEach>
						</c:when>
						<c:when test="${pageInfo.getPageNum() gt 3}">
							<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
							<c:forEach begin="1" end="5" varStatus="status">
								<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
									<a href="${pageContext.request.contextPath}/resident/list.action?page=${i}&${queryString}">${i}</a>
								</li>
								<c:set var="i" value="${i + 1}" />
							</c:forEach>
						</c:when>
						<c:otherwise>
							<c:forEach begin="1" end="5" varStatus="status">
								<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
									<a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}&${queryString}">${status.count}</a>
								</li>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
						<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
							<a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}&${queryString}">${status.count}</a>
						</li>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
				<a href="${pageContext.request.contextPath}/resident/list.action?page=${pageInfo.getPages()}&${queryString}">&raquo;</a>
			</li>
            <li class="pagination-count">共有${count}条</li>
		</ul>
	</div>
	<!-- 单位模态框 -->
	<div class="modal fade" id="search_company_modal" tabindex="-1" role="dialog" aria-labelledby="search_company_modal_label">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="search_company_modal_label">选择所在区域</h4>
				</div>
				<div class="modal-body">
					<ul id="company_tree"></ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="search_closed_modal">确定</button>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
        var clickedCompany = null;
        require(["commonFunction", "jquery", "bootstrap", "layui"], function (commonFunction) {
            window.commonFunction = commonFunction;
            $(function () {
                // 分页
                var pagination_ul = $("#pagination_parent").children("ul").css("width");
                pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
                $("#pagination_parent").css("width", pagination_ul);
                // 查询
                $("form[name='query_input']").submit(function () {
                    if ($("input[name='communityResidentName']").val() == "" && $("input[name='communityResidentAddress']").val() == "" && $("input[name='communityResidentPhones']").val() == "") {
                        return false;
                    }
                });
                /**
                 * 关闭模态框
                 */
                $("#search_closed_modal").click(function () {
                    if (clickedCompany != null) {
                        $("input[name='company']").val(clickedCompany.name);
                        $("#search_company_modal").modal("hide");
                    }
                });
                /**
                 * 上传Excel
                 */
                layui.use(["layer", "upload"], function(){
                    var layer = layui.layer;
                    var upload = layui.upload;
                    var index = null;
                    //执行实例
                    var uploadInst = upload.render({
                        elem: "#import_as_system", //绑定元素
                        url: "${pageContext.request.contextPath}/resident/import_as_system.action", //上传接口
                        accept: "file",
                        exts: "xls|xlsx",
                        data: {
                            _token: "${_token}"
                        },
                        choose: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                            index = layer.load();
                        },
                        done: function(res){
                            //上传完毕回调
                            layer.close(index);
                            layer.msg("上传成功！", {icon: 6});
                            setTimeout(function () {
                                location.reload();
                            }, 1000);
                        },
                        error: function(){
                            //请求异常回调
                            layer.close(index);
                            layer.msg("上传失败！", {icon: 5});
                        }
                    });
                });
            });
        });
        /**
         * 选择社区居委会
         */
        function selectCompany() {
            $("#company_tree").html("");
            $.get("${pageContext.request.contextPath}/resident/ajax_select.action", {"_token": "${_token}"}, function (data) {
				if (data) {
                    layui.use("tree", function () {
                        layui.tree({
                            elem: "#company_tree", //传入元素选择器
                            nodes: data,
                            click: function (node) {
                                clickedCompany = node;
                            }
                        });
                    });
                }
            });
            $("#search_company_modal").modal("show");
        }
	</script>
</body>
</html>
