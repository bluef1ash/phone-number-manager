-- 添加表用户权限
GRANT SELECT, UPDATE, DELETE, INSERT, EXECUTE ON `phone_number_manager`.* TO www@localhost;
-- 添加基础权限
INSERT INTO `phone_number_manager`.`pm_privileges`
(`privilege_id`, `name`, `constraint_auth`, `uri`, `higher_privilege`, `icon_name`, `orders`, `is_display`) VALUES
(1, '社区居民管理', 'communityResidentAction', '', 0, 'glyphicon glyphicon-earphone', 0, 1),
(2, '社区管理', 'communityAction', '', 0, 'glyphicon glyphicon-tree-deciduous', 0, 1),
(3, '街道管理', 'subdistrictAction', '', 0, 'glyphicon glyphicon-briefcase', 0, 1),
(4, '用户和角色管理', 'userAndRoleAction', '', 0, 'glyphicon glyphicon-heart', 0, 1),
(5, '系统管理', 'systemAction', '', 0, 'glyphicon glyphicon-cog', 0, 1),
(6, '居民电话列表', 'communityResidentList', '/resident/list', 1, 'glyphicon glyphicon-list-alt', 0, 1),
(7, '添加居民信息', 'createCommunityResident', '/resident/create', 1, 'glyphicon glyphicon-plus', 0, 1),
(8, '修改居民信息', 'editCommunityResident', '/resident/edit', 1, 'glyphicon glyphicon-edit', 0, 0),
(9, '添加、修改居民信息处理', 'communityResidentCreateOrEditHandle', '/resident/handle', 1, 'glyphicon glyphicon-repeat', 0, 0),
(10, '通过AJAX技术删除居民信息', 'deleteCommunityResidentForAjax', '/resident/ajax_delete', 1, 'glyphicon glyphicon-remove', 0, 0),
(11, '导入居民信息进系统', 'communityResidentImportAsSystem', '/resident/import_as_system', 1, 'glyphicon glyphicon-import', 0, 0),
(12, '导出居民信息到Excel', 'communityResidentSaveAsExcel', '/resident/save_as_excel', 1, 'glyphicon glyphicon-export', 0, 0),
(13, '使用AJAX技术列出社区居委会', 'findCommunityForAjax', '/resident/ajax_select', 1, 'glyphicon glyphicon-list-alt', 0, 0),
(14, '社区列表', 'communityList', '/community/list', 2, 'glyphicon glyphicon-list-alt', 0, 1),
(15, '添加社区信息', 'createCommunity', '/community/create', 2, 'glyphicon glyphicon-plus', 0, 1),
(16, '修改社区信息', 'editCcommunity', '/community/edit', 2, 'glyphicon glyphicon-edit', 0, 0),
(17, '添加、修改社区信息处理', 'communityCreateOrEditHandle', '/community/handle', 2, 'glyphicon glyphicon-repeat', 0, 0),
(18, '通过AJAX技术删除社区信息', 'deleteCommunityForAjax', '/community/ajax_delete', 2, 'glyphicon glyphicon-remove', 0, 0),
(19, '街道列表', 'subdistrictList', '/subdistrict/list', 3, 'glyphicon glyphicon-list-alt', 0, 1),
(20, '添加街道信息', 'createSubdistrict', '/subdistrict/create', 3, 'glyphicon glyphicon-plus', 0, 1),
(21, '修改街道信息', 'editSubdistrict', '/subdistrict/edit', 3, 'glyphicon glyphicon-edit', 0, 0),
(22, '添加、修改街道信息处理', 'subdistrictCreateOrEditHandle', '/subdistrict/handle', 3, 'glyphicon glyphicon-repeat', 0, 0),
(23, '通过AJAX技术删除街道信息', 'deleteSubdistrictForAjax', '/subdistrict/ajax_delete', 3, 'glyphicon glyphicon-remove', 0, 0),
(24, '系统用户列表', 'systemUserList', '/system/user_role/user/list', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(25, '系统用户锁定与解锁', 'systemUserLockedForAjax', '/system/user_role/user/ajax_user_lock', 4, 'glyphicon glyphicon-lock', 0, 0),
(26, '添加系统用户', 'createSystemUser', '/system/user_role/user/edit', 4, 'glyphicon glyphicon-plus', 0, 0),
(27, '修改系统用户', 'editSystemUser', '/system/user_role/user/edit', 4, 'glyphicon glyphicon-edit', 0, 0),
(28, '添加与修改处理系统用户', 'systemUserAddOrEditHandle', '/system/user_role/user/handle', 4, 'glyphicon glyphicon-repeat', 0, 0),
(29, '通过AJAX技术删除系统用户', 'deleteSystemUserForAjax', '/system/user_role/user/ajax_delete', 4, 'glyphicon glyphicon-remove', 0, 0),
(30, '使用Ajax技术通过系统角色编号获取单位', 'getCompanyIdForAjax', '/system/user_role/user/ajax_get_company', 4, 'glyphicon glyphicon-list-alt', 0, 0),
(31, '系统用户角色列表', 'systemUserRoleList', '/system/user_role/role/list', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(32, '添加系统用户角色', 'createSystemUserRole', '/system/user_role/role/edit', 4, 'glyphicon glyphicon-plus', 0, 0),
(33, '修改系统用户角色', 'editSystemUserRole', '/system/user_role/user/edit', 4, 'glyphicon glyphicon-edit', 0, 0),
(34, '添加与修改处理系统用户角色', 'systemUserRoleCreateOrEditHandle', '/system/user_role/role/handle', 4, 'glyphicon glyphicon-repeat', 0, 0),
(35, '通过AJAX技术删除系统用户角色', 'deleteSystemUserRoleForAjax', '/system/user_role/role/ajax_delete', 4, 'glyphicon glyphicon-remove', 0, 0),
(36, '系统用户权限列表', 'systemUserPrivilegeList', '/system/user_role/privilege/list', 4, 'glyphicon glyphicon-list-alt', 0, 1),
(37, '添加系统用户权限', 'createSystemUserPrivilege', '/system/user_role/privilege/edit', 4, 'glyphicon glyphicon-plus', 0, 0),
(38, '修改系统用户权限', 'editSystemUserPrivilege', '/system/user_role/privilege/edit', 4, 'glyphicon glyphicon-edit', 0, 0),
(39, '添加与修改处理系统用户权限', 'systemUserPrivilegeCreateOrEditHandle', '/system/user_role/privilege/handle', 4, 'glyphicon glyphicon-repeat', 0, 0),
(40, '通过AJAX技术删除系统用户权限', 'deleteSystemUserPrivilegeForAjax', '/system/user_role/privilege/ajax_delete', 4, 'glyphicon glyphicon-remove', 0, 0),
(41, '通过AJAX技术获取系统用户角色拥有的权限', 'getPrivilegesByRoleIdForAjax', '/system/user_role/privilege/ajax_get_privileges', 4, 'glyphicon glyphicon-list-alt', 0, 0)
;
-- 添加基础角色
INSERT INTO `phone_number_manager`.`pm_roles`
(`role_id`,`name`,`description`,`higher_role`) VALUES
(1, '系统管理员', '系统管理员', 0),
(2, '街道管理员', '街道管理员', 1),
(3, '社区管理员', '社区管理员', 2)
;
-- 添加基础系统用户
INSERT INTO `phone_number_manager`.`pm_system_users`
(`system_user_id`, `username`, `password`, `role_id`) VALUES
(1, 'admin', md5('123456'), 1)
;
