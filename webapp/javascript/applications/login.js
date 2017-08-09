require(["jquery", "layui"], function () {
	$(function (){
		/**
		 * 用户名输入框键盘事件
		 */
		$("#username").keyup(function(event){
			if ($(this).val() != "" && event.keyCode == 13) {
				$("#password").focus();
			}
		}).focus(function () {
			$(this).parent("span").parent("li").removeClass("has-error has-success");
		}).focus();
		/**
		 * 密码输入框键盘事件
		 */
		$("#password").keyup(function(event){
			if ($(this).val() != "" && event.keyCode == 13) {
				$("#logging").trigger("click");
			}
		}).focus(function () {
			$(this).parent("span").parent("li").removeClass("has-error has-success");
		});
		/**
		 * 登录按钮点击事件
		 */
		$("#logging").click(function() {
            layui.use('layer', function () {
                var layer = layui.layer;
                layer.ready(function () {
                    var index = layer.load(1, {shade: [0.8, '#000']});
                    var username = $("#username");
                    var password = $("#password");
                    var captcha = $("#captcha");
                    var username_value = username.val().trim();
                    var password_value = password.val();
                    var captcha_value = captcha.val();
                    var username_li = username.parent("span").parent("li");
                    var password_li = password.parent("span").parent("li");
                    var captcha_li = captcha.parent("span").parent("li");
                    if (username_value != "" && password_value != "") {
                        $.ajax({
                            "async": false,
                            "data": {
                                "username": username_value,
                                "password": password_value,
                                "captcha": captcha_value,
                                "_token": $("input[name='_token']").val()
                            },
                            "success": function (data) {
                                layer.close(index);
                                if (data.state > 0) {
                                    username_li.removeClass("has-error").addClass("has-success");
                                    password_li.removeClass("has-error").addClass("has-success");
                                    layer.msg(data.message, {icon: 6, shade: [0.8, '#000']}, function () {
                                        location.href = basePath + "index.action";
                                    });
                                } else if (data.state == 0) {
                                    username_li.removeClass("has-success").addClass("has-error");
                                    password_li.removeClass("has-success").addClass("has-error");
                                    layer.msg(data.message, {icon: 5});
                                } else if (data.state == -1) {
                                    captcha_li.removeClass("has-success").addClass("has-error");
                                    layer.msg(data.message, {icon: 5});
                                }
                            },
                            "type": "post",
                            "url": basePath + "login/ajax.action"
                        });
                    }
                });
            });
        });
        /**
         * 点击验证码图片更换
         */
		$("#captcha_img").click(function () {
		    var src = $(this).prop("src");
		    if (src.indexOf("?") > -1) {
                src = src.substring(0, src.indexOf("?"));
            }
            $(this).attr("src", src + "?timestamp=" + new Date().getTime())
        });
	});
});