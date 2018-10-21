<%@ page pageEncoding="utf-8"%>
<%@ include file="/jsp/layouts/header.jsp" %>
		<title>社区居民联系方式管理系统</title>
        <meta name="_token" content="${_token}">
	</head>
	<body class="hold-transition skin-blue sidebar-mini">
		<div id="ajax_loader"><i></i></div>
		<div class="container-fluid container-custom">
			<!--头部信息-->
            <div class="main-header">
                <div class="row">
                    <a href="${pageContext.request.contextPath}" target="_blank" class="col-md-2 logo" title="社区居民联系方式管理系统" ondragstart="return false;">
                        <span class="logo-mini">PM</span><span class="logo-lg">居民管理系统</span>
                    </a>
                    <div class="col-md-10 navbar navbar-static-top">
                        <a href="javascript:" class="sidebar-toggle" title="缩小左侧导航栏" ondragstart="return false;">
                            <span class="sr-only">缩小左侧导航栏</span>
                        </a>
                        <div class="navbar-custom-menu">
                            <ul class="nav navbar-nav">
                                <li class="dropdown messages-menu">
                                    <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" title="短消息" ondragstart="return false;">
                                        <i class="fa fa-envelope-o"></i><span class="label label-success"></span>
                                    </a>
                                </li>
                                <li class="dropdown notifications-menu">
                                    <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" title="系统通知" ondragstart="return false;">
                                        <i class="fa fa-bell-o"></i><span class="label label-warning"></span>
                                    </a>
                                </li>
                                <li class="dropdown tasks-menu">
                                    <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" title="任务" ondragstart="return false;">
                                        <i class="fa fa-flag-o"></i> <span class="label label-danger"></span>
                                    </a>
                                </li>
                                <li class="dropdown user user-menu">
                                    <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" title="个人中心" ondragstart="return false;">
                                        <img src="${pageContext.request.contextPath}/images/user2-160x160.jpg" class="user-image" alt="User Image"> <span class="hidden-xs">${sessionScope.systemUser.username}</span>
                                    </a>
                                    <ul class="dropdown-menu pull-right">
                                        <li>
                                            <a class="menu-item" data-id="userInfo" href="${pageContext.request.contextPath}/system/user_role/user/edit.action?id=${systemUser.systemUserId}" title="个人信息" ondragstart="return false;">
                                                <i class="fa fa-user"></i>个人信息
                                            </a>
                                        </li>
                                        <li>
                                            <a href="javascript:" title="清空缓存" id="clean_cache" ondragstart="return false;">
                                                <i class="fa fa-trash-o"></i>清空缓存
                                            </a>
                                        </li>
                                        <li>
                                            <a href="javascript:" title="皮肤设置" ondragstart="return false;">
                                                <i class="fa fa-paint-brush"></i>皮肤设置
                                            </a>
                                        </li>
                                        <li class="divider"></li>
                                        <li>
                                            <a href="${pageContext.request.contextPath}/login/loginout.action" title="安全退出" ondragstart="return false;">
                                                <i class="ace-icon fa fa-power-off"></i>安全退出
                                            </a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="index-main">
                <div class="row">
                    <!--左边导航-->
                    <div class="col-md-2 sidebar">
                        <div class="user-panel">
                            <div class="pull-left image">
                                <img src="${pageContext.request.contextPath}/images/user2-160x160.jpg" class="img-circle" alt="系统用户头像">
                            </div>
                            <div class="pull-left info">
                                <p>${sessionScope.systemUser.username}</p>
                                <a href="javascript:" ondragstart="return false;"><i class="fa fa-circle text-success"></i>在线</a>
                            </div>
                        </div>
                        <form action="javascript:" method="get" class="sidebar-form">
                            <div class="input-group">
                                <input type="text" name="search_key" class="form-control" placeholder="搜索功能">
                                <a class="input-group-btn btn btn-flat" id="search_btn" title="点击搜索" ondragstart="return false;"><i class="fa fa-search"></i></a>
                            </div>
                        </form>
                        <ul class="sidebar-menu" id="sidebar-menu">
                        </ul>
                    </div>
                    <!--中间内容-->
                    <div id="content-wrapper" class="col-md-10 content-wrapper">
                        <div class="content-tabs">
                            <button class="roll-nav roll-left" id="tab_left">
                                <i class="fa fa-backward"></i>
                            </button>
                            <nav class="page-tabs menu-tabs">
                                <div class="page-tabs-content">
                                    <a href="javascript:" class="menu-tab active" data-id="${pageContext.request.contextPath}/index/welcome.action">欢迎首页</a>
                                    <a href="javascript:" class="menu-tab" data-id="${pageContext.request.contextPath}/index/about.action" style="padding-right: 15px;">平台介绍</a>
                                </div>
                            </nav>
                            <button class="roll-nav roll-right" id="tab_right">
                                <i class="fa fa-forward" style="margin-left: 3px;"></i>
                            </button>
                            <div class="btn-group roll-nav roll-right">
                                <button class="dropdown" id="tab_close" data-toggle="dropdown">
                                    页签操作<i class="fa fa-caret-down" style="padding-left: 3px;"></i>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-right">
                                    <li>
                                        <a href="javascript:" id="tab_reload" ondragstart="return false;">刷新当前</a>
                                    </li>
                                    <li>
                                        <a href="javascript:" id="tab_close_current" ondragstart="return false;">关闭当前</a>
                                    </li>
                                    <li>
                                        <a href="javascript:" id="tab_close_all" ondragstart="return false;">全部关闭</a>
                                    </li>
                                    <li>
                                        <a href="javascript:" id="tab_close_other" ondragstart="return false;">除此之外全部关闭</a>
                                    </li>
                                </ul>
                            </div>
                            <button class="roll-nav roll-right" id="fullscreen">
                                <i class="fa fa-arrows-alt"></i>
                            </button>
                        </div>
                        <div id="content_main">
                            <iframe class="lradms-iframe" width="100%" height="100%" src="${pageContext.request.contextPath}/index/welcome.action" data-id="${pageContext.request.contextPath}/index/welcome.action"></iframe>
                            <iframe class="lradms-iframe" width="100%" height="100%" src="${pageContext.request.contextPath}/index/about.action" data-id="${pageContext.request.contextPath}/index/about.action" style="display: none"></iframe>
                        </div>
                    </div>
                </div>
            </div>
            <div class="footer">
                Copyright © 社区居民联系方式管理系统 - Powered By 廿二月的天
            </div>
		</div>
		<script type="text/javascript">
			require(["learun"], function (learun) {
                /**
                 * 显示页面
                 */
                learun();
			});
		</script>
	</body>
</html>
