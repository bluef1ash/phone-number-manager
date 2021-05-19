SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema phone_number
-- -----------------------------------------------------
-- 电话管理系统

-- -----------------------------------------------------
-- Schema phone_number
--
-- 电话管理系统
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `phone_number` DEFAULT CHARACTER SET utf8mb4;
USE `phone_number`;

-- -----------------------------------------------------
-- Table `phone_number`.`pm_community_resident`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_community_resident` (
    `id`               BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '居民姓名',
    `address`          VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '住址',
    `create_time`      DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`      DATETIME             NOT NULL COMMENT '更新时间',
    `version`          SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    `community_id`     BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属ID',
    `subcontractor_id` BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属社区分包人编号',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC) VISIBLE,
    INDEX `idx_community_id`(`community_id` ASC) VISIBLE,
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区居民信息表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_system_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_system_user` (
    `id`                     BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `username`               VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '用户名称',
    `password`               CHAR(88)             NOT NULL DEFAULT '' COMMENT '用户密码',
    `login_ip`               VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '登录IP',
    `login_time`             DATETIME             NOT NULL COMMENT '登录时间',
    `is_locked`              TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否已锁定，0未锁定，1已锁定',
    `is_enabled`             TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '是否禁用用户',
    `account_expire_time`    DATETIME             NOT NULL COMMENT '账号过期时间',
    `credential_expire_time` DATETIME             NOT NULL COMMENT '凭证过期时间',
    `create_time`            DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`            DATETIME             NOT NULL COMMENT '更新时间',
    `level`                  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '单位级别，0默认全局级别，1社区级别，2街道级别',
    `version`                SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    `company_id`             BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '单位编号',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username`(`username` ASC) VISIBLE,
    INDEX `idx_company_id`(`company_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '系统用户表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_community`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_community` (
    `id`                  BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT,
    `name`                VARCHAR(10)           NOT NULL DEFAULT '' COMMENT '社区名称',
    `actual_number`       MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT 0 COMMENT '辖区内人数',
    `dormitory_submitted` TINYINT(1) UNSIGNED   NOT NULL DEFAULT 0 COMMENT '社区楼长是否已提交',
    `resident_submitted`  TINYINT(1) UNSIGNED   NOT NULL DEFAULT 0 COMMENT '社区居民是否已提交',
    `create_time`         DATETIME              NOT NULL COMMENT '增加时间',
    `update_time`         DATETIME              NOT NULL COMMENT '更新时间',
    `version`             SMALLINT(5) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '数据版本',
    `subdistrict_id`      BIGINT UNSIGNED       NOT NULL DEFAULT 0 COMMENT '街道办事处所属ID',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC) VISIBLE,
    INDEX `idx_subdistrict_id`(`subdistrict_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_subdistrict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_subdistrict` (
    `id`          BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '街道办事处名称',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '街道办事处表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_role` (
    `id`          BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '角色名称',
    `description` VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '角色描述',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    `parent_id`   BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `role_name_UNIQUE`(`name` ASC) VISIBLE,
    INDEX `idx_parent_id`(`parent_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '角色表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_privilege` (
    `id`              BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`            VARCHAR(30)          NOT NULL DEFAULT '' COMMENT '权限名称',
    `constraint_auth` VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '约束名称',
    `uri`             VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '访问URI地址',
    `parent_id`       BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级权限',
    `icon_name`       VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '图标名称',
    `orders`          TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '菜单排序',
    `is_display`      TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否在菜单栏中显示，0不显示，1显示',
    `create_time`     DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`     DATETIME             NOT NULL COMMENT '更新时间',
    `version`         SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC) VISIBLE,
    UNIQUE INDEX `uk_constraint_auth`(`constraint_auth` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '用户权限表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_role_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_role_privilege` (
    `role_id`      BIGINT UNSIGNED      NOT NULL COMMENT '角色所属编号',
    `privilege_id` BIGINT UNSIGNED      NOT NULL COMMENT '权限所属编号',
    `create_time`  DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`  DATETIME             NOT NULL COMMENT '更新时间',
    `version`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`role_id`, `privilege_id`)
)
    ENGINE = InnoDB
    COMMENT = '用户角色与权限中间表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_configuration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_configuration` (
    `key`            VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '系统配置项关键字名称',
    `type`           TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '系统配置项类型，0未知类型，1布尔类型，2字符串类型，3数值类型，4系统用户类型',
    `value`          VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '配置内容',
    `description`    VARCHAR(60)          NOT NULL DEFAULT '' COMMENT '配置项描述',
    `key_is_changed` TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '系统配置项关键字名称是否允许更改。0不允许，1允许',
    `create_time`    DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`    DATETIME             NOT NULL COMMENT '更新时间',
    `version`        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`key`)
)
    ENGINE = InnoDB
    COMMENT = '系统配置表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_subcontractor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_subcontractor` (
    `id`           BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`         VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '分包人姓名',
    `create_time`  DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`  DATETIME             NOT NULL COMMENT '更新时间',
    `version`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    `community_id` BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '分包人所属社区编号',
    PRIMARY KEY (`id`),
    INDEX `idx_community_id`(`community_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '社区分包人表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_dormitory_manager`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_dormitory_manager` (
    `id`                VARCHAR(15)          NOT NULL COMMENT '编号',
    `name`              VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '楼片长姓名',
    `gender`            TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '性别，0男，1女，2未知',
    `birth`             DATETIME             NOT NULL COMMENT '出生年月',
    `political_status`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '政治面貌，0群众，1共产党员，2预备共产党员，3共青团员，4预备共青团员，5其它',
    `employment_status` TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '工作状况，0在职，1退休，2无业',
    `education`         TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '学历，0文盲，1小学，2初中，3中专，4高中，5大专，6本科，7硕士研究生，8博士研究生',
    `address`           VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '家庭住址',
    `manager_address`   VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '管理的地址',
    `manager_count`     INT UNSIGNED         NOT NULL DEFAULT 0 COMMENT '管理的户数',
    `create_time`       DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`       DATETIME             NOT NULL COMMENT '更新时间',
    `version`           SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    `subcontractor_id`  BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区分包人编号',
    `community_id`      BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    PRIMARY KEY (`id`),
    INDEX `idx_community_id`(`community_id` ASC) VISIBLE,
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC) VISIBLE,
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '楼片长信息表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_phone_number`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_phone_number` (
    `phone_number` VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '联系方式，固定电话与移动电话',
    `source_type`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '联系方式的来源类型，0为居民，1为楼长，2为分包人，3为社区，4为街道',
    `source_id`    VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '所属来源者编号',
    `phone_type`   TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '联系方式类型，0未知，1手机号码，2固定电话号码',
    `create_time`  DATETIME             NOT NULL COMMENT '增加时间',
    `update_time`  DATETIME             NOT NULL COMMENT '更新时间',
    `version`      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`phone_number`, `source_type`, `source_id`),
    INDEX `idx_source_type_source_id`(`source_type` ASC, `source_id` ASC) VISIBLE
)
    ENGINE = InnoDB
    COMMENT = '联系方式表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_user_role` (
    `user_id`     BIGINT UNSIGNED      NOT NULL COMMENT '系统用户所属编号',
    `role_id`     BIGINT UNSIGNED      NOT NULL COMMENT '用户角色所属编号',
    `create_time` DATETIME             NOT NULL COMMENT '增加时间',
    `update_time` DATETIME             NOT NULL COMMENT '更新时间',
    `version`     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本',
    PRIMARY KEY (`user_id`, `role_id`)
)
    ENGINE = InnoDB
    COMMENT = '用户与角色中间表';


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
