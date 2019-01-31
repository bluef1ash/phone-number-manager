define("sidebar", [
    "pace",
    "vue",
    "jquery",
    "bootstrap",
    "perfect-scrollbar",
    "coreUi"], function(pace, Vue) {
    pace.start({
        document: false
    });
    new Vue({
        el: "#sidebar",
        data: {
            userPrivileges: [],
            currentUri: null
        },
        created: function() {
            var _this = this;
            var arrUrl = document.location.toString().split("//");
            _this.currentUri = arrUrl[1].substring(arrUrl[1].indexOf("/"));
            if (_this.currentUri.indexOf("?") !== -1) {
                _this.currentUri = _this.currentUri.split("?")[0];
            }
            $.ajax({
                url: "/index/getmenu",
                type: "get",
                data: {"isDisplay": 1},
                success: function(data, status) {
                    if (status === "success" && data !== null) {
                        for (var i = 0; i < data.userPrivileges.length; i++) {
                            if (data.userPrivileges[i].constraintAuth.slice(-5) === "Title") {
                                _this.userPrivileges.push(data.userPrivileges[i]);
                                for (var j = 0; j < data.userPrivileges[i].subUserPrivileges.length; j++) {
                                    _this.userPrivileges.push(data.userPrivileges[i].subUserPrivileges[j]);
                                }
                            }
                        }
                    }
                }
            });
        },
        methods: {
            /**
             * 设置开启或关闭菜单
             * @param userPrivilege
             * @return {string}
             */
            setClassOpen: function(userPrivilege) {
                if (userPrivilege.subUserPrivileges && userPrivilege.subUserPrivileges[0].uri !== "") {
                    for (var i = 0; i < userPrivilege.subUserPrivileges.length; i++) {
                        var uri = userPrivilege.subUserPrivileges[i].uri.substring(0, userPrivilege.subUserPrivileges[i].uri.lastIndexOf("/"));
                        if (this.currentUri.indexOf(uri) > -1) {
                            return "open";
                        }
                    }
                }
            }
        }
    });
});
