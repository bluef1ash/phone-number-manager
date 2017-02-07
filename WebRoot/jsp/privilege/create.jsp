<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>添加权限 - 权限管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/system/user_role/privilege/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right">权限名称</td>
						<td>
							<input type="text" name="privilegeName" class="form-control" placeholder="请输入权限名">
						</td>
					</tr>
					<tr>
						<td class="text-right">权限描述</td>
						<td>
							<input type="text" name="privilegeDescription" class="form-control" placeholder="请输入描述">
						</td>
					</tr>
					<tr>
						<td class="text-right">权限约束名称</td>
						<td>
							<input type="text" name="constraintAuth" class="form-control" placeholder="请输入权限约束名称">
						</td>
					</tr>
					<tr>
						<td class="text-right">访问地址</td>
						<td>
							<input type="text" name="uri" class="form-control" placeholder="请输入访问地址">
						</td>
					</tr>
					<tr>
						<td class="text-right">菜单显示图标</td>
						<td>
							<input type="text" name="iconName" class="form-control" placeholder="菜单显示图标">
						</td>
					</tr>
					<tr>
						<td class="text-right">是否在菜单栏中显示</td>
						<td>
							<label class="radio-inline">
								<input type="radio" name="isDisplay" value="1">是
							</label>
							<label class="radio-inline">
								<input type="radio" name="isDisplay" value="0" checked>否
							</label>
						</td>
					</tr>
					<tr>
						<td class="text-right">上级权限</td>
						<td>
							<select name="higherPrivilege" class="form-control">
								<option value="0">无</option>
								<c:forEach items="${userPrivileges}" var="uPrivilege">
									<option value="${uPrivilege.privilegeId}">${uPrivilege.privilegeName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="submissionToken" value="${submissionToken}">
							<input type="submit" value="添加" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>