<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title><c:choose><c:when test="${user.systemUserId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>用户 - 用户管理 - 社区居民联系电话管理系统</title>
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
        <form action="${pageContext.request.contextPath}/system/user_role/user/handle" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right">用户名</td>
						<td>
                            <input type="text" name="username" value="${user.username}" class="form-control" placeholder="请输入用户名"<c:if test="${user.systemUserId != null}"> readonly</c:if>>
						</td>
					</tr>
					<tr>
						<td class="text-right">密码</td>
						<td>
							<input type="password" name="password" class="form-control" placeholder="如果不修改密码，请留空！">
						</td>
					</tr>
					<tr>
						<td class="text-right">确认密码</td>
						<td>
							<input type="password" name="confirmPassword" class="form-control"  placeholder="如果不修改密码，请留空！">
						</td>
					</tr>
					<tr>
						<td class="text-right">用户角色</td>
						<td>
							<select name="roleId" class="form-control">
								<option value="0">请选择</option>
								<c:forEach items="${userRoles}" var="userRole">
									<option value="${userRole.roleId}"<c:if test="${user.userRole.roleId == userRole.roleId}"> selected</c:if>>${userRole.roleName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
                    <tr>
                        <td class="text-right">用户所属角色</td>
                        <td>
                            <select name="roleLocationId" class="form-control">
                                <option value="0">请选择</option>
                            </select>
                        </td>
                    </tr>
					<tr>
						<td class="text-right">是否锁定用户</td>
						<td>
							<p class="radio">
								<label><input type="radio" name="isLocked" value="1"<c:if test="${user.isLocked == 1}"> checked</c:if>>是</label>
								<label><input type="radio" name="isLocked" value="0"<c:if test="${user.isLocked == 0}"> checked</c:if>>否</label>
							</p>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${user.systemUserId != null}">
                                <input type="hidden" name="systemUserId" value="${user.systemUserId}">
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
            require(["jquery"], function () {
                $(function () {
                    var roleId = $("select[name='roleId']");
                    var roleLocationId = $("select[name='roleLocationId']");
                    getCompany(roleId, roleLocationId);
                    roleLocationId.val("${user.roleLocationId}");
                    roleId.change(function () {
                        roleLocationId.val(0);
                        roleLocationId.attr("disabled", false);
                        roleLocationId.children().first().nextAll().remove();
                        if ($(this).val() != 0 && $(this).val() != 1) {
                            getCompany($(this), roleLocationId);
                        } else if ($(this).val() == 1) {
                            roleLocationId.attr("disabled", true);
                        }
                    });
                });

                /**
                 * 获取单位
                 *
                 * @param obj
                 * @param roleLocationId
                 */
                function getCompany(obj, roleLocationId) {
                    $.ajax({
                        url: "${pageContext.request.contextPath}/system/user_role/user/ajax_get_company",
                        method: "get",
                        async: false,
                        data: {
                            "roleId": obj.val() + "-" + obj.find("option:selected").text(),
                            "_token": "${_token}"
                        },
                        success: function (data) {
                            if (data.length > 0) {
                                for (var i = 0; i < data.length; i++) {
                                    roleLocationId.append('<option value="' + data[i].value + '">' + data[i].name + '</option>');
                                }
                            }
                        }
                    });
                }
            });
        </script>
	</body>
</html>
