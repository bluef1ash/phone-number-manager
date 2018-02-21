<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/jsp/layouts/header.jsp" %>
        <title>社区居民列表 - 社区居民管理 - 社区居民联系电话管理系统</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/content-search.css">
    </head>
    <body>
        <div class="content-title">
            您的位置：<a href="${pageContext.request.contextPath}/index.action" title="主页" ondragstart="return false;">主页</a> > <a href="javascript:;" title="社区居民管理" ondragstart="return false;">社区居民管理</a> > <a href="${pageContext.request.contextPath}/resident/list.action" title="社区居民列表" ondragstart="return false;">社区居民列表</a>
        </div>
        <div class="resident-excel">
            <a href="" class="btn btn-warning margin-br-10" data-toggle="modal" data-target="#import_as_system_modal" role="button" title="从Excel文件导入系统" ondragstart="return false;">从Excel文件导入系统</a>

            <a href="${pageContext.request.contextPath}/resident/save_as_excel.action" class="btn btn-default margin-br-10" role="button" title="导出到Excel" ondragstart="return false;">导出到Excel</a>
            <a href="${pageContext.request.contextPath}/resident/create.action" class="btn btn-primary margin-br-10 menu-tab" role="button" title="添加社区居民" ondragstart="return false;">添加社区居民</a>
        </div>
        <div class="query-input">
            <div class="row">
                <form action="${pageContext.request.contextPath}/resident/list.action" method="get" name="resident_search" class="form-horizontal" role="form">
                    <div class="col-md-2 form-group">
                        <label class="col-md-4 control-label" for="company_name">单位</label>
                        <div class="col-md-8 search-company">
                            <input type="hidden" name="companyId" value="${companyId}">
                            <input type="hidden" name="companyRid" value="${companyRid}">
                            <input type="hidden" name="companyName" value="${companyName}">
                            <input type="text" id="company_name" class="form-control" readonly="readonly"  data-toggle="modal" data-target="#search_company_modal">
                            <button type="button" class="btn btn-default" data-toggle="modal" data-target="#search_company_modal"><span class="glyphicon glyphicon-search"></span></button>
                        </div>
                    </div>
                    <div class="col-md-3 form-group">
                        <label class="col-md-5 control-label">社区居民姓名</label>
                        <span class="col-md-7"><input type="text" name="communityResidentName" class="form-control" placeholder="请输入社区居民姓名" value="${communityResident.communityResidentName}"></span>
                    </div>
                    <div class="col-md-3 form-group">
                        <label class="col-md-4 control-label">家庭住址</label>
                        <span class="col-md-8"><input type="text" name="communityResidentAddress" class="form-control" placeholder="请输入社区居民家庭住址" value="${communityResident.communityResidentAddress}"></span>
                    </div>
                    <div class="col-md-3 form-group">
                        <label class="col-md-4 control-label">联系方式</label>
                        <span class="col-md-8"><input type="text" name="communityResidentPhones" class="form-control" placeholder="请输入社区居民联系方式" value="${communityResident.communityResidentPhones}"></span>
                    </div>
                    <div class="col-md-1">
                        <input type="hidden" name="_token" value="${_token}">
                        <input type="submit" value="查询" class="btn btn-primary search-company-submit">
                    </div>
                </form>
            </div>
        </div>
        <table class="table table-bordered font-size-14">
            <thead>
                <tr>
                    <th>序号</th>
                    <th>社区居民姓名</th>
                    <th style="width: 30%">家庭住址</th>
                    <th style="width: 25%">联系方式（多个联系方式以英文逗号分隔）</th>
                    <th>所属社区居委会</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${communityResidents}" var="communityResident" varStatus="status">
                    <tr data-toggle="tooltip" title="修改时间：<fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${communityResident.communityResidentEditTime}" />">
                        <td>${status.count}</td>
                        <td>${communityResident.communityResidentName}</td>
                        <td>${communityResident.communityResidentAddress}</td>
                        <td>${communityResident.communityResidentPhones}</td>
                        <td>${communityResident.community.communityName}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/resident/edit.action?id=${communityResident.communityResidentId}" class="btn btn-default btn-sm operation" role="button" title="修改此记录" ondragstart="return false;">修改</a>
                            <a href="javascript:;" class="btn btn-danger btn-sm operation delete-resident" onclick="commonFunction.deleteObject('${pageContext.request.contextPath}/resident/ajax_delete.action', ${communityResident.communityResidentId}, '${_token}')" role="button" title="删除此记录" ondragstart="return false;">删除</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div id="pagination_parent">
            <ul class="pagination">
                <li<c:if test="${pageInfo.isIsFirstPage() eq true}"> class="disabled"</c:if>>
                    <a href="${pageContext.request.contextPath}/resident/list.action?${queryString}" title="第一页" ondragstart="return false;">&laquo;</a>
                </li>
                <c:choose>
                    <c:when test="${pageInfo.getPages() gt 5}">
                        <c:choose>
                            <c:when test="${pageInfo.getPageNum() + 2 gt pageInfo.getPages()}">
                                <c:set var="i" value="${pageInfo.getPages() - 4}" />
                                <c:forEach begin="1" end="5" varStatus="status">
                                    <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                        <a href="${pageContext.request.contextPath}/resident/list.action?page=${i}&${queryString}" title="第${i}页" ondragstart="return false;">${i}</a>
                                    </li>
                                    <c:set var="i" value="${i + 1}" />
                                </c:forEach>
                            </c:when>
                            <c:when test="${pageInfo.getPageNum() gt 3}">
                                <c:set var="i" value="${pageInfo.getPageNum() - 2}" />
                                <c:forEach begin="1" end="5" varStatus="status">
                                    <li<c:if test="${i eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                        <a href="${pageContext.request.contextPath}/resident/list.action?page=${i}&${queryString}" title="第${i}页" ondragstart="return false;">${i}</a>
                                    </li>
                                    <c:set var="i" value="${i + 1}" />
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <c:forEach begin="1" end="5" varStatus="status">
                                    <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                        <a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}&${queryString}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                                    </li>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <c:forEach begin="1" end="${pageInfo.getPages()}" varStatus="status">
                            <li<c:if test="${status.count eq pageInfo.getPageNum()}"> class="active"</c:if>>
                                <a href="${pageContext.request.contextPath}/resident/list.action?page=${status.count}&${queryString}" title="第${status.count}页" ondragstart="return false;">${status.count}</a>
                            </li>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <li<c:if test="${pageInfo.isIsLastPage() eq true}"> class="disabled"</c:if>>
                    <a href="${pageContext.request.contextPath}/resident/list.action?page=${pageInfo.getPages()}&${queryString}" title="最后一页" ondragstart="return false;">&raquo;</a>
                </li>
                <li class="pagination-count">共有${count}条</li>
            </ul>
        </div>
        <!-- 单位模态框 -->
        <div class="modal fade bs-example-modal-sm" id="search_company_modal" tabindex="-1" role="dialog" aria-labelledby="search_company_modal_label">
            <div class="modal-dialog modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="search_company_modal_label">选择所在区域</h4>
                    </div>
                    <div class="modal-body">
                        <ul class="ztree" id="company_tree"></ul>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- 导入Excel模态框 -->
        <div class="modal fade bs-example-modal-sm" id="import_as_system_modal" tabindex="-1" role="dialog" aria-labelledby="import_as_system_modal_label">
            <div class="modal-dialog modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="import_as_system_modal_label">选择导入的街道</h4>
                    </div>
                    <div class="modal-body">
                        <form class="form-horizontal" action="#" method="get" onsubmit="return false;">
                            <div class="form-group">
                                <div class="col-sm-8">
                                    <select class="form-control" id="subdistrict_id">
                                        <option value="0">请选择</option>
                                    </select>
                                </div>
                                <div class="col-sm-4">
                                    <button class="btn btn-primary" id="import_as_system_file">选择文件</button>
                                    <button id="confirm_upload"></button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            require(["commonFunction", "resident_list"], function (commonFunction, residentList) {
                window.commonFunction = commonFunction;
                residentList("${pageContext.request.contextPath}/");
            });
        </script>
    </body>
</html>
