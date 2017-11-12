<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title><c:choose><c:when test="${userPrivilege.privilegeId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>权限 - 权限管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
        <c:if test="${messageErrors != null}">
            <c:forEach items="${messageErrors}" var="error">
                <div class="alert alert-danger alert-dismissable">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <span>${error.defaultMessage}</span>
                </div>
            </c:forEach>
            <script type="text/javascript">
                require(["bootstrap"]);
            </script>
        </c:if>
		<form action="${pageContext.request.contextPath}/system/user_role/privilege/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right">权限名称</td>
						<td>
							<input type="text" name="privilegeName" class="form-control" value="${userPrivilege.privilegeName}" placeholder="请输入权限名">
						</td>
					</tr>
					<tr>
						<td class="text-right">权限约束名称</td>
						<td>
							<input type="text" name="constraintAuth" class="form-control" value="${userPrivilege.constraintAuth}" placeholder="请输入权限约束名称">
						</td>
					</tr>
					<tr>
						<td class="text-right">访问地址</td>
						<td>
							<input type="text" name="uri" class="form-control" value="${userPrivilege.uri}" placeholder="请输入访问地址">
						</td>
					</tr>
					<tr>
						<td class="text-right">菜单显示图标</td>
						<td>
							<input type="text" name="iconName" class="form-control" value="${userPrivilege.iconName}" placeholder="菜单显示图标">
						</td>
					</tr>
					<tr>
						<td class="text-right">是否在菜单栏中显示</td>
						<td>
							<c:choose>
								<c:when test="${userPrivilege.isDisplay == 0}">
									<label class="radio-inline">
										<input type="radio" name="isDisplay" value="1">是
									</label>
									<label class="radio-inline">
										<input type="radio" name="isDisplay" value="0" checked>否
									</label>
								</c:when>
								<c:otherwise>
									<label class="radio-inline">
										<input type="radio" name="isDisplay" value="1" checked>是
									</label>
									<label class="radio-inline">
										<input type="radio" name="isDisplay" value="0">否
									</label>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="text-right">上级权限</td>
						<td>
							<select name="higherPrivilege" class="form-control">
								<option value="0"<c:if test="${userPrivilege.privilegeId == 0}"> selected</c:if>>无</option>
								<c:forEach items="${userPrivileges}" var="uPrivilege">
									<c:if test="${uPrivilege.privilegeId != userPrivilege.privilegeId}">
										<option value="${uPrivilege.privilegeId}"<c:if test="${userPrivilege.higherPrivilege == uPrivilege.privilegeId}"> selected</c:if>>${uPrivilege.privilegeName}</option>
									</c:if>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${userPrivilege.privilegeId != null}">
                                <input type="hidden" name="privilegeId" value="${userPrivilege.privilegeId}">
                                <input type="hidden" name="_method" value="PUT">
                            </c:if>
							<input type="hidden" name="_token" value="${_token}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="保存" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>
