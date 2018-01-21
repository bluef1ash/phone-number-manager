<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>社区列表 - 社区管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action" title="主页" ondragstart="return false;">主页</a> > <a href="javascript:;" title="社区管理" ondragstart="return false;">社区管理</a> > <a href="${pageContext.request.contextPath}/community/list.action" title="社区列表" ondragstart="return false;">社区列表</a>
		</div>
		<a href="${pageContext.request.contextPath}/community/create.action" class="btn btn-default float-right margin-br-10 menu-tab" role="button" ondragstart="return false;">添加社区</a>
		<table class="table table-bordered font-size-14">
			<thead></thead>
			<tbody>
				<tr>
					<th style="width: 10%">序号</th>
					<th>社区名称</th>
					<th>联系方式</th>
					<th>所属街道</th>
					<th style="width: 35%">操作</th>
				</tr>
				<c:forEach items="${communities}" var="community" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${community.communityName}</td>
						<td>${community.communityTelephone}</td>
						<td>${community.subdistrict.subdistrictName}</td>
						<td>
							<a href="${pageContext.request.contextPath}/community/edit.action?id=${community.communityId}" class="btn btn-default operation" role="button" ondragstart="return false;">修改</a>
							<a href="javascript:;" class="btn btn-default operation" role="button" onclick="commonFunction.deleteObject('${pageContext.request.contextPath}/community/ajax_delete.action', ${community.communityId}, '${_token}');" ondragstart="return false;">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div id="pagination_parent">
			<ul class="pagination">
				<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/community/list.action" title="第1页" ondragstart="return false;">&laquo;</a>
				</li>
				<c:choose>
					<c:when test="${pageInfo.getPages() gt 5}">
						<c:choose>
							<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
								<c:set var="i" value="${pageInfo.getPages() - 4}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/community/list.action?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:when test="${pageInfo.getPageNum() gt 3}">
								<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/community/list.action?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/community/list.action?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
									</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
							<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
								<a href="${pageContext.request.contextPath}/community/list.action?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${pageInfo.isIsLastPage() eq true}">
						<li class="disabled">
							<a href="javascript:;" title="最后一页" ondragstart="return false;">&raquo;</a>
						</li>
					</c:when>
					<c:otherwise>
						<li>
							<a href="${pageContext.request.contextPath}/community/list.action?page=${pageInfo.getPages()}" title="最后一页" ondragstart="return false;">&raquo;</a>
						</li>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
		<script type="text/javascript">
			require(["commonFunction", "jquery"], function (commonFunction) {
                window.commonFunction = commonFunction;
				$(function (){
					var pagination_ul = $("#pagination_parent").children("ul").css("width");
					pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
					$("#pagination_parent").css("width", pagination_ul);
				});
			});
		</script>
	</body>
</html>
