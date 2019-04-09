import Vue from "vue";

$(document).ready(() => {
    new Vue({
        el: "#sidebar",
        data: {
            csrf: csrf,
            getMenuUrl: getMenuUrl,
            userPrivileges: [],
            currentUri: null
        },
        created() {
            let arrUrl = location.toString().split("//");
            this.currentUri = arrUrl[1].substring(arrUrl[1].indexOf("/"));
            if (this.currentUri.indexOf("?") !== -1) {
                this.currentUri = this.currentUri.split("?")[0];
            }
            $.ajax({
                url: this.getMenuUrl,
                method: "get",
                data: {
                    _csrf: this.csrf,
                    isDisplay: 1
                }
            }).then(data => {
                if (data.state === 1) {
                    data.userPrivileges.forEach(item => {
                        if (item.constraintAuth.slice(-5) === "Title") {
                            this.userPrivileges.push(item);
                            item.subUserPrivileges.forEach(sub => {
                                this.userPrivileges.push(sub);
                            });
                        }
                    });
                }
            });
        },
        methods: {
            /**
             * 设置开启或关闭菜单
             * @param userPrivilege
             * @return {string}
             */
            setClassOpen(userPrivilege) {
                if (userPrivilege.subUserPrivileges && userPrivilege.subUserPrivileges[0].uri !== "") {
                    for (let i = 0; i < userPrivilege.subUserPrivileges.length; i++) {
                        let uri = userPrivilege.subUserPrivileges[i].uri.substring(0, userPrivilege.subUserPrivileges[i].uri.lastIndexOf("/"));
                        if (this.currentUri.indexOf(uri) > -1) {
                            return "open";
                        }
                    }
                }
            }
        }
    });
});
