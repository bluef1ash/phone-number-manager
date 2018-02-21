<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title><c:choose><c:when test="${userRole.roleId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>角色 - 角色管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
        <c:if test="${messageErrors != null}">
            <c:forEach items="${messageErrors}" var="error">
                <div class="alert alert-danger alert-dismissable">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <span>${error.defaultMessage}</span>
                </div>
            </c:forEach>
        </c:if>
		<form action="${pageContext.request.contextPath}/system/user_role/role/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">角色名称</td>
						<td>
							<input type="text" name="roleName" value="${userRole.roleName}" class="form-control" placeholder="请输入角色名">
						</td>
					</tr>
					<tr>
						<td class="text-right">角色描述</td>
						<td>
							<input type="text" name="roleDescription" value="${userRole.roleDescription}" class="form-control" placeholder="请输入描述">
						</td>
					</tr>
					<tr>
						<td class="text-right">上级角色</td>
						<td>
							<select name="higherRole" class="form-control">
								<option value="0"<c:if test="${userRole.roleId == 0}"> selected</c:if>>无</option>
								<c:forEach items="${userRoles}" var="uRole">
									<c:if test="${uRole.roleId != userRole.roleId}">
										<option value="${uRole.roleId}"<c:if test="${userRole.higherRole == uRole.roleId}"> selected</c:if>>${uRole.roleName}</option>
									</c:if>
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
                            <c:if test="${userRole.roleId != null}">
                                <input type="hidden" name="roleId" value="${userRole.roleId}">
                                <input type="hidden" name="_method" value="PUT">
                            </c:if>
							<input type="hidden" name="_token" value="${_token}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="保存" class="btn btn-primary">
                            <button class="btn btn-default margin-l-10" role="button" onclick="history.go(-1)">返回</button>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<script type="text/javascript">
            require(["jquery", "bootstrap"], function () {
                $(function () {
                    var arr = [11];
                    var privilegeIds = $("input[name='privilegeIds']");
                    // 获取系统用户角色拥有的权限
                    $.get("${pageContext.request.contextPath}/system/user_role/privilege/ajax_get_privileges.action", {"roleId" : ${userRole.roleId}, "_token": "${_token}"}, function (data) {
                        if (data !== null) {
                            for (i = 0; i < data.privileges.length; i++) {
                                $("input[name='privilegeIds'][value='" + data.privileges[i].privilegeId + "']").attr("checked", true);
                            }
                            $("input[name='_token']").val(data._token);
                            // 打开页面时，选中已选中项
                            privilegeIds.each(function (index, element) {
                                var pid = $(element).data("pid");
                                if ($("input[name='privilegeIds'][value='" + pid + "']").prop("checked")) {
                                    $(element).attr({"checked": false, "disabled": true});
                                }
                            })
                        }
                    });
                    // 子权限启用与禁用
                    privilegeIds.change(function () {
                        var privilege_id = $(this).val();
                        if ($(this).prop("checked") == true) {
                            privilegeIds.each(function (index, element) {
                                if ($(element).data("pid") == privilege_id) {
                                    $(element).attr({"checked": false, "disabled": true});
                                }
                            });
                        } else {
                            $("input[name='privilegeIds']").each(function (index, element) {
                                if ($(element).data("pid") == privilege_id) {
                                    $(element).attr("disabled", false);
                                }
                            });
                        }
                    });
                });
            });
		</script>
	</body>
</html>
