import Vue from "vue";
import { $ajax } from "@library/javascript/common";

$(document).ready(() => {
    /**
     * 退出登录
     */
    document.querySelector("#logout").addEventListener("click", () => {
        $ajax(
            {
                url: logoutUrl,
                method: "post",
                headers: {
                    "X-CSRF-TOKEN": $("meta[name='X-CSRF-TOKEN']").prop(
                        "content"
                    ),
                },
            },
            (data, textStatus, jqXHR) => {
                if (jqXHR.status === 200) {
                    location.href = "/login";
                }
            }
        );
    });
    new Vue({
        el: "#sidebar",
        data: {
            getMenuUrl: getMenuUrl,
            userPrivileges: [],
            currentUri: null,
        },
        created() {
            let arrUrl = location.toString().split("//");
            this.currentUri = arrUrl[1]
                .replace("#/", "")
                .substring(arrUrl[1].indexOf("/"));
            $ajax(
                {
                    url: this.getMenuUrl,
                    data: {
                        display: true,
                    },
                },
                (data) => {
                    if (data.state === 1) {
                        data.userPrivileges.forEach((userPrivilege) => {
                            if (
                                userPrivilege.constraintAuth.slice(-5) ===
                                "Title"
                            ) {
                                this.userPrivileges.push(userPrivilege);
                                userPrivilege.subUserPrivileges.forEach(
                                    (subUserPrivilege) => {
                                        this.userPrivileges.push(
                                            subUserPrivilege
                                        );
                                    }
                                );
                            }
                        });
                    }
                }
            );
        },
        methods: {
            /**
             * 设置开启或关闭菜单
             * @param userPrivilege
             * @return {string}
             */
            setClassOpen(userPrivilege) {
                if (userPrivilege.subUserPrivileges.subUserPrivileges) {
                    for (
                        let i = 0;
                        i < userPrivilege.subUserPrivileges.length;
                        i++
                    ) {
                        let subUserPrivilege =
                            userPrivilege.subUserPrivileges[i];
                        console.log(subUserPrivilege);
                        if (typeof subUserPrivilege === "undefined") {
                            continue;
                        }
                        if (subUserPrivilege.uri.includes(this.currentUri)) {
                            return "open";
                        }
                    }
                }
            },
        },
    });
});
