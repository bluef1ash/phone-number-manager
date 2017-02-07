<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>角色列表 - 角色管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">用户管理</a> > <a href="${pageContext.request.contextPath}/system/user_role/role/list.action">角色列表</a>
		</div>
		<a href="${pageContext.request.contextPath}/system/user_role/role/create.action" class="btn btn-default float-right margin-br-10" role="button">添加角色</a>
		<table class="table table-bordered font-size-14">
			<thead></thead>
			<tbody>
				<tr>
					<th style="width: 10%">序号</th>
					<th style="width: 25%">角色名</th>
					<th style="width: 20%">角色描述</th>
					<th style="width: 20%">状态</th>
					<th style="width: 25%">操作</th>
				</tr>
				<c:forEach items="${userRoles}" var="role" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${role.roleName}</td>
						<td>${role.roleDescription}</td>
						<td>预留</td>
						<td>
							<a href="${pageContext.request.contextPath}/system/user_role/role/edit.action?id=${role.roleId}" class="btn btn-default operation" role="button">修改</a>
							<a href="javascript:;" class="btn btn-default operation" role="button" onclick="delete_object(this, ${role.roleId}, 'system/user_role/role/ajax_delete.action', '系统用户角色');">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div id="pagination_parent">
			<ul class="pagination">
				<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/role/list.action">&laquo;</a>
				</li>
				<c:choose>
					<c:when test="${pageInfo.getPages() gt 5}">
						<c:choose>
							<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
								<c:set var="i" value="${pageInfo.getPages() - 4}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:when test="${pageInfo.getPageNum() gt 3}">
								<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${status.count}">${status.count}</a>
									</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
							<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
								<a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${status.count}">${status.count}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${pageInfo.getPages()}">&raquo;</a>
				</li>
			</ul>
		</div>
		<script type="text/javascript">
			$(function (){
				var pagination_ul = $("#pagination_parent").children("ul").css("width");
				pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
				$("#pagination_parent").css("width", pagination_ul);
			});
		</script>
	</body>
</html>