<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/jsp/layouts/header.jsp" %>
<title>登录 - 社区居民联系电话管理系统</title>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/applications/login.js"></script>
</head>
<body class="denglu02">
	<div class="dl">
		<h2 class="biaoti">
			<a href="${pageContext.request.contextPath}/" title="社区居民联系方式报送系统">社区居民联系方式报送系统</a></h2>
		<div class="log">
			<dl class="xuzhi02">
				<dt class="xz">注意：</dt>
				<dd>本系统由于设计和编写时间有限，界面或者功能可能有些问题，如有发现问题请立即通知设计人员。谢谢！</dd>
			</dl>
			<form class="form-horizontal" role="form">
				<ul class="deng02">
					<li style="width:100%; height:60px;" class="form-group">
						<label for="username" class="col-md-3 control-label">用户名：</label>
						<span class="col-md-8">
							<input type="text" class="form-control" id="username" placeholder="请输入用户名">
						</span>
					</li>
					<li style="width:100%; height:60px;" class="form-group">
						<label for="password" class="col-md-3 control-label">密码：</label>
						<span class="col-md-8">
							<input type="password" class="form-control" id="password" placeholder="请输入密码">
						</span>
					</li>
					<li style="width:100%; height:60px;" class="form-group">
						<label for="captcha" class="col-md-3 control-label">验证码：</label>
						<span class="col-md-5">
							<input type="text" class="form-control" id="captcha" placeholder="请输入验证码">
						</span>
						<img src="${pageContext.request.contextPath}/login/captcha.action" alt="" class="col-md-3" id="captcha_img">
					</li>
					<li style="width:100%; height:60px;">
						<input type="hidden" name="_token" value="${_token}">
						<spring:htmlEscape defaultHtmlEscape="true" />
						<button type="button" class="btn btn-primary" id="logging">登录</button>
					</li>
				</ul>
			</form>
		</div>
	</div>
</body>
</html>
