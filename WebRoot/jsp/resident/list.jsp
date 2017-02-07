<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>社区居民列表 - 社区居民管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">社区居民管理</a> > <a href="${pageContext.request.contextPath}/resident/list.action">社区居民列表</a>
		</div>
		<div>
			<a href="${pageContext.request.contextPath}/resident/create.action" class="btn btn-default float-right margin-br-10" role="button">添加社区居民</a>
			<a href="${pageContext.request.contextPath}/resident/save_as_excel.action" class="btn btn-default float-right margin-br-10" role="button">导出到Excel</a>
			<a href="${pageContext.request.contextPath}/resident/import_as_system.action" class="btn btn-default float-right margin-br-10" role="button">从Excel文件导入系统</a>
		</div>
		<form action="" class="form-horizontal" name="query_input" method="post">
			<table class="table table-bordered font-size-14">
				<thead>
					<tr class="query-input">
						<td class="form-group">
							<label class="col-sm-4 control-label">单位</label>
							<span class="col-sm-8">
								<input type="text" class="form-control" disabled="disabled">
								<button type="button" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
								<input type="hidden" name="unit">
							</span>
						</td>
						<td class="form-group">
							<label class="col-sm-4 control-label">社区居民姓名</label>
							<span class="col-sm-8"><input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名"></span>
						</td>
						<td class="form-group">
							<label class="col-sm-4 control-label">家庭住址</label>
							<span class="col-sm-8"><input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭住址"></span>
						</td>
						<td class="form-group" colspan="2">
							<label class="col-sm-4 control-label">联系方式</label>
							<span class="col-sm-8"><input type="text" name="communityResidentPhones" class="form-control" placeholder="请输入社区居民联系方式"></span>
						</td>
					</tr>
					<tr class="query-input">
						<td colspan="5">
							<input type="submit" value="查询" class="btn btn-primary">
						</td>
					</tr>
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
		</form>
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
				<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/resident/list.action?page=${pageInfo.getPages()}">&raquo;</a>
				</li>
			</ul>
		</div>
		<script type="text/javascript">
			$(function (){
				// 分页
				var pagination_ul = $("#pagination_parent").children("ul").css("width");
				pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
				$("#pagination_parent").css("width", pagination_ul);
				// 查询
				$("form[name='query_input']").submit(function(){
					if ($("input[name='communityResidentName']").val() == "" && $("input[name='communityResidentAddress']").val() == "" && $("input[name='communityResidentPhones'']").val() == "") {
						return false;
					}
				});
			});
		</script>
	</body>
</html>