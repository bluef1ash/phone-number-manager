<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/jsp/public/header.jsp" />
		<title>用户列表 - 用户管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
			您的位置：<a href="${pageContext.request.contextPath}/index.action">主页</a> > <a href="javascript:;">用户管理</a> > <a href="${pageContext.request.contextPath}/system/user_role/user/list.action">用户列表</a>
		</div>
		<a href="${pageContext.request.contextPath}/system/user_role/user/create.action" class="btn btn-default float-right margin-br-10" role="button">添加用户</a>
		<table class="table table-bordered font-size-14">
			<thead>
			</thead>
			<tbody>
				<tr>
					<th style="width: 5%">序号</th>
					<th style="width: 10%">用户名</th>
					<th style="width: 15%">角色</th>
					<th style="width: 15%">登录时间</th>
					<th style="width: 15%">登录时IP地址</th>
					<th style="width: 15%">状态</th>
					<th style="width: 25%">操作</th>
				</tr>
				<c:forEach items="${systemUsers}" var="systemUser" varStatus="status">
					<tr>
						<td>${status.count}</td>
						<td>${systemUser.username}</td>
						<td>${systemUser.userRole.roleName}</td>
						<td>
							<c:choose>
								<c:when test="${systemUser.loginTime eq '1970-01-01 08:00:01.0'}">
									从未登录
								</c:when>
								<c:otherwise>
									${systemUser.loginTime}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${systemUser.loginIp eq ''}">
									从未登录
								</c:when>
								<c:otherwise>
									${systemUser.loginIp}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${systemUser.isLocked == 0}">
									<a href="javascript:;" class="btn btn-success" data-state="${systemUser.isLocked}" role="botton" onclick="is_locked(this);">正常</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:;" class="btn btn-danger" data-state="${systemUser.isLocked}" role="botton" onclick="is_locked(this);">已锁定</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/system/user_role/user/edit.action?id=${systemUser.systemUserId}" class="btn btn-default operation" role="button">修改</a>
							<a href="javascript:;" class="btn btn-default operation" role="button" onclick="delete_object(this, ${systemUser.systemUserId}, 'system/user_role/user/ajax_delete.action', '系统用户');">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot></tfoot>
		</table>
		<div id="pagination_parent">
			<ul class="pagination">
				<li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/user/list.action">&laquo;</a>
				</li>
				<c:choose>
					<c:when test="${pageInfo.getPages() gt 5}">
						<c:choose>
							<c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
								<c:set var="i" value="${pageInfo.getPages() - 4}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/user/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:when test="${pageInfo.getPageNum() gt 3}">
								<c:set var="i" value="${pageInfo.getPageNum() - 2}" />
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/user/list.action?page=${i}">${i}</a>
									</li>
									<c:set var="i" value="${i + 1}" />
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach begin="1" end="5" varStatus="status">
									<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
										<a href="${pageContext.request.contextPath}/system/user_role/user/list.action?page=${status.count}">${status.count}</a>
									</li>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
							<li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
								<a href="${pageContext.request.contextPath}/system/user_role/user/list.action?page=${status.count}">${status.count}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
					<a href="${pageContext.request.contextPath}/system/user_role/user/list.action?page=${pageInfo.getPages()}">&raquo;</a>
				</li>
			</ul>
		</div>
		<script type="text/javascript">
			$(function (){
				var pagination_ul = $("#pagination_parent").children("ul").css("width");
				pagination_ul = Math.ceil(pagination_ul.substr(0, pagination_ul.length - 2)) + "px";
				$("#pagination_parent").css("width", pagination_ul);
			});
			/**
			 * 锁定与解锁系统用户
			 * @param obj
			 */
			 function is_locked(obj) {
			 	if (obj != null) {
			 		var this_obj = $(obj);
			 		var locked = this_obj.data("state");
			 		$.get("${pageContext.request.contextPath}/system/user_role/user/ajax_user_lock.action", {"locked" : locked}, function(state) {
			 			if(state) {
			 				if (locked == 1) {
				 				this_obj.removeClass("btn-danger").addClass("btn-success").data("state", "0").html("正常");
				 			} else {
				 				this_obj.removeClass("btn-success").addClass("btn-danger").data("state", "1").html("已锁定");
				 			}
			 			}
			 		});
			 	}
			 }
		</script>
	</body>
</html>