<%@ page language="java" pageEncoding="utf-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
        <title><c:choose><c:when test="${communityResident.communityResidentId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>社区居民 - 社区居民管理 - 社区居民联系电话管理系统</title>
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
        <form action="${pageContext.request.contextPath}/resident/handle" method="post" id="resident" v-cloak @submit="submit">
			<table class="table table-bordered font-size-14">
				<thead>
				</thead>
				<tbody>
                    <tr :class="{'has-error': hasErrors[0]}">
						<td class="text-right" style="width: 35%;">社区居民姓名</td>
						<td>
                            <input type="text" name="communityResidentName" v-model="communityResidentName" class="form-control" placeholder="请输入社区居民姓名" value="${communityResident.communityResidentName}">
						</td>
					</tr>
                    <tr :class="{'has-error': hasErrors[1]}">
                        <td class="text-right">社区居民家庭地址</td>
                        <td>
                            <input type="text" name="communityResidentAddress" v-model="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭地址" value="${communityResident.communityResidentAddress}">
                        </td>
                    </tr>
                    <tr :class="{'has-error': hasErrors[2]}">
                        <td class="text-right">社区居民联系方式一</td>
                        <td>
                            <input type="text" name="communityResidentPhone1" v-model="communityResidentPhone1" class="form-control" placeholder="请输入社区居民联系方式一" value="${communityResident.communityResidentPhone1}">
						</td>
					</tr>
                    <tr :class="{'has-error': hasErrors[3]}">
						<td class="text-right">社区居民联系方式二</td>
						<td>
                            <input type="text" name="communityResidentPhone2" v-model="communityResidentPhone2" class="form-control" placeholder="请输入社区居民联系方式二" value="${communityResident.communityResidentPhone2}">
						</td>
					</tr>
                    <tr :class="{'has-error': hasErrors[4]}">
						<td class="text-right">社区居民联系方式三</td>
						<td>
                            <input type="text" name="communityResidentPhone3" v-model="communityResidentPhone3" class="form-control" placeholder="请输入社区居民联系方式三" value="${communityResident.communityResidentPhone3}">
						</td>
					</tr>
                    <tr :class="{'has-error': hasErrors[5]}">
                        <td class="text-right">所属街道</td>
						<td>
                            <select name="subdistrictId" class="form-control" v-model="subdistrictId" @change="chooseSubdistrict">
                                <option :value="0">请选择</option>
                                <option :value="subdistrict.subdistrictId" v-for="(subdistrict, index) in subdistricts" :key="subdistrict.subdistrictId">{{subdistrict.subdistrictName}}</option>
                            </select>
						</td>
					</tr>
                    <tr :class="{'has-error': hasErrors[6]}">
						<td class="text-right">所属社区</td>
						<td>
                            <select name="communityId" class="form-control" v-model="communityId" @change="chooseSubcontractor">
                                <option :value="0">请选择</option>
                                <option :value="community.communityId" v-for="(community, index) in newCommunities" :key="community.communityId">{{community.communityName}}</option>
                            </select>
                        </td>
                    </tr>
                    <tr :class="{'has-error': hasErrors[7]}">
                        <td class="text-right">社区分包人</td>
                        <td>
                            <select name="subcontractorId" class="form-control" v-model="subcontractorId">
                                <option :value="0">请选择</option>
                                <option :value="subcontractor.id" v-for="(subcontractor, index) in newSubcontractors" :key="subcontractor.id">{{subcontractor.name}}</option>
                            </select>
                        </td>
                    </tr>
					<tr>
						<td colspan="2" class="text-center">
                            <c:if test="${communityResident.communityResidentId != null}">
                                <input type="hidden" name="communityResidentId" value="${communityResident.communityResidentId}">
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
            require(["check_resident_input"], function(check_resident_input) {
                check_resident_input(${empty communityResident ? "null" : communityResident}, ${communities}, ${subcontractors})
            });
        </script>
	</body>
</html>
