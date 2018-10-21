<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>用户列表 - 用户管理 - 社区居民联系电话管理系统</title>
	</head>
	<body>
		<div class="content-title">
            您的位置：<a href="${pageContext.request.contextPath}/index" title="主页">主页</a> >
            <a href="javascript:" title="用户管理">用户管理</a> >
            <a href="${pageContext.request.contextPath}/system/user_role/user/list" title="用户列表" ondragstart="return false;">用户列表</a>
		</div>
        <a href="${pageContext.request.contextPath}/system/user_role/user/create" class="btn btn-primary float-right margin-br-10 menu-tab" role="button" title="添加用户" ondragstart="return false;">添加用户</a>
		<table class="table table-bordered font-size-14">
			<thead>
                <tr>
                    <th style="width: 5%">序号</th>
                    <th style="width: 10%">用户名</th>
                    <th style="width: 15%">角色</th>
                    <th style="width: 15%">登录时间</th>
                    <th style="width: 15%">登录时IP地址</th>
                    <th style="width: 15%">状态</th>
                    <th style="width: 25%">操作</th>
                </tr>
			</thead>
			<tbody>
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
                                <c:when test="${systemUser.systemUserId eq 1}">
                                    正常
                                </c:when>
								<c:when test="${systemUser.isLocked eq 0}">
									<a href="javascript:" class="btn btn-success btn-sm" title="点击按钮更改为锁定状态" role="button" onclick="is_locked(this, ${systemUser.systemUserId});" ondragstart="return false;">正常</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:" class="btn btn-warning btn-sm" title="点击按钮更改为正常状态" role="button" onclick="is_locked(this, ${systemUser.systemUserId});" ondragstart="return false;">已锁定</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
                            <a href="${pageContext.request.contextPath}/system/user_role/user/edit?id=${systemUser.systemUserId}" class="btn btn-default btn-sm operation" role="button" title="修改此系统用户" ondragstart="return false;">修改</a>
                            <a href="javascript:" class="btn btn-danger btn-sm operation delete-resident" onclick="commonFunction.deleteObject('${pageContext.request.contextPath}/system/user_role/user/ajax_delete', ${systemUser.systemUserId}, '${_token}')" role="button" title="删除此系统用户" ondragstart="return false;">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot></tfoot>
		</table>
        <ul class="pagination">
            <li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
                <a href="${pageContext.request.contextPath}/system/user_role/user/list" title="第一页" ondragstart="return false;">&laquo;</a>
            </li>
            <c:choose>
                <c:when test="${pageInfo.getPages() gt 5}">
                    <c:choose>
                        <c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
                            <c:set var="i" value="${pageInfo.getPages() - 4}" />
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/user/list?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
                                </li>
                                <c:set var="i" value="${i + 1}" />
                            </c:forEach>
                        </c:when>
                        <c:when test="${pageInfo.getPageNum() gt 3}">
                            <c:set var="i" value="${pageInfo.getPageNum() - 2}" />
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/user/list?page=${i}" title="第${i}页" ondragstart="return false;">${i}</a>
                                </li>
                                <c:set var="i" value="${i + 1}" />
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach begin="1" end="5" varStatus="status">
                                <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                    <a href="${pageContext.request.contextPath}/system/user_role/user/list?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                                </li>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
                        <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                            <a href="${pageContext.request.contextPath}/system/user_role/user/list?page=${status.count}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                        </li>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${pageInfo.isIsLastPage() eq true}">
                    <li class="disabled">
                        <a href="javascript:" title="最后一页" ondragstart="return false;">&raquo;</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li>
                        <a href="${pageContext.request.contextPath}/system/user_role/user/list?page=${pageInfo.getPages()}" title="最后一页" ondragstart="return false;">&raquo;</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
		<script type="text/javascript">
            /**
             * 锁定与解锁系统用户
             * @param obj
             * @param systemUserId
             */
            function is_locked(obj, systemUserId) {
                if (obj != null) {
                    var this_obj = $(obj);
                    $.get("${pageContext.request.contextPath}/system/user_role/user/ajax_user_lock", {
                        "systemUserId": systemUserId,
                        "locked": locked,
                        "_token": "${_token}"
                    }, function(data) {
                        if(data) {
                            if (data.isLocked == 1) {
                                this_obj.removeClass("btn-success").addClass("btn-danger").html("已锁定");
                            } else {
                                this_obj.removeClass("btn-danger").addClass("btn-success").html("正常");
                            }
                        }
                    });
                }
            }
		</script>
	</body>
</html>
