export default {
    /**
     * 验证URL链接
     * @param  {string} str_url 需要验证的URL
     * @return {boolean}        返回是否URL合法
     */
    isURL(str_url) {
        let strRegex = "^((https|http|ftp|rtsp|mms)?://)"
            + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
            + "(([0-9]{1,3}.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
            + "|" // 允许IP和DOMAIN（域名）
            + "([0-9a-z_!~*'()-]+.)*" // 域名- www.
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." // 二级域名
            + "[a-z]{2,6})" // first level domain- .com or .museum
            + "(:[0-9]{1,4})?" // 端口- :80
            + "((/?)|" // a slash isn't required if there is no file name
            + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        let re = new RegExp(strRegex);
        //re.test()
        return re.test(str_url);
    },
    /**
     * 删除数据
     * @param vueObj
     * @param url
     * @param id
     * @param idField
     */
    deleteObject(vueObj, url, id, idField = "id") {
        if (id !== null && id !== "") {
            vueObj.$confirm("是否确定要删除此数据？", "警告", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {
                $.ajax({
                    url: deleteUrl,
                    method: "delete",
                    data: {
                        [idField]: id,
                        _csrf: vueObj.csrf
                    }
                }).then(data => {
                    if (data.state) {
                        vueObj.$message({
                            message: data.message,
                            type: "success"
                        });
                        setTimeout(function() {
                            location.reload();
                        }, 3000);
                    } else {
                        vueObj.$message({
                            message: data.message,
                            type: "error"
                        });
                    }
                });
            }).catch(() => {
            });
        }
    },
    /**
     * 去除字符串中所有空格、制表符、换行符
     * @returns {string | void | *}
     */
    trim(string) {
        return string.replace(/[\s\n\t]+/g, "");
    },
    /**
     * 检查联系方式的类型
     * @param phoneNumber 需要检测的联系方式
     * @returns {number} 联系方式的类型
     */
    checkPhoneType(phoneNumber) {
        if (phoneNumber === null || typeof phoneNumber === "undefined" || phoneNumber === "") {
            return 0;
        } else if (/^[+86]?1[34578]\d{9}$/.test(phoneNumber)) {
            return 1;
        } else if (/^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}([-]\d{2,4})?$/.test(phoneNumber)) {
            return 2;
        }
        return -1;
    },
    /**
     * 判断用户是否使用PC端打开
     * @return {boolean}
     */
    browserType() {
        let sUserAgent = navigator.userAgent.toLowerCase();
        let bIsIpad = sUserAgent.match(/ipad/i) === "ipad";
        let bIsIphoneOs = sUserAgent.match(/iphone os/i) === "iphone os";
        let bIsMidp = sUserAgent.match(/midp/i) === "midp";
        let bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) === "rv:1.2.3.4";
        let bIsUc = sUserAgent.match(/ucweb/i) === "ucweb";
        let bIsAndroid = sUserAgent.match(/android/i) === "android";
        let bIsCE = sUserAgent.match(/windows ce/i) === "windows ce";
        let bIsWM = sUserAgent.match(/windows mobile/i) ===
            "windows mobile";
        return bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc ||
            bIsAndroid || bIsCE || bIsWM;
    }
};
