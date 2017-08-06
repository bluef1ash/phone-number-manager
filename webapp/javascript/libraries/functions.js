/**
 * 使用AJAX删除系统用户
 * @param obj
 * @param id
 * @param uri
 * @param message
 */
function delete_object(obj, id, uri, message) {
	if (id != null) {
		var this_obj = $(obj);
		$.get(base_path + uri, {"id" : id}, function (data) {
			if (data) {
				this_obj.parent("td").parent("tr").remove();
				layer.msg(message + "删除成功！", {icon : 1});
			} else {
				layer.msg(message + "删除失败！", {icon: 5});
	 		}
	 	});
 	}
 }