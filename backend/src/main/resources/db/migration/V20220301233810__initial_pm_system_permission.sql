INSERT INTO `pm_system_permission` (`id`, `name`, `function_name`, `uri`, `http_methods`, `level`, `parent_id`,
                                    `menu_type`, `is_display`, `create_time`, `update_time`)
VALUES (1, '首页相关', 'indexController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (2, '社区居民管理', 'communityResidentController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (3, '社区楼长管理', 'dormitoryManagerController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (4, '单位管理', 'companyController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (5, '系统用户与系统权限管理', 'userAndPermissionController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (6, '系统管理', 'systemController', '', '', 0, 0, 0, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (7, '欢迎', './welcome', '/welcome', '["GET"]', 1, 1, 1, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (8, '获取首页菜单栏内容', 'getMenu', '/menu', '["GET"]', 1, 1, 2, 0, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (9, '获取图表数据', 'getComputedCount', '/computed', '["GET"]', 1, 1, 2, 0, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (10, '居民电话列表', 'communityResidentList', '/resident', '["GET"]', 1, 2, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (11, '居民电话列表', './resident', '/resident', '["GET"]', 1, 2, 1, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (12, '通过编号查找社区居民', 'getCommunityResidentById', '/resident/{id}', '["GET"]', 1, 2, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (13, '添加居民信息处理', 'communityResidentCreateHandle', '/resident', '["POST"]', 1, 2, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (14, '修改居民信息处理', 'communityResidentModifyHandle', '/resident/{id}', '["PUT"]', 1, 2, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (15, '删除居民信息', 'removeCommunityResident', '/resident/{id}', '["DELETE"]', 1, 2, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (16, '导入居民信息进系统', 'communityResidentImportAsSystem', '/resident/import/{streetId}', '["POST"]', 1, 2, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (17, '导出居民信息到Excel', 'communityResidentSaveAsExcel', '/resident/download', '["GET"]', 1, 2, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (18, '楼长信息列表', 'dormitoryManagerList', '/dormitory', '["GET"]', 1, 3, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (19, '通过编号查找社区楼长', 'getDormitoryManagerById', '/dormitory/{id}', '["GET"]', 1, 3, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (20, '楼长信息列表', './dormitory', '/dormitory', '["GET"]', 1, 3, 1, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (21, '添加楼长信息处理', 'dormitoryManagerCreateHandle', '/dormitory', '["POST"]', 1, 3, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (22, '修改楼长信息处理', 'dormitoryManagerModifyHandle', '/dormitory/{id}', '["PUT"]', 1, 3, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (23, '删除楼长信息', 'removeDormitoryManager', '/dormitory/{id}', '["DELETE"]', 1, 3, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (24, '导入楼长信息进系统', 'dormitoryManagerImportAsSystem', '/dormitory/import/{streetId}', '["GET"]', 1, 3, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (25, '导出楼长信息到Excel', 'dormitoryManagerSaveAsExcel', '/dormitory/download', '["GET"]', 1, 3, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (26, '单位列表', 'companyList', '/company', '["GET"]', 1, 4, 2, 0, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (27, '通过单位编号获取', 'getCompanyById', '/company/{id}', '["GET"]', 1, 4, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (28, '单位列表', './company', '/company', '["GET"]', 1, 4, 1, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (29, '添加单位信息处理', 'companyCreateHandle', '/company', '["POST"]', 1, 4, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (30, '修改单位信息处理', 'companyModifyHandle', '/company/{id}', '["PUT"]', 1, 4, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (31, '删除单位信息', 'removeCompany', '/company', '["DELETE"]', 1, 4, 2, 0, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (32, '系统用户列表', 'systemUserList', '/system/user', '["GET"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (33, '通过系统用户编号查找', 'getSystemUserById', '/system/user/{id}', '["GET"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (34, '系统用户列表', './user', '/system/user', '["GET"]', 1, 5, 1, 1, '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (35, '单独字段修改系统用户', 'systemUserModifyHandlePatch', '/system/user/{id}', '["PATCH"]', 1, 5, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (36, '添加处理系统用户', 'systemUserCreateHandle', '/system/user', '["POST"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (37, '修改处理系统用户', 'systemUserModifyHandle', '/system/user/{id}', '["PUT"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (38, '删除系统用户', 'removeSystemUser', '/system/user', '["DELETE"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (39, '通过单位编号加载系统用户', 'loadSystemUserByCompanyId', '/user/company/{companyId}', '["GET"]', 1, 5, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (40, '系统权限列表', 'systemSystemPermissionList', '/system/permission', '["GET"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (41, '通过编号获取系统权限', 'getSystemPermissionById', '/permission/{id}', '["GET"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (42, '系统权限列表', './permission', '/system/permission', '["GET"]', 1, 5, 1, 1, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (43, '添加处理系统权限', 'systemPermissionsCreateHandle', '/system/permission', '["POST"]', 1, 5, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (44, '修改处理系统权限', 'systemPermissionsModifyHandle', '/system/permission/{id}', '["PUT"]', 1, 5, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (45, '删除系统权限', 'removeSystemPermission', '/system/permission', '["DELETE"]', 1, 5, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (46, '获取系统用户单位拥有的权限', 'getPermissionsByCompanyId', '/system/permission/company/{companyId}', '["GET"]', 1, 5, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (47, '系统配置列表', 'systemConfigurationList', '/system/configuration', '["GET"]', 1, 6, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (48, '通过系统配置项编号查找', 'getConfigurationById', '/system/configuration/{id}', '["GET"]', 1, 6, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (49, '系统配置列表', './configuration', '/system/configuration', '["GET"]', 1, 6, 1, 1, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00'),
    (50, '添加处理系统配置', 'systemConfigurationCreateHandle', '/system/configuration', '["POST"]', 1, 6, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (51, '修改处理系统配置', 'systemConfigurationModifyHandle', '/system/configuration/{id}', '["PUT"]', 1, 6, 2, 0,
     '1000-01-01 00:00:00', '1000-01-01 00:00:00'),
    (52, '删除系统配置', 'removeConfigurationById', '/system/configuration', '["DELETE"]', 1, 6, 2, 0, '1000-01-01 00:00:00',
     '1000-01-01 00:00:00');