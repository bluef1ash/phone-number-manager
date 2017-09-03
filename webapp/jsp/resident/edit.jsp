<%@ page language="java" pageEncoding="utf-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>修改社区居民 - 社区居民管理 - 社区居民联系电话管理系统</title>
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
		<form action="${pageContext.request.contextPath}/resident/handle.action" method="post">
			<table class="table table-bordered font-size-14">
				<thead>
				</thead>
				<tbody>
					<tr>
						<td class="text-right" style="width: 35%;">社区居民姓名</td>
						<td>
							<input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名" value="${communityResident.communityResidentName}">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民家庭地址</td>
						<td>
							<input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭地址" value="${communityResident.communityResidentAddress}">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式一</td>
						<td>
							<input type="text" name="communityResidentPhone1" class="form-control" placeholder="请输入社区居民联系方式一" value="${communityResident.communityResidentPhone1}">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式二</td>
						<td>
							<input type="text" name="communityResidentPhone2" class="form-control" placeholder="请输入社区居民联系方式二" value="${communityResident.communityResidentPhone2}">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区居民联系方式三</td>
						<td>
							<input type="text" name="communityResidentPhone3" class="form-control" placeholder="请输入社区居民联系方式三" value="${communityResident.communityResidentPhone3}">
						</td>
					</tr>
					<tr>
						<td class="text-right">社区分包人</td>
						<td>
							<input type="text" name="communityResidentSubcontractor" class="form-control" placeholder="请输入社区居民联系方式三" value="${communityResident.communityResidentSubcontractor}">
						</td>
					</tr>
					<tr>
						<td class="text-right">所属社区</td>
						<td>
							<select name="communityId" class="form-control" autocomplete="off">
								<option value="0">请选择</option>
								<c:forEach items="${communities}" var="community">
									<option value="${community.communityId}"<c:if test="${community.communityId == communityResident.communityId}"> selected</c:if>>${community.communityName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="text-center">
							<input type="hidden" name="communityResidentId" value="${communityResident.communityResidentId}">
							<input type="hidden" name="submissionToken" value="${submissionToken}">
							<input type="hidden" name="_token" value="${CSRFToken}">
							<input type="hidden" name="_method" value="PUT">
							<spring:htmlEscape defaultHtmlEscape="true" />
							<input type="submit" value="保存" class="btn btn-primary">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
        <script type="text/javascript">
            require(["check_resident_input", "bootstrap"], function (check_resident_input) {
                check_resident_input();

            });
        </script>
	</body>
</html>
