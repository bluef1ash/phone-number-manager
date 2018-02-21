define("configuration", ["jquery", "layui"], function () {
    var findSystemUsersUrl = null;
    /**
     * 根据系统配置项值类别改变系统配置项值表单
     * @param typeValue 系统配置项值类别
     * @param value 回显
     */
    function changeType(typeValue, value) {
        var configurationValue = $("#configuration_value");
        switch (typeValue) {
            case "1":
                configurationValue.html('<label class="radio-inline"><input type="radio" name="value" value="1">是</label><label class="radio-inline"><input type="radio" name="value" value="0">否</label>');
                if (value != null) {
                    $("input[name='value']").val(value);
                }
                break;
            case "2":
                configurationValue.html('<input type="text" class="form-control" name="value" placeholder="请输入系统配置项值">');
                if (value != null) {
                    $("input[name='value']").val(value);
                }
                break;
            case "3":
                configurationValue.html('<input type="number" class="form-control" name="value" placeholder="请输入系统配置项值">');
                if (value != null) {
                    $("input[name='value']").val(value);
                }
                break;
            case "4":
                var _token = $("input[name='_token']");
                $.ajax({
                    url: findSystemUsersUrl,
                    method: "get",
                    data: {_token: _token.val()},
                    success: function (data) {
                        if (data.state) {
                            configurationValue.html('<select name="value" class="form-control"><option value="0">请选择</option></select>');
                            var _option = "";
                            for (var i = 0; i < data.systemUsers.length; i++) {
                                _option += '<option value="' + data.systemUsers[i].systemUserId + '">' + data.systemUsers[i].username + "</option>";
                            }
                            var select = $("select[name='value']");
                            select.append(_option);
                            if (value != null) {
                                select.val(value);
                            }
                        } else {
                            layui.use("layer", function () {
                                var layer = layui.layer;
                                layer.ready(function () {
                                    layer.msg(data.message, {icon: 5});
                                });
                            });
                        }
                        _token.val(data._token);
                    }
                });
                break;
            default:
                break;
        }
    }
    return function (findSystemUsersUrlParam, value) {
        findSystemUsersUrl = findSystemUsersUrlParam;
        $(function () {
            var typeSelect = $("select[name='type']");
            changeType(typeSelect.val(), value);
            typeSelect.change(function () {
                changeType($(this).val(), null);
            });
        });

    }
});
