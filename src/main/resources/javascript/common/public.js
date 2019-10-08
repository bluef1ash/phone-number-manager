import "@baseSrc/scss/index.scss";
import "bootstrap";
import "@coreui/coreui";
import Pace from "pace-progress";
import commonFunction from "@base/lib/javascript/common";

Pace.start({
    document: false
});

/**
 * 退出登录
 */
function logout() {
    commonFunction.$ajax({
        url: logoutUrl,
        method: "post",
        headers: {
            "X-CSRF-TOKEN": $("meta[name='X-CSRF-TOKEN']").prop("content")
        }
    }, data => {
        location.reload();
    });
}
