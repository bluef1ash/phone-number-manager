<%@ page language="java" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/jsp/layouts/header.jsp" %>
<title>
    <c:choose><c:when test="${subcontractor.communityId == null}">添加</c:when><c:otherwise>修改</c:otherwise></c:choose>社区分包人 - 社区管理 - 社区居民联系电话管理系统</title>
</head>
<body>
    <c:if test="${messageErrors != null}"> <c:forEach items="${messageErrors}" var="error">
        <div class="alert alert-danger alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <span>${error.defaultMessage}</span>
        </div>
    </c:forEach> </c:if>
    <form action="${pageContext.request.contextPath}/community/subcontractor/handle" method="post">
        <table class="table table-bordered font-size-14" id="subcontractor">
            <thead></thead>
            <tbody>
                <tr>
                    <td class="text-right" style="width: 35%;">社区分包人姓名</td>
                    <td>
                        <input type="text" name="name" value="${subcontractor.name}" class="form-control" placeholder="请输入社区分包人姓名">
                    </td>
                </tr>
                <tr>
                    <td class="text-right">社区分包人联系方式</td>
                    <td>
                        <input type="text" name="telephone" value="${subcontractor.telephone}" class="form-control" placeholder="请输入社区分包人联系方式">
                    </td>
                </tr>
                <tr>
                    <td class="text-right">分包人所属街道</td>
                    <td>
                        <select name="subdistrictId" class="form-control" v-model="subdistrictId" @change="choseSubdistrict">
                            <option :value="0">请选择</option>
                            <option :value="subdistrict.id" v-for="(subdistrict, index) in subdistricts" :key="subdistrict.id">{{subdistrict.name}}</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="text-right">分包人所属社区</td>
                    <td>
                        <select name="communityId" class="form-control" v-model="communityId">
                            <option :value="0">请选择</option>
                            <option :value="community.id" v-for="(community, index) in newCommunities" :key="community.id">{{community.name}}</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" class="text-center">
                        <c:if test="${community.communityId != null}">
                            <input type="hidden" name="communityId" value="${community.communityId}">
                            <input type="hidden" name="_method" value="PUT"> </c:if>
                        <input type="hidden" name="_token" value="${_token}">
                        <spring:htmlEscape defaultHtmlEscape="true"/>
                        <input type="submit" value="保存" class="btn btn-primary">
                        <button class="btn btn-default margin-l-10" role="button" onclick="history.go(-1)">返回</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </form>
    <script type="text/javascript">
        require(["vue", "lodash", "bootstrap"], function(Vue) {
            var vm = new Vue({
                el: "#subcontractor",
                data: {
                    communities: ${communities},
                    newCommunities: [],
                    communityId: 0,
                    subdistricts: [],
                    subdistrictId: 0
                },
                created: function() {
                    for (var i = 0; i < this.communities.length; i++) {
                        if (i > 0 && this.communities[i].subdistrict.subdistrictId ===
                            this.communities[i - 1].subdistrict.subdistrictId) {
                            continue;
                        }
                        this.subdistricts.push({
                            id: this.communities[i].subdistrict.subdistrictId,
                            name: this.communities[i].subdistrict.subdistrictName
                        });
                    }
                },
                methods: {
                    /**
                     * 选择所属街道
                     */
                    choseSubdistrict: function() {
                        this.newCommunities = [];
                        this.communityId = 0;
                        for (var i = 0; i < this.communities.length; i++) {
                            if (this.communities[i].subdistrict.subdistrictId === this.subdistrictId) {
                                this.newCommunities.push({
                                    id: this.communities[i].communityId,
                                    name: this.communities[i].communityName
                                });
                            }
                        }
                    }
                }
            });
        });
    </script>
</body>
</html>
