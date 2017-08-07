<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/layouts/header.jsp" />
		<title>添加角色 - 角色管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/system/user_role/role/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">角色名称</td>
						<td>
							<input type="text" name="roleName" class="form-control" placeholder="请输入角色名">
						</td>
					</tr>
					<tr>
						<td class="text-right">角色描述</td>
						<td>
							<input type="text" name="roleDescription" class="form-control" placeholder="请输入描述">
						</td>
					</tr>
					<tr>
						<td class="text-right">上级角色</td>
						<td>
							<select name="higherRole" class="form-control">
								<option value="0">无</option>
								<c:forEach items="${userRoles}" var="uRole">
									<option value="${uRole.roleId}">${uRole.roleName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="text-right">角色权限</td>
						<td>
							<input type="hidden" name="privilegeIds" value="0">
							<c:forEach items="${userPrivileges}" var="userPrivilege">
								<p class="privilege-checkbox">
									<label class="checkbox-inline">
										<input type="checkbox" name="privilegeIds" value="${userPrivilege.privilegeId}">${userPrivilege.privilegeName}
									</label>
									<c:if test="${userPrivilege.subUserPrivileges != null}">
										(
										<c:forEach items="${userPrivilege.subUserPrivileges}" var="subUserPrivilege">
											<label class="checkbox-inline">
												<input type="checkbox" name="privilegeIds" value="${subUserPrivilege.privilegeId}" data-pid="${userPrivilege.privilegeId}">${subUserPrivilege.privilegeName}
											</label>
										</c:forEach>
										)
									</c:if>
								</p>
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="submissionToken" value="${submissionToken}">
							<input type="hidden" name="_token" value="${CSRFToken}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" name="submit" value="添加" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<script type="text/javascript">
			$(function () {
				$("input[name='privilegeIds']").change(function () {
					var privilege_id = $(this).val();
					var arr = [11];
					if ($(this).prop("checked") == true) {
						$("input[name='privilegeIds']").each(function (index, element) {
							if ($(element).data("pid") == privilege_id && arr.toString().indexOf($(element).val()) == -1) {
								$(element).attr({"checked": false, "disabled": true});
							}
						});
					} else {
						$("input[name='privilegeIds']").each(function (index, element) {
							if ($(element).data("pid") == privilege_id && arr.toString().indexOf($(element).val()) == -1) {
								$(element).attr("disabled", false);
							}
						});
					}
				});
			});
		</script>
	</body>
</html>