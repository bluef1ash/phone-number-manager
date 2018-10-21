<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title><c:choose><c:when test="${configuration.key == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>系统配置 - 系统管理 - 社区居民联系电话管理系统</title>
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
        <form action="${pageContext.request.contextPath}/system/configuration/handle" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">系统配置项关键字名称</td>
						<td>
                            <input type="text" name="key" value="${configuration.key}" class="form-control" placeholder="系统配置项关键字名称"<c:if test="${configuration.keyIsChanged eq 0}"> readonly</c:if>>
						</td>
					</tr>
					<tr>
						<td class="text-right">系统配置项值类别</td>
						<td>
                            <select name="type" class="form-control">
                                <option value="0">请选择</option>
                                <option value="1"<c:if test="${configuration.type eq 1}"> selected</c:if>>布尔类型</option>
                                <option value="2"<c:if test="${configuration.type eq 2}"> selected</c:if>>字符串类型</option>
                                <option value="3"<c:if test="${configuration.type eq 3}"> selected</c:if>>数值类型</option>
                                <option value="4"<c:if test="${configuration.type eq 4}"> selected</c:if>>系统用户</option>
                            </select>
						</td>
					</tr>
					<tr>
						<td class="text-right">系统配置项值</td>
						<td id="configuration_value"></td>
					</tr>
					<tr>
						<td class="text-right">系统配置项描述</td>
						<td>
                            <input type="text" name="description" value="${configuration.description}" class="form-control" placeholder="请输入系统配置项的描述信息">
						</td>
					</tr>
                    <c:if test="${configuration.key eq null}">
                        <tr>
                            <td class="text-right">系统配置项关键字名称是否允许更改</td>
                            <td>
                                <label class="radio-inline">
                                    <input type="radio" name="keyIsChanged" value="1">是
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="keyIsChanged" value="0">否
                                </label>
                            </td>
                        </tr>
                    </c:if>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${configuration.key != null}">
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
            require(["configuration"], function (configuration) {
                configuration("${pageContext.request.contextPath}/system/user_role/user/ajax_get", "${configuration.value}");
            });
        </script>
	</body>
</html>
