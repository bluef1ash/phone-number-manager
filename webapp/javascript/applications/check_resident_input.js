define("check_resident_input", ["jquery", "layui"], function () {
    return function () {
        $(document).ready(function () {
            /**
             * 提交验证
             */
            $("form[name='community_resident']").submit(function (event) {
                var communityResidentName = $("input[name='communityResidentName']");
                var communityResidentAddress = $("input[name='communityResidentAddress']");
                var communityResidentPhone1 = $("input[name='communityResidentPhone1']");
                var communityResidentPhone2 = $("input[name='communityResidentPhone2']");
                var communityResidentPhone3 = $("input[name='communityResidentPhone3']");
                var communityResidentSubcontractor = $("input[name='communityResidentSubcontractor']");
                var communityId = $("select[name='communityId']");
                if (communityResidentName.val() === "") {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        layer.ready(function () {
                            layer.msg("社区居民姓名不能为空！", {icon: 5});
                        });
                    });
                    event.preventDefault();
                    return false;
                }
                if (communityResidentAddress.val() === "") {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        layer.ready(function () {
                            layer.msg("社区居民家庭地址不能为空！", {icon: 5});
                        });
                    });
                    event.preventDefault();
                    return false;
                }
                if (communityResidentPhone1.val() === "" && communityResidentPhone2.val() === "" && communityResidentPhone3.val() === "") {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        layer.ready(function () {
                            layer.msg("社区居民联系方式必须至少填写一项！", {icon: 5});
                        });
                    });
                    event.preventDefault();
                    return false;
                }
                if (communityResidentSubcontractor.val() === "") {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        layer.ready(function () {
                            layer.msg("社区分包人不能为空！", {icon: 5});
                        });
                    });
                    event.preventDefault();
                    return false;
                }
                if (communityId.val() === "0") {
                    layui.use("layer", function () {
                        var layer = layui.layer;
                        layer.ready(function () {
                            layer.msg("必须选择所属社区！", {icon: 5});
                        });
                    });
                    event.preventDefault();
                    return false;
                }
            });
        });
    }
});
