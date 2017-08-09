<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/layouts/header.jsp" />
		<title>社区居民列表 - 社区居民管理 - 社区居民联系电话管理系统</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content-search.css">
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">社区居民管理</a> > <a href="${pageContext.request.contextPath}/resident/list.action">社区居民列表</a>
		</div>
		<div class="resident-excel">
			<a href="${pageContext.request.contextPath}/resident/create.action" class="btn btn-default float-right margin-br-10" role="button">添加社区居民</a>
			<a href="${pageContext.request.contextPath}/resident/save_as_excel.action" class="btn btn-default float-right margin-br-10" role="button">导出到Excel</a>
			<a href="${pageContext.request.contextPath}/resident/import_as_system.action" class="btn btn-default float-right margin-br-10" role="button">从Excel文件导入系统</a>
		</div>
		<form class="form-horizontal" role="form">
			<div class="query-input">
				<div class="form-group">
					<label class="col-md-4 control-label">单位</label>
					<span class="col-md-8 search-company">
						<input type="text" name="search_company" class="form-control" disabled="disabled" data-toggle="modal" data-target="#search_company_modal">
						<button type="button" class="btn btn-default" data-toggle="modal" data-target="#search_company_modal"><span class="glyphicon glyphicon-search"></span></button>
						<input type="hidden" name="unit">
					</span>
				</div>
				<div class="form-group">
					<label class="col-md-5 control-label">社区居民姓名</label>
					<span class="col-md-7"><input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名"></span>
				</div>
				<div class="form-group">
					<label class="col-md-4 control-label">家庭住址</label>
					<span class="col-md-8"><input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭住址"></span>
				</div>
				<div class="form-group">
					<label class="col-md-4 control-label">联系方式</label>
					<span class="col-md-8"><input type="text" name="communityResidentPhones" class="form-control" placeholder="请输入社区居民联系方式"></span>
				</div>
				<div>
					<input type="submit" value="查询" class="btn btn-primary">
				</div>
			</div>
		</form>
		<table class="table table-bordered font-size-14">
			<thead>
				<tr>
					<th style="width: 5%">序号</th>
					<th style="width: 20%">社区居民姓名</th>
					<th style="width: 30%">家庭住址</th>
					<th style="width: 25%">联系方式（多个方式以英文逗号分隔）</th>
					<th style="width: 20%">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${communityResidents}" var="communityResident" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${communityResident.communityResidentName}</td>
						<td>${communityResident.communityResidentAddress}</td>
						<td>${communityResident.communityResidentPhones}</td>
						<td>
							<a href="${pageContext.request.contextPath}/resident/edit.action?id=${communityResident.communityResidentId}" class="btn btn-default operation" role="button">修改</a>
							<a href="javascript:;" class="btn btn-default operation" role="button" onclick="delete_object(this, ${communityResident.communityResidentId}, 'resident/ajax_delete.action', '社区居民');">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div id="pagination_parent">
			<ul class="pagination">
				<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/resident/list.action">&laquo;</a>
				</li>
				<c:choose>
					<c:when test="${pageInfo.getPages() gt 5}">
						<c:choose>
							<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
								<c:set var="i" value="${pageInfo.getPages() - 4}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/resident/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:when test="${pageInfo.getPageNum() gt 3}">
								<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/resident/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}">${status.count}</a>
									</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
							<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
								<a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}">${status.count}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${pageInfo.isIsLastPage() eq true}"> 
						<li class="disabled">
							<a href="javascript:;">&raquo;</a>
						</li>
					</c:when>
					<c:otherwise>
						<li>
							<a href="${pageContext.request.contextPath}/resident/list.action?page=${pageInfo.getPages()}">&raquo;</a>
						</li>
					</c:otherwise>
				</c:choose>
				<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/resident/list.action?page=${pageInfo.getPages()}">&raquo;</a>
				</li>
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
                        <button type="button" class="btn btn-primary">确定</button>
                    </div>
                </div>
            </div>
        </div>
		<script type="text/javascript">
			require(["jquery", "bootstrap", "layui"], function () {
				$(function (){
					// 分页
					var pagination_ul = $("#pagination_parent").children("ul").css("width");
					pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
					$("#pagination_parent").css("width", pagination_ul);
					// 查询
					$("form[name='query_input']").submit(function(){
						if ($("input[name='communityResidentName']").val() == "" && $("input[name='communityResidentAddress']").val() == "" && $("input[name='communityResidentPhones']").val() == "") {
							return false;
						}
					});
					layui.use(
                        layui.tree({
                            elem: '#company_tree' //传入元素选择器
                            ,nodes: [{ //节点
                                name: '父节点1'
                                ,children: [{
                                    name: '子节点11'
                                },{
                                    name: '子节点12'
                                }]
                            },{
                                name: '父节点2（可以点左侧箭头，也可以双击标题）'
                                ,children: [{
                                    name: '子节点21'
                                    ,children: [{
                                        name: '子节点211'
                                    }]
                                }]
                            }]
                        })
                    );
				});
			});
		</script>
	</body>
</html>