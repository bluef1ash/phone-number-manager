define("check_resident_input", ["commonFunction", "jquery", "layui"], function (commonFunction) {
    return function () {
        $(document).ready(function () {
            var communityResidentName = $("input[name='communityResidentName']");
            var communityResidentAddress = $("input[name='communityResidentAddress']");
            var communityResidentPhone1 = $("input[name='communityResidentPhone1']");
            var communityResidentPhone2 = $("input[name='communityResidentPhone2']");
            var communityResidentPhone3 = $("input[name='communityResidentPhone3']");
            var communityResidentSubcontractor = $("input[name='communityResidentSubcontractor']");
            var communityId = $("select[name='communityId']");
            communityResidentName.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityResidentAddress.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityResidentPhone1.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityResidentPhone2.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityResidentPhone3.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityResidentSubcontractor.on("focus", function () {
                $(this).removeClass("has-error has-success");
            });
            communityId.on("change", function () {
                $(this).removeClass("has-error has-success");
            });
            /**
             * 提交验证
             */
            $("form[name='community_resident']").submit(function (event) {
                var communityResidentNameValue = commonFunction.trim(communityResidentName.val());
                var communityResidentAddressValue = commonFunction.trim(communityResidentAddress.val());
                var communityResidentPhone1Value = commonFunction.trim(communityResidentPhone1.val());
                var communityResidentPhone2Value = commonFunction.trim(communityResidentPhone2.val());
                var communityResidentPhone3Value = commonFunction.trim(communityResidentPhone3.val());
                var communityResidentSubcontractorValue = commonFunction.trim(communityResidentSubcontractor.val());
                communityResidentName.val(communityResidentNameValue);
                communityResidentAddress.val(communityResidentAddressValue);
                communityResidentPhone1.val(communityResidentPhone1Value);
                communityResidentPhone2.val(communityResidentPhone2Value);
                communityResidentPhone3.val(communityResidentPhone3Value);
                communityResidentSubcontractor.val(communityResidentSubcontractorValue);
                if (communityResidentNameValue == "") {
                    return stopSubmit(this, event, "社区居民姓名不能为空！");
                }
                if (communityResidentAddressValue == "") {
                    return stopSubmit(this, event, "社区居民家庭地址不能为空！");
                }
                if (communityResidentPhone1Value == "" && communityResidentPhone2Value == "" && communityResidentPhone3Value == "") {
                    return stopSubmit(this, event, "社区居民联系方式必须至少填写一项！");
                } else {
                    if (communityResidentPhone1Value == communityResidentPhone2Value || communityResidentPhone1Value == communityResidentPhone3Value || communityResidentPhone2Value == communityResidentPhone3Value) {
                        return stopSubmit(this, event, "社区居民联系方式不能相同！");
                    }
                }
                if (communityResidentSubcontractorValue == "") {
                    return stopSubmit(this, event, "社区分包人不能为空！");
                }
                if (communityId.val() == "0") {
                    return stopSubmit(this, event, "必须选择所属社区！");
                }
            });
        });

        /**
         * 停止提交
         * @param obj
         * @param event
         * @param string
         * @returns {boolean}
         */
        function stopSubmit(obj, event, string) {
            layui.use("layer", function () {
                var layer = layui.layer;
                layer.ready(function () {
                    layer.msg(string, {icon: 5});
                    event.preventDefault();
                });
            });
            $(obj).addClass("has-error");
            return false;
        }
    }
});
