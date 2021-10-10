-- -----------------------------------------------------
-- Schema phone_number
--
-- 电话管理系统
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `phone_number_manager` DEFAULT CHARACTER SET utf8mb4;
USE `phone_number_manager`;

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_phone_number`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_phone_number` (
    `phone_number` VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '联系方式；固定电话与移动电话',
    `source_type`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '联系方式的来源类型；0：居民，1：楼长，2：分包人，3：单位',
    `source_id`    VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '所属来源者编号',
    `phone_type`   TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '联系方式类型；0：未知，1：手机号码，2：固定电话号码',
    `create_time`  DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`  DATETIME             NOT NULL COMMENT '更新时间',
    `version`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`phone_number`, `source_type`, `source_id`),
    INDEX `idx_source_type_source_id`(`source_type` ASC, `source_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '联系方式表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_community_resident`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_community_resident` (
    `id`               BIGINT UNSIGNED      NOT NULL COMMENT '社区居民编号',
    `name`             VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '社区居民姓名',
    `address`          VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '居住地址',
    `company_id`       BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    `subcontractor_id` BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属社区分包人编号',
    `create_time`      DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`      DATETIME             NOT NULL COMMENT '更新时间',
    `version`          SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC) VISIBLE,
    INDEX `idx_company_id`(`company_id` ASC) VISIBLE,
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区居民信息表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_dormitory_manager`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_dormitory_manager` (
    `id`                VARCHAR(15)          NOT NULL COMMENT '社区楼片长编号',
    `name`              VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '楼片长姓名',
    `gender`            TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '性别；0：男，1：女，2：未知',
    `birth`             DATETIME             NOT NULL COMMENT '出生年月',
    `political_status`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '政治面貌；0：群众，1：共产党员，2：预备共产党员，3：共青团员，4：预备共青团员，5：其它',
    `employment_status` TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '工作状况；0：在职，1：退休，2：无业',
    `education`         TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '学历；0：文盲，1：小学，2：初中，3：中专，4：高中，5：大专，6：本科，7：硕士研究生，8：博士研究生',
    `address`           VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '家庭居住地址',
    `manager_address`   VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '管理的地址',
    `manager_count`     INT UNSIGNED         NOT NULL DEFAULT 0 COMMENT '管理的户数',
    `subcontractor_id`  BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区分包人编号',
    `company_id`        BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    `create_time`       DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`       DATETIME             NOT NULL COMMENT '更新时间',
    `version`           SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    INDEX `idx_company_id`(`company_id` ASC) VISIBLE,
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC) VISIBLE,
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区楼片长信息表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_subcontractor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_subcontractor` (
    `id`          BIGINT UNSIGNED      NOT NULL COMMENT '社区分包人编号',
    `name`        VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '分包人姓名',
    `company_id`  BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '分包人所属单位编号',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    INDEX `idx_company_id`(`company_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区分包人表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_company` (
    `id`            BIGINT UNSIGNED       NOT NULL COMMENT '单位编号',
    `name`          VARCHAR(10)           NOT NULL DEFAULT '' COMMENT '单位名称',
    `actual_number` MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT 0 COMMENT '辖区内人数',
    `parent_id`     BIGINT UNSIGNED       NOT NULL DEFAULT 0 COMMENT '上级所属编号',
    `create_time`   DATETIME              NOT NULL COMMENT '增加时间',
    `update_time`   DATETIME              NOT NULL COMMENT '更新时间',
    `version`       SMALLINT(5) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC) VISIBLE,
    INDEX `idx_parent_id`(`parent_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '单位信息表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_system_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_system_user` (
    `id`                     BIGINT UNSIGNED      NOT NULL COMMENT '系统用户编号',
    `username`               VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '系统用户名称',
    `password`               CHAR(88)             NOT NULL DEFAULT '' COMMENT '系统用户密码',
    `login_ip`               VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '最后一次登录的IP地址',
    `login_time`             DATETIME             NOT NULL COMMENT '最后一次登录的时间',
    `is_locked`              TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否已锁定；0：未锁定，1：已锁定',
    `is_enabled`             TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '是否已启用；0：已禁用，1：已启用',
    `account_expire_time`    DATETIME             NOT NULL COMMENT '账号过期时间',
    `credential_expire_time` DATETIME             NOT NULL COMMENT '凭证过期时间',
    `company_id`             BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属单位编号',
    `create_time`            DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`            DATETIME             NOT NULL COMMENT '更新时间',
    `version`                SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username`(`username` ASC) VISIBLE,
    INDEX `idx_company_id`(`company_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '系统用户表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_user_role` (
    `user_id`     BIGINT UNSIGNED      NOT NULL COMMENT '系统用户所属编号',
    `role_id`     BIGINT UNSIGNED      NOT NULL COMMENT '用户角色所属编号',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`user_id`, `role_id`)
)
    ENGINE = InnoDB
    COMMENT = '用户与角色中间表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_system_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_system_role` (
    `id`          BIGINT UNSIGNED      NOT NULL COMMENT '系统用户角色编号',
    `name`        VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '系统用户角色名称；“ROLE_”开头',
    `description` VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '系统用户角色描述',
    `parent_id`   BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级角色',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `role_name_UNIQUE`(`name` ASC) VISIBLE,
    INDEX `idx_parent_id`(`parent_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '系统用户角色表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_role_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_role_privilege` (
    `role_id`      BIGINT UNSIGNED      NOT NULL COMMENT '系统用户角色所属编号',
    `privilege_id` BIGINT UNSIGNED      NOT NULL COMMENT '系统用户权限所属编号',
    `create_time`  DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`  DATETIME             NOT NULL COMMENT '更新时间',
    `version`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`role_id`, `privilege_id`)
)
    ENGINE = InnoDB
    COMMENT = '用户角色与权限中间表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_system_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_system_privilege` (
    `id`          BIGINT UNSIGNED      NOT NULL COMMENT '系统用户权限编号',
    `name`        VARCHAR(30)          NOT NULL DEFAULT '' COMMENT '系统用户权限名称',
    `description` VARCHAR(200)         NOT NULL DEFAULT '' COMMENT '系统用户权限约束描述',
    `uri`         VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '访问URI地址',
    `method`      TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '方法类型；0：ALL，1：GET，2：HEAD，3：POST，4：PUT，5：PATCH，6：DELETE，7：OPTIONS，8：TRACE',
    `parent_id`   BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级权限',
    `icon_name`   VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '图标名称',
    `orders`      TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '菜单排序',
    `is_display`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否在菜单栏中显示；0：不显示，1：显示',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '系统用户权限表';

-- -----------------------------------------------------
-- Table `phone_number_manager`.`pm_configuration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number_manager`.`pm_configuration` (
    `key`            VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '系统配置项关键字名称',
    `type`           TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '系统配置项类型；0：未知类型，1：布尔类型，2：字符串类型，3：数值类型，4：系统用户类型',
    `value`          VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '配置内容',
    `description`    VARCHAR(60)          NOT NULL DEFAULT '' COMMENT '配置项描述',
    `key_is_changed` TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '系统配置项关键字名称是否允许更改；0：不允许，1：允许',
    `create_time`    DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`    DATETIME             NOT NULL COMMENT '更新时间',
    `version`        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`key`)
)
    ENGINE = InnoDB
    COMMENT = '系统配置表';
