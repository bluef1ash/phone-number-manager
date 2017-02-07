<%@ page language="java" pageEncoding="utf-8"%>
<jsp:include page="/jsp/public/header.jsp" />
<title>社区居民联系电话管理系统</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/javascript/index.js"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini"
	style="overflow:hidden;">
	<div id="ajax-loader"
		style="cursor: progress; position: fixed; top: -50%; left: -50%; width: 200%; height: 200%; background: #fff; z-index: 10000; overflow: hidden;">
		<img src="${basePath}images/ajax-loader.gif"
			style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; margin: auto;" />
	</div>
	<div class="wrapper">
		<!--头部信息-->
		<header class="main-header">
			<a href="${pageContext.request.contextPath}" target="_blank"
				class="logo"> <span class="logo-mini">PM</span> <span
				class="logo-lg">电话管理系统</span>
			</a>
			<nav class="navbar navbar-static-top">
				<a class="sidebar-toggle"> <span class="sr-only">Toggle
						navigation</span>
				</a>
				<div class="navbar-custom-menu">
					<ul class="nav navbar-nav">
						<li class="dropdown messages-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-envelope-o "></i> <span class="label label-success"></span>
						</a></li>
						<li class="dropdown notifications-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-bell-o"></i> <span class="label label-warning"></span>
						</a></li>
						<li class="dropdown tasks-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <i
								class="fa fa-flag-o"></i> <span class="label label-danger"></span>
						</a></li>
						<li class="dropdown user user-menu"><a href="#"
							class="dropdown-toggle" data-toggle="dropdown"> <img
								src="${pageContext.request.contextPath}/images/user2-160x160.jpg"
								class="user-image" alt="User Image"> <span
								class="hidden-xs">${sessionScope.systemUser.username}</span>
						</a>
							<ul class="dropdown-menu pull-right">
								<li><a class="menuItem" data-id="userInfo"
									href="${pageContext.request.contextPath}/SystemManage/User/Info"><i
										class="fa fa-user"></i>个人信息</a></li>
								<li><a href="javascript:void();"><i
										class="fa fa-trash-o"></i>清空缓存</a></li>
								<li><a href="javascript:void();"><i
										class="fa fa-paint-brush"></i>皮肤设置</a></li>
								<li class="divider"></li>
								<li><a
									href="${pageContext.request.contextPath}/login/loginout.action"><i
										class="ace-icon fa fa-power-off"></i>安全退出</a></li>
							</ul></li>
					</ul>
				</div>
			</nav>
		</header>
		<!--左边导航-->
		<div class="main-sidebar">
			<div class="sidebar">
				<div class="user-panel">
					<div class="pull-left image">
						<img
							src="${pageContext.request.contextPath}/images/user2-160x160.jpg"
							class="img-circle" alt="User Image">
					</div>
					<div class="pull-left info">
						<p>${sessionScope.systemUser.username}</p>
						<a><i class="fa fa-circle text-success"></i>在线</a>
					</div>
				</div>
				<form action="#" method="get" class="sidebar-form">
					<div class="input-group">
						<input type="text" name="q" class="form-control"
							placeholder="搜索功能"> <span class="input-group-btn">
							<a class="btn btn-flat"><i class="fa fa-search"></i></a>
						</span>
					</div>
				</form>
				<ul class="sidebar-menu" id="sidebar-menu">
					<!--<li class="header">导航菜单</li>
					<li class="treeview active">
						<a href="javascript:void();">
							<i class="glyphicon glyphicon-earphone"></i><span>社区居民电话管理</span>
							<i class="fa fa-angle-left pull-right"></i>
						</a>
						<ul class="treeview-menu">
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/resident/list.action">
									<i class="glyphicon glyphicon-list-alt"></i>居民电话列表
								</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/resident/create.action">
									<i class="glyphicon glyphicon-plus"></i>添加居民信息
									</a>
							</li>
						</ul>
					</li>
					<li class="treeview">
						<a href="javascript:void();">
							<i class="glyphicon glyphicon-tree-deciduous"></i><span>社区管理</span>
							<i class="fa fa-angle-left pull-right"></i>
						</a>
						<ul class="treeview-menu">
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/community/list.action">
									<i class="glyphicon glyphicon-list-alt"></i>社区列表
								</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/community/create.action">
									<i class="glyphicon glyphicon-plus"></i>添加社区
									</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/community/edit.action">
									<i class="glyphicon glyphicon-saved"></i>编辑社区
								</a>
							</li>
						</ul>
					</li>
					<li class="treeview">
						<a href="javascript:void();">
							<i class="glyphicon glyphicon-briefcase"></i><span>街道管理</span>
							<i class="fa fa-angle-left pull-right"></i>
						</a>
						<ul class="treeview-menu">
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/subdistrict/list.action">
									<i class="glyphicon glyphicon-list-alt"></i>街道列表
								</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/subdistrict/create.action">
									<i class="glyphicon glyphicon-plus"></i>添加街道
									</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/subdistrict/edit.action">
									<i class="glyphicon glyphicon-saved"></i>编辑街道
								</a>
							</li>
						</ul>
					</li>
					<li class="treeview">
						<a href="javascript:void();">
							<i class="glyphicon glyphicon-heart"></i><span>用户相关管理</span>
							<i class="fa fa-angle-left pull-right"></i>
						</a>
						<ul class="treeview-menu">
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/system/user_role/user/list.action">
									<i class="glyphicon glyphicon-user"></i>用户管理
								</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/system/user_role/role/list.action">
									<i class="glyphicon glyphicon-eye-open"></i>角色管理
									</a>
							</li>
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/system/user_role/privilege/list.action">
									<i class="glyphicon glyphicon-lock"></i>权限管理
									</a>
							</li>
						</ul>
					</li>
					<li class="treeview">
						<a href="javascript:void();">
							<i class="glyphicon glyphicon-cog"></i><span>系统管理</span>
							<i class="fa fa-angle-left pull-right"></i>
						</a>
						<ul class="treeview-menu">
							<li>
								<a class="menuItem" href="${pageContext.request.contextPath}/system/setting.action">
									<i class="glyphicon glyphicon-wrench"></i>系统配置
								</a>
							</li>
						</ul>
					</li>-->
				</ul>
			</div>
		</div>
		<!--中间内容-->
		<div id="content-wrapper" class="content-wrapper">
			<div class="content-tabs">
				<button class="roll-nav roll-left tabLeft">
					<i class="fa fa-backward"></i>
				</button>
				<nav class="page-tabs menuTabs">
					<div class="page-tabs-content" style="margin-left: 0px;">
						<a href="javascript:;" class="menuTab active"
							data-id="${pageContext.request.contextPath}/index/welcome.action">欢迎首页</a>
						<a href="javascript:;" class="menuTab"
							data-id="${pageContext.request.contextPath}/index/about.action"
							style="padding-right: 15px;">平台介绍</a>
					</div>
				</nav>
				<button class="roll-nav roll-right tabRight">
					<i class="fa fa-forward" style="margin-left: 3px;"></i>
				</button>
				<div class="btn-group roll-nav roll-right">
					<button class="dropdown tabClose" data-toggle="dropdown">
						页签操作<i class="fa fa-caret-down" style="padding-left: 3px;"></i>
					</button>
					<ul class="dropdown-menu dropdown-menu-right">
						<li><a class="tabReload" href="javascript:void();">刷新当前</a></li>
						<li><a class="tabCloseCurrent" href="javascript:void();">关闭当前</a></li>
						<li><a class="tabCloseAll" href="javascript:void();">全部关闭</a></li>
						<li><a class="tabCloseOther" href="javascript:void();">除此之外全部关闭</a></li>
					</ul>
				</div>
				<button class="roll-nav roll-right fullscreen">
					<i class="fa fa-arrows-alt"></i>
				</button>
			</div>
			<div class="content-iframe" style="overflow: hidden;">
				<div class="mainContent" id="content-main"
					style="margin: 10px; margin-bottom: 0px; padding: 0;">
					<iframe class="LRADMS_iframe" width="100%" height="100%" src="${pageContext.request.contextPath}/index/welcome.action" data-id="${pageContext.request.contextPath}/index/welcome.action"></iframe>
					<iframe class="LRADMS_iframe" width="100%" height="100%"
						src="${pageContext.request.contextPath}/index/about.action"
						data-id="${pageContext.request.contextPath}/index/about.action" style="display: none"></iframe>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(function () {
			$(".menuItem").click(function () {
				$(".LRADMS_iframe").each(function () {
					var login_url = "${pageContext.request.contextPath}/jsp/login/login.jsp";
					if ($(this).prop("src") == login_url) {
						window.location.href = login_url;
					}
				});
			});
		});
	</script>
</body>
</html>
