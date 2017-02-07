<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>权限列表 - 权限管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">用户管理</a> > <a href="${pageContext.request.contextPath}/system/user_role/role/list.action">角色列表</a>
		</div>
		<a href="${pageContext.request.contextPath}/system/user_role/privilege/create.action" class="btn btn-default float-right margin-br-10" role="button">添加权限</a>
		<table class="table table-bordered font-size-14">
			<thead></thead>
			<tbody>
				<tr>
					<th style="width: 10%">序号</th>
					<th style="width: 25%">权限名</th>
					<th style="width: 30%">权限描述</th>
					<th style="width: 35%">操作</th>
				</tr>
				<c:forEach items="${userPrivileges}" var="userPrivilege" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${userPrivilege.privilegeName}</td>
						<td>${userPrivilege.privilegeDescription}</td>
						<td>
							<a href="${pageContext.request.contextPath}/system/user_role/privilege/edit.action?id=${userPrivilege.privilegeId}" class="btn btn-default operation" role="button">修改</a>
							<a href="javascript:;" class="btn btn-default operation" role="button" onclick="delete_object(this, ${userPrivilege.privilegeId}, 'system/user_role/privilege/ajax_delete.action', '系统用户权限');">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div id="pagination_parent">
			<ul class="pagination">
				<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action">&laquo;</a>
				</li>
				<c:choose>
					<c:when test="${pageInfo.getPages() gt 5}">
						<c:choose>
							<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
								<c:set var="i" value="${pageInfo.getPages() - 4}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:when test="${pageInfo.getPageNum() gt 3}">
								<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action?page=${status.count}">${status.count}</a>
									</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
							<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
								<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action?page=${status.count}">${status.count}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/privilege/list.action?page=${pageInfo.getPages()}">&raquo;</a>
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