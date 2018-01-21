<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>发生异常 - 异常处理 - 社区居民联系电话管理系统</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/error.css">
	</head>
	<body>
        <div class="container top">
            <div class="row">
                <h2 class="col-md-5 error-code">500</h2>
                <div class="col-md-7 error-message">
                    <h3>抱歉，出现错误了！</h3>
                    <div class="text">
                        无法访问本页面的原因是：<br>
                        ${exception.message}<br>
                        您可以<a href="javascript:history.go(-1);" title="返回">返回</a>上一页，或者再次<a href="javascript:location.reload();" title="刷新" ondragstart="return false;">刷新</a>本页面！<br>
                        管理员为系统给您带来的不便致以诚挚的歉意！
                    </div>
                </div>
            </div>
        </div>
        <div class="container bottom">
            <img src="${pageContext.request.contextPath}/images/try.png" alt="程序猿已在路上">
        </div>
	</body>
</html>
