<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>添加社区居民 - 社区居民管理 - 社区居民联系电话管理系统</title>
        <script type="text/javascript">
            require(["check_resident_input"], function (check_resident_input) {check_resident_input();});
        </script>
	</head>
	<body>
		<form action="${pageContext.request.contextPath}/resident/handle.action" method="post" name="community_resident">
			<table class="table table-bordered font-size-14">
				<thead></thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">社区居民姓名</td>
						<td>
							<input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民家庭地址</td>
						<td>
							<input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭地址">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式一</td>
						<td>
							<input type="text" name="communityResidentPhone1" class="form-control" placeholder="请输入社区居民联系方式一">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式二</td>
						<td>
							<input type="text" name="communityResidentPhone2" class="form-control" placeholder="请输入社区居民联系方式二">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式三</td>
						<td>
							<input type="text" name="communityResidentPhone3" class="form-control" placeholder="请输入社区居民联系方式三">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区分包人</td>
						<td>
							<input type="text" name="communityResidentSubcontractor" class="form-control" placeholder="请输入社区居民联系方式三">
						</td>
					</tr>
					<tr>
						<td class="text-right">所属社区</td>
						<td>
							<select name="communityId" class="form-control">
								<option value="0">请选择</option>
								<c:forEach items="${communities}" var="community">
									<option value="${community.communityId}">${community.communityName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="_token" value="${_token}">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="添加" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</body>
</html>
