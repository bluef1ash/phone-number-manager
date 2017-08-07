<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/layouts/header.jsp" />
		<title>添加社区 - 社区管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/community/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">社区名称</td>
						<td>
							<input type="text" name="communityName" class="form-control" placeholder="请输入社区名">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区联系方式</td>
						<td>
							<input type="text" name="communityTelephone" class="form-control" placeholder="请输入社区联系方式">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区需报送总人数</td>
						<td>
							<input type="text" name="actualNumber" class="form-control" placeholder="社区需报送总人数">
						</td>
					</tr>
					<tr>
						<td class="text-right">上级街道</td>
						<td>
							<select name="subdistrictId" class="form-control">
								<option value="0">请选择</option>
								<c:forEach items="${subdistricts}" var="subdistrict">
									<option value="${subdistrict.subdistrictId}">${subdistrict.subdistrictName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="submissionToken" value="${submissionToken}">
							<input type="hidden" name="_token" value="${CSRFToken}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="添加" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>