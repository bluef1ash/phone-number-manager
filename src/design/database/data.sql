-- 添加表用户权限
GRANT SELECT, UPDATE, DELETE, INSERT, EXECUTE ON `phone_number`.* TO phone_number@localhost;
truncate table `phone_number`.`pm_privileges`;
-- 添加基础权限
INSERT INTO `phone_number`.`pm_privileges`
(`privilege_id`, `name`, `constraint_auth`, `uri`, `higher_privilege`, `icon_name`, `orders`, `is_display`)
VALUES (1, '社区居民相关', 'communityResidentTitle', '', 0, '', 0, 1),
    (2, '社区相关', 'communityTitle', '', 0, '', 0, 1),
    (3, '街道相关', 'subdistrictTitle', '', 0, '', 0, 1),
    (4, '系统相关', 'systemTitle', '', 0, '', 0, 1),
    (5, '社区居民管理', 'communityResidentAction', '', 1, 'fa fa-phone', 0, 1),
    (6, '社区管理', 'communityAction', '', 2, 'fa fa-building', 0, 1),
    (7, '街道管理', 'subdistrictAction', '', 3, 'fa fa-laptop', 0, 1),
    (8, '用户和角色管理', 'userAndRoleAction', '', 4, 'fa fa-user', 0, 1),
    (9, '系统管理', 'systemAction', '', 4, 'fa fa-cog', 0, 1),
(10, '居民电话列表', 'communityResidentList', '/resident/list', 5, 'fa fa-list', 0, 1),
(11, '添加居民信息', 'createCommunityResident', '/resident/create', 5, 'fa fa-plus', 0, 1),
(12, '修改居民信息', 'editCommunityResident', '/resident/edit', 5, 'fa fa-pencil-square-o', 0, 0),
(13, '添加、修改居民信息处理', 'communityResidentCreateOrEditHandle', '/resident/handle', 5, '', 0, 0),
(14, '通过AJAX技术删除居民信息', 'deleteCommunityResidentForAjax', '/resident/ajax_delete', 5, '', 0, 0),
(15, '导入居民信息进系统', 'communityResidentImportAsSystem', '/resident/import_as_system', 5, '', 0, 0),
(16, '导出居民信息到Excel', 'communityResidentSaveAsExcel', '/resident/save_as_excel', 5, '', 0, 0),
(17, '使用AJAX技术列出社区居委会', 'findCommunityForAjax', '/resident/ajax_select', 5, 'fa fa-list', 0, 0),
    (18, '社区列表', 'communityList', '/community/list', 6, 'fa fa-list', 0, 1),
(19, '添加社区信息', 'createCommunity', '/community/create', 6, 'fa fa-plus', 0, 1),
(20, '修改社区信息', 'editCommunity', '/community/edit', 6, 'fa fa-pencil-square-o', 0, 0),
(21, '添加、修改社区信息处理', 'communityCreateOrEditHandle', '/community/handle', 6, '', 0, 0),
(22, '通过AJAX技术删除社区信息', 'deleteCommunityForAjax', '/community/ajax_delete', 6, '', 0, 0),
(23, '社区分包人列表', 'subcontractorList', '/community/subcontractor/list', 6, 'fa fa-list', 0, 1),
(24, '添加社区分包人信息', 'createSubcontractor', '/community/subcontractor/create', 6, 'fa fa-plus', 0, 1),
(25, '修改社区分包人信息', 'editSubcontractor', '/community/subcontractor/edit', 6, 'fa fa-pencil-square-o', 0, 0),
(26, '添加、修改社区分包人信息处理', 'subcontractorCreateOrEditHandle', '/community/subcontractor/handle', 6, '', 0, 0),
(27, '通过AJAX技术删除社区分包人信息', 'deleteSubcontractorForAjax', '/community/subcontractor/ajax_delete', 6, '', 0, 0),
(28, '街道列表', 'subdistrictList', '/subdistrict/list', 7, 'fa fa-list', 0, 1),
(29, '添加街道信息', 'createSubdistrict', '/subdistrict/create', 7, 'fa fa-plus', 0, 1),
(30, '修改街道信息', 'editSubdistrict', '/subdistrict/edit', 7, 'fa fa-pencil-square-o', 0, 0),
(31, '添加、修改街道信息处理', 'subdistrictCreateOrEditHandle', '/subdistrict/handle', 7, '', 0, 0),
(32, '通过AJAX技术删除街道信息', 'deleteSubdistrictForAjax', '/subdistrict/ajax_delete', 7, '', 0, 0),
(33, '通过Ajax技术获取街道信息', 'getSubdistrictForAjax', '/subdistrict/ajax_load', 7, '', 0, 0),
(34, '系统用户列表', 'systemUserList', '/system/user_role/user/list', 8, 'fa fa-list', 0, 1),
(35, '系统用户锁定与解锁', 'systemUserLockedForAjax', '/system/user_role/user/ajax_user_lock', 8, '', 0,
 0),
(36, '添加系统用户', 'createSystemUser', '/system/user_role/user/edit', 8, 'fa fa-plus', 0, 0),
(37, '修改系统用户', 'editSystemUser', '/system/user_role/user/edit', 8, 'fa fa-pencil-square-o', 0, 0),
(38, '添加与修改处理系统用户', 'systemUserAddOrEditHandle', '/system/user_role/user/handle', 8, '', 0, 0),
(39, '通过AJAX技术删除系统用户', 'deleteSystemUserForAjax', '/system/user_role/user/ajax_delete', 8, '', 0, 0),
(40, '使用Ajax技术通过系统角色编号获取单位', 'getCompanyIdForAjax', '/system/user_role/user/ajax_get_company', 8, 'fa fa-list', 0, 0),
(41, '使用Ajax技术获取所有系统用户', 'getSystemUsersForAjax', '/system/user_role/user/ajax_get', 8, 'fa fa-list', 0, 0),
(42, '使用Ajax技术通过系统角色编号获取', 'getCompanyForAjax', '/system/user_role/user/ajax_get_companies', 8, 'fa fa-list', 0, 0),
(43, '系统用户角色列表', 'systemUserRoleList', '/system/user_role/role/list', 8, 'fa fa-list', 0, 1),
(44, '添加系统用户角色', 'createSystemUserRole', '/system/user_role/role/edit', 8, 'fa fa-plus', 0, 0),
(45, '修改系统用户角色', 'editSystemUserRole', '/system/user_role/user/edit', 8, 'fa fa-pencil-square-o', 0, 0),
(46, '添加与修改处理系统用户角色', 'systemUserRoleCreateOrEditHandle', '/system/user_role/role/handle', 8, '', 0, 0),
(47, '通过AJAX技术删除系统用户角色', 'deleteSystemUserRoleForAjax', '/system/user_role/role/ajax_delete', 8, '', 0, 0),
(48, '系统用户权限列表', 'systemUserPrivilegeList', '/system/user_role/privilege/list', 8, 'fa fa-list', 0, 1),
(49, '添加系统用户权限', 'createSystemUserPrivilege', '/system/user_role/privilege/edit', 8, 'fa fa-plus', 0, 0),
(50, '修改系统用户权限', 'editSystemUserPrivilege', '/system/user_role/privilege/edit', 8, 'fa fa-pencil-square-o', 0, 0),
(51, '添加与修改处理系统用户权限', 'systemUserPrivilegeCreateOrEditHandle', '/system/user_role/privilege/handle', 8, '', 0, 0),
(52, '通过AJAX技术删除系统用户权限', 'deleteSystemUserPrivilegeForAjax', '/system/user_role/privilege/ajax_delete', 8, '', 0, 0),
(53, '通过AJAX技术获取系统用户角色拥有的权限', 'getPrivilegesByRoleIdForAjax', '/system/user_role/privilege/ajax_get_privileges', 9,
 'fa fa-list', 0, 0),
(54, '系统配置列表', 'systemConfigurationList', '/system/configuration/list', 9, 'fa fa-list', 0, 1),
(55, '添加系统配置', 'createConfiguration', '/system/configuration/edit', 9, 'fa fa-plus', 0,
 0),
(56, '修改系统配置', 'editConfiguration', '/system/configuration/edit', 9, 'fa fa-pencil-square-o', 0,
 0),
(57, '添加与修改处理系统配置', 'systemConfigurationCreateOrEditHandle', '/system/configuration/handle', 9,
 '', 0, 0),
(58, '通过AJAX技术删除系统配置', 'deleteConfigurationForAjax', '/system/configuration/ajax_delete', 9,
 '', 0, 0);
-- 添加基础角色
INSERT INTO `phone_number`.`pm_roles`
    (`role_id`, `name`, `description`, `higher_role`)
VALUES (1, '系统管理员', '系统管理员', 0),
    (2, '街道管理员', '街道管理员', 1),
    (3, '社区管理员', '社区管理员', 2);
-- 添加基础系统用户
INSERT INTO `phone_number`.`pm_system_users`
    (`system_user_id`, `username`, `password`, `role_id`)
VALUES (1, 'admin', md5('123456'), 1);
-- 添加系统配置
INSERT INTO `phone_number`.`pm_configurations`
    (`key`, `type`, `value`, `description`, `key_is_changed`)
VALUES ('system_administrator_id', 3, '1', '系统管理员编号', 0),
    ('system_role_id', 3, '1', '系统用户角色编号', 0),
    ('community_role_id', 3, '3', '社区用户角色编号', 0),
    ('subdistrict_role_id', 3, '2', '街道用户角色编号', 0),
('excel_subdistrict_cell_number', 3, '0', 'Excel表中“街道”所在列的位置序号，从0开始', 0),
    ('excel_community_cell_number', 3, '1', 'Excel表中“社区”所在列的位置序号，从0开始', 0),
('excel_community_resident_name_cell_number', 3, '2', 'Excel表中“户主姓名”所在列的位置序号，从0开始', 0),
    ('excel_address_cell_number', 3, '3', 'Excel表中“家庭住址”所在列的位置序号，从0开始', 0),
    ('excel_phone1_cell_number', 3, '4', 'Excel表中“联系方式一”所在列的位置序号，从0开始', 0),
    ('excel_phone2_cell_number', 3, '5', 'Excel表中“联系方式二”所在列的位置序号，从0开始', 0),
    ('excel_phone3_cell_number', 3, '6', 'Excel表中“联系方式三”所在列的位置序号，从0开始', 0),
('excel_subcontractor_cell_number', 3, '7', 'Excel表中“分包人”所在列的位置序号，从0开始', 0),
    ('excel_title', 2, '“评社区”活动电话库登记表', 'Excel表标题', 0),
    ('excel_title_up', 2, '附件2', 'Excel表标题上文字', 0),
('read_excel_start_row_number', 3, '4', '读取Excel表开始行号，从内容开始的行数加，包含开始行数', 0),
    ('system_is_active', 1, '1', '系统是否开启', 0);
