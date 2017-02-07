-- 添加基础权限
INSERT INTO `phone_number_manager`.`pm_privileges`
(`privilege_id`, `privilege_name`, `privilege_description`, `constraint_auth`, `uri`, `higher_privilege`, `icon_name`, `orders`, `is_display`) VALUES
(1, '社区居民管理', '社区居民管理', 'communityResidentAction', '', 0, 'glyphicon glyphicon-earphone', 0, 1),
(2, '社区管理', '社区管理', 'communityAction', '', 0, 'glyphicon glyphicon-tree-deciduous', 0, 1),
(3, '街道管理', '街道管理', 'subdistrictAction', '', 0, 'glyphicon glyphicon-briefcase', 0, 1),
(4, '用户和角色管理', '用户和角色管理', 'userAndRoleAction', '', 0, 'glyphicon glyphicon-heart', 0, 1),
(5, '系统管理', '系统管理', 'systemAction', '', 0, 'glyphicon glyphicon-cog', 0, 1),
(6, '居民电话列表', '居民电话列表', 'communityResidentList', '/resident/list.action', 1, 'glyphicon glyphicon-list-alt', 0, 1),
(7, '添加居民信息', '添加居民信息', 'createCommunityResident', '/resident/create.action', 1, 'glyphicon glyphicon-plus', 0, 1),
(8, '修改居民信息', '修改居民信息', 'editCommunityResident', '/resident/edit.action', 1, 'glyphicon glyphicon-edit', 0, 0),
(9, '添加、修改居民信息处理', '添加、修改居民信息处理', 'communityResidentCreateOrEditHandle', '/resident/handle.action', 1, 'glyphicon glyphicon-repeat', 0, 0),
(10, '通过AJAX技术删除居民信息', '通过AJAX技术删除居民信息', 'deleteCommunityResidentForAjax', '/resident/ajax_delete.action', 1, 'glyphicon glyphicon-remove', 0, 0),
(11, '导入居民信息进系统', '导入居民信息进系统', 'communityResidentImportAsSystem', '/resident/import_as_system.action', 1, 'glyphicon glyphicon-import', 0, 0),
(12, '导出居民信息到Excel', '导出居民信息到Excel', 'communityResidentSaveAsExcel', '/resident/save_as_excel.action', 1, 'glyphicon glyphicon-export', 0, 0),
(13, '社区列表', '社区列表', 'communityList', '/community/list.action', 2, 'glyphicon glyphicon-list-alt', 0, 1),
(14, '添加社区信息', '添加社区信息', 'createCommunity', '/community/create.action', 2, 'glyphicon glyphicon-plus', 0, 1),
(15, '修改社区信息', '修改社区信息', 'editCcommunity', '/community/edit.action', 2, 'glyphicon glyphicon-edit', 0, 0),
(16, '添加、修改社区信息处理', '添加、修改社区信息处理', 'communityCreateOrEditHandle', '/community/handle.action', 2, 'glyphicon glyphicon-repeat', 0, 0),
(17, '通过AJAX技术删除社区信息', '通过AJAX技术删除社区信息', 'deleteCommunityForAjax', '/community/ajax_delete.action', 2, 'glyphicon glyphicon-remove', 0, 0),
(18, '街道列表', '街道列表', 'subdistrictList', '/subdistrict/list.action', 3, 'glyphicon glyphicon-list-alt', 0, 1),
(19, '添加街道信息', '添加街道信息', 'createSubdistrict', '/subdistrict/create.action', 3, 'glyphicon glyphicon-plus', 0, 1),
(20, '修改街道信息', '修改街道信息', 'editSubdistrict', '/subdistrict/edit.action', 3, 'glyphicon glyphicon-edit', 0, 0),
(21, '添加、修改街道信息处理', '添加、修改街道信息处理', 'subdistrictCreateOrEditHandle', '/subdistrict/handle.action', 3, 'glyphicon glyphicon-repeat', 0, 0),
(22, '通过AJAX技术删除街道信息', '通过AJAX技术删除街道信息', 'deleteSubdistrictForAjax', '/subdistrict/ajax_delete.action', 3, 'glyphicon glyphicon-remove', 0, 0),
(23, '系统用户列表', '系统用户列表', 'systemUserList', '/system/user_role/user/list.action', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(24, '系统用户锁定与解锁', '系统用户锁定与解锁', 'systemUserLockedForAjax', '/system/user_role/user/ajax_user_lock.action', 4, 'glyphicon glyphicon-lock', 0, 0),
(25, '添加系统用户', '添加系统用户', 'createSystemUser', '/system/user_role/user/edit.action', 4, 'glyphicon glyphicon-plus', 0, 0),
(26, '修改系统用户', '修改系统用户', 'editSystemUser', '/system/user_role/user/edit.action', 4, 'glyphicon glyphicon-edit', 0, 0),
(27, '添加与修改处理系统用户', '添加与修改处理系统用户', 'systemUserAddOrEditHandle', '/system/user_role/user/handle.action', 4, 'glyphicon glyphicon-repeat', 0, 0),
(28, '通过AJAX技术删除系统用户', '通过AJAX技术删除系统用户', 'deleteSystemUserForAjax', '/system/user_role/user/ajax_delete.action', 4, 'glyphicon glyphicon-remove', 0, 0),
(29, '系统用户角色列表', '系统用户角色列表', 'systemUserRoleList', '/system/user_role/role/list.action', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(30, '添加系统用户角色', '添加系统用户角色', 'createSystemUserRole', '/system/user_role/role/edit.action', 4, 'glyphicon glyphicon-plus', 0, 0),
(31, '修改系统用户角色', '修改系统用户角色', 'editSystemUserRole', '/system/user_role/user/edit.action', 4, 'glyphicon glyphicon-edit', 0, 0),
(32, '添加与修改处理系统用户角色', '添加与修改处理系统用户角色', 'systemUserRoleCreateOrEditHandle', '/system/user_role/role/handle.action', 4, 'glyphicon glyphicon-repeat', 0, 0),
(33, '通过AJAX技术删除系统用户角色', '通过AJAX技术删除系统用户角色', 'deleteSystemUserRoleForAjax', '/system/user_role/role/ajax_delete.action', 4, 'glyphicon glyphicon-remove', 0, 0),
(34, '系统用户权限列表', '系统用户权限列表', 'systemUserPrivilegeList', '/system/user_role/privilege/list.action', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(35, '添加系统用户权限', '添加系统用户权限', 'createSystemUserPrivilege', '/system/user_role/privilege/edit.action', 4, 'glyphicon glyphicon-plus', 0, 0),
(36, '修改系统用户权限', '修改系统用户权限', 'editSystemUserPrivilege', '/system/user_role/privilege/edit.action', 4, 'glyphicon glyphicon-edit', 0, 0),
(37, '添加与修改处理系统用户权限', '添加与修改处理系统用户权限', 'systemUserPrivilegeCreateOrEditHandle', '/system/user_role/privilege/handle.action', 4, 'glyphicon glyphicon-repeat', 0, 0),
(38, '通过AJAX技术删除系统用户权限', '通过AJAX技术删除系统用户权限', 'deleteSystemUserPrivilegeForAjax', '/system/user_role/privilege/ajax_delete.action', 4, 'glyphicon glyphicon-remove', 0, 0)
;
-- 添加基础角色
INSERT INTO `phone_number_manager`.`pm_roles` 
(`role_id`,`role_name`,`role_description`,`higher_role`) VALUES
(1, '系统管理员', '系统管理员', 0),
(2, '街道管理员', '街道管理员', 1),
(3, '社区管理员', '社区管理员', 2)
;
-- 添加基础系统与基础权限
INSERT INTO `phone_number_manager`.`pm_roles_privileges` 
(`role_id`,`privilege_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5)
;
-- 添加基础系统用户
INSERT INTO `phone_number_manager`.`pm_system_users` 
(`system_user_id`, `username`, `password`, `role_id`) VALUES
(1, 'admin', md5('123456'), 1)
;