<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>修改街道 - 街道管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/subdistrict/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead>
					<c:if test="${messageErrors != null}">  
					    <c:forEach items="${messageErrors}" var="error">  
					        <span style="color:red">${error.defaultMessage}</span><br/>  
					    </c:forEach>
					</c:if>
				</thead>
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
							<input type="hidden" name="subdistrictId" value="${subdistrict.subdistrictId}">
							<input type="hidden" name="submissionToken" value="${submissionToken}">
							<input type="submit" value="保存" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>