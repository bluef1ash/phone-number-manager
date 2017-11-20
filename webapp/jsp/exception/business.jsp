<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>发生业务异常 - 异常处理 - 社区居民联系电话管理系统</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/error.css">
	</head>
	<body>
        <div>
            <h2></h2>
            <h3>${exception.message}</h3>
            <a href="javascript:;" class="btn btn-default reload" role="button">刷新</a>
        </div>
        <script type="text/javascript">
            require(["jquery"], function () {
                $(function () {
                    var bodyHeight = $("body").height();
                    var div = $("div");
                    var divHeight = div.height();
                    var marginTop = bodyHeight / 2 - divHeight / 2;
                    div.css("margin-top", marginTop + "px");
                    $("a").click(function () {
                        location.reload();
                    });
                });
            });
        </script>
	</body>
</html>
