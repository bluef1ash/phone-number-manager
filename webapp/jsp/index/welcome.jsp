<%@ page language="java" pageEncoding="utf-8"%>
<jsp:include page="/jsp/layouts/header.jsp" />
        <title>欢迎 - 后台管理系统</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/welcome.css" />
        <script type="text/javascript">
            var getComputedCountUrl = "${pageContext.request.contextPath}/index/getcomputedcount.action";
            var _token = "${_token}";
            require(["welcome"]);
        </script>
    </head>
    <body>
        <div class="container-fluid areas-content">
            <div class="row">
                <ul class="col-md-8 tip-panel">
                    <li style="background-color: #e35b5a;">
                        <ul>
                            <li><i class="fa fa-warning"></i></li>
                            <li>
                                <h2><b id="lack_number">0</b><span>条</span></h2>
                                <h5>缺少的条目</h5>
                            </li>
                        </ul>
                    </li>
                    <li style="background-color: #44b6ae;">
                        <ul>
                            <li><i class="fa fa-thumbs-o-up"></i></li>
                            <li>
                                <h2><b id="database_number">0</b><span>条</span></h2>
                                <h5>目前添加的总数</h5>
                            </li>
                        </ul>
                    </li>
                    <li style="background-color: #578ebe;">
                        <ul>
                            <li><i class="fa fa-clock-o"></i></li>
                            <li>
                                <h2><b id="actual_number">0</b><span>条</span></h2>
                                <h5>需要添加的总数</h5>
                            </li>
                        </ul>
                    </li>
                </ul>
                <div class="col-md-4">
                    <div class="panel panel-default pie-chart">
                        <div class="panel-heading"><i class="fa fa-pie-chart fa-lg" style="padding-right: 5px;"></i>数量统计</div>
                        <canvas id="pie_chart"></canvas>
                        <div class="panel-body">
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel panel-default bar-chart">
                <div class="panel-heading">
                    <i class="fa fa-bar-chart fa-lg" style="padding-right: 5px;"></i>社区统计
                </div>
                <div class="panel-body">
                    <canvas id="bar_chart" height="250px"></canvas>
                </div>
            </div>
            <div class="panel panel-default articles">
                <div class="panel-heading">
                    <i class="fa fa-rss fa-lg" style="padding-right: 5px;"></i>通知公告
                </div>
                <div class="panel-body">
                    <ul>
                        <li>
                            <a href="javascript:;" ondragstart="return false;">测试信息</a><span class="time">0000-00-00</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </body>
</html>
