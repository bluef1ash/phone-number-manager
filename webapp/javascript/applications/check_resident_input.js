define("check_resident_input", ["jquery", "layui", function () {
    $(document).ready(function () {
        alert(1)
        /**
         * 提交验证
         */
        $("form[name='community_resident']").submit(function (event) {
            alert(1)
            event.preventDefault();
        });
    });
}]);
