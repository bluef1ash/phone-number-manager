<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>添加用户 - 用户管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/system/user_role/user/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right">用户名</td>
						<td>
							<input type="text" name="username" class="form-control" placeholder="请输入用户名">
						</td>
					</tr>
					<tr>
						<td class="text-right">密码</td>
						<td>
							<input type="password" name="password" class="form-control" placeholder="请输入密码">
						</td>
					</tr>
					<tr>
						<td class="text-right">确认密码</td>
						<td>
							<input type="password" name="password_sure" class="form-control"  placeholder="请再次输入密码">
						</td>
					</tr>
					<tr>
						<td class="text-right">用户角色</td>
						<td>
							<select name="roleId" class="form-control">
								<option value="0">请选择</option>
								<c:forEach items="${userRoles}" var="userRole">
									<option value="${userRole.roleId}">${userRole.roleName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="text-right">是否锁定用户</td>
						<td>
							<p class="radio">
								<label><input type="radio" name="isLocked" value="1">是</label>
								<label><input type="radio" name="isLocked" value="0" checked>否</label>
							</p>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="_token" value="${_token}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="添加" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>
