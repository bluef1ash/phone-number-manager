<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title><c:choose><c:when test="${community.communityId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>社区 - 社区管理 - 社区居民联系电话管理系统</title>
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
		<form action="${pageContext.request.contextPath}/community/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">社区名称</td>
						<td>
							<input type="text" name="communityName" value="${community.communityName}" class="form-control" placeholder="请输入社区名">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区联系方式</td>
						<td>
							<input type="text" name="communityTelephone" value="${community.communityTelephone}" class="form-control" placeholder="请输入社区联系方式">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区需报送总人数</td>
						<td>
							<input type="text" name="actualNumber" value="${community.actualNumber}" class="form-control" placeholder="社区需报送总人数">
						</td>
					</tr>
					<tr>
						<td class="text-right">上级街道</td>
						<td>
							<select name="subdistrictId" class="form-control">
								<option value="0">请选择</option>
								<c:forEach items="${subdistricts}" var="subdistrict">
									<option value="${subdistrict.subdistrictId}"<c:if test="${community.subdistrict.subdistrictId eq subdistrict.subdistrictId}"> selected</c:if>>${subdistrict.subdistrictName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${community.communityId != null}">
                                <input type="hidden" name="communityId" value="${community.communityId}">
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
