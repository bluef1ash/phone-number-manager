<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="/jsp/layouts/header.jsp" />
		<title><c:choose><c:when test="${subdistrict.subdistrictId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>街道 - 街道管理 - 社区居民联系电话管理系统</title>
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
		<form action="${pageContext.request.contextPath}/subdistrict/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">街道名称</td>
						<td>
							<input type="text" name="subdistrictName" value="${subdistrict.subdistrictName}" class="form-control" placeholder="请输入街道名">
						</td>
					</tr>
					<tr>
						<td class="text-right">街道联系方式</td>
						<td>
							<input type="text" name="subdistrictTelephone" value="${subdistrict.subdistrictTelephone}" class="form-control" placeholder="请输入街道联系方式">
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${subdistrict.subdistrictId != null}">
                                <input type="hidden" name="subdistrictId" value="${subdistrict.subdistrictId}">
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
