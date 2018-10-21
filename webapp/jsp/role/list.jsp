<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>角色列表 - 角色管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action" title="主页" ondragstart="return false;">主页</a> > <a href="javascript:" title="用户管理" ondragstart="return false;">用户管理</a> > <a href="${pageContext.request.contextPath}/system/user_role/role/list.action" title="角色列表" ondragstart="return false;">角色列表</a>
		</div>
		<a href="${pageContext.request.contextPath}/system/user_role/role/create.action" class="btn btn-primary float-right margin-br-10 menu-tab" role="button" title="添加角色" ondragstart="return false;">添加角色</a>
		<table class="table table-bordered font-size-14">
			<thead>
                <tr>
                    <th style="width: 10%">序号</th>
                    <th style="width: 25%">角色名</th>
                    <th style="width: 20%">角色描述</th>
                    <th style="width: 20%">状态</th>
                    <th style="width: 25%">操作</th>
                </tr>
            </thead>
			<tbody>
				<c:forEach items="${userRoles}" var="role" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${role.roleName}</td>
						<td>${role.roleDescription}</td>
						<td>预留</td>
						<td>
							<a href="${pageContext.request.contextPath}/system/user_role/role/edit.action?id=${role.roleId}" class="btn btn-default btn-sm operation" role="button" title="修改" ondragstart="return false;">修改</a>
                            <a href="javascript:" class="btn btn-danger btn-sm operation delete-resident" onclick="commonFunction.deleteObject('${pageContext.request.contextPath}/system/user_role/role/ajax_delete.action', ${role.roleId}, '${_token}')" role="button" title="删除" ondragstart="return false;">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
        <ul class="pagination">
            <li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
                <a href="${pageContext.request.contextPath}/system/user_role/role/list.action" title="第一页" ondragstart="return false;">&laquo;</a>
            </li>
            <c:choose>
                <c:when test="${pageInfo.getPages() gt 5}">
                    <c:choose>
                        <c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
                            <c:set var="i" value="${pageInfo.getPages() - 4}" />
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
                                </li>
                                <c:set var="i" value="${i + 1}" />
                            </c:forEach>
                        </c:when>
                        <c:when test="${pageInfo.getPageNum() gt 3}">
                            <c:set var="i" value="${pageInfo.getPageNum() - 2}" />
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
                                </li>
                                <c:set var="i" value="${i + 1}" />
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                                </li>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
                        <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                            <a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                        </li>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${pageInfo.isIsLastPage() eq true}">
                    <li class="disabled">
                        <a href="javascript:" title="最后一页" ondragstart="return false;">&raquo;</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li>
                        <a href="${pageContext.request.contextPath}/system/user_role/role/list.action?page=${pageInfo.getPages()}" title="最后一页" ondragstart="return false;">&raquo;</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
	</body>
</html>
