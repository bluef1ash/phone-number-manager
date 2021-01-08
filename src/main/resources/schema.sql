SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema phone_number
-- -----------------------------------------------------
-- 电话管理系统

-- -----------------------------------------------------
-- Schema phone_number
--
-- 电话管理系统
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `phone_number` DEFAULT CHARACTER SET utf8;
USE `phone_number`;

-- -----------------------------------------------------
-- Table `phone_number`.`pm_community_resident`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_community_resident` (
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(10)     NOT NULL DEFAULT '' COMMENT '居民姓名',
    `address`          VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '住址',
    `phones`           VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '居民电话，共三个，用英文逗号分隔',
    `create_time`      DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`      DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `community_id`     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '社区所属ID',
    `subcontractor_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属社区分包人编号',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC),
    INDEX `idx_community_id`(`community_id` ASC),
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '社区居民信息表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_system_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_system_user` (
    `id`           BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT,
    `username`     VARCHAR(10)         NOT NULL DEFAULT '' COMMENT '用户名称',
    `password`     CHAR(88)            NOT NULL DEFAULT '' COMMENT '用户密码',
    `login_ip`     VARCHAR(15)         NOT NULL DEFAULT '' COMMENT '登录IP',
    `login_time`   DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '登录时间',
    `is_locked`    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已锁定，0未锁定，1已锁定',
    `create_time`  DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`  DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `company_type` TINYINT(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单位类型级别，0默认全局级别，1社区级别，2街道级别',
    `company_id`   BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '单位编号',
    `role_id`      BIGINT UNSIGNED     NOT NULL COMMENT '角色所属ID',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username`(`username` ASC),
    INDEX `idx_company_id`(`company_id` ASC),
    INDEX `idx_role_id`(`role_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '系统用户表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_community`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_community` (
    `id`                  BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT,
    `name`                VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '社区名称',
    `landline`            VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '联系电话',
    `actual_number`       SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '指标',
    `dormitory_submitted` TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '社区楼长是否已提交',
    `resident_submitted`  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '社区居民是否已提交',
    `create_time`         DATETIME(0)          NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`         DATETIME(0)          NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `subdistrict_id`      BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '街道办事处所属ID',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC),
    INDEX `idx_subdistrict_id`(`subdistrict_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '社区表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_subdistrict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_subdistrict` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(10)     NOT NULL DEFAULT '' COMMENT '街道办事处名称',
    `landline`    VARCHAR(15)     NOT NULL DEFAULT '' COMMENT '联系电话',
    `create_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC)
)
    ENGINE = InnoDB
    COMMENT = '街道办事处表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_role` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(20)     NOT NULL DEFAULT '' COMMENT '角色名称',
    `description` VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '角色描述',
    `create_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `parent_id`   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '上级角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `role_name_UNIQUE`(`name` ASC),
    INDEX `idx_parent_id`(`parent_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '角色表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_privilege` (
    `id`              BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT,
    `name`            VARCHAR(30)         NOT NULL DEFAULT '' COMMENT '权限名称',
    `constraint_auth` VARCHAR(50)         NOT NULL DEFAULT '' COMMENT '约束名称',
    `uri`             VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '访问URI地址',
    `parent_id`       BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '上级权限',
    `icon_name`       VARCHAR(50)         NOT NULL DEFAULT '' COMMENT '图标名称',
    `orders`          TINYINT(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单排序',
    `is_display`      TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否在菜单栏中显示，0不显示，1显示',
    `create_time`     DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`     DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_name`(`name` ASC),
    UNIQUE INDEX `uk_constraint_auth`(`constraint_auth` ASC)
)
    ENGINE = InnoDB
    COMMENT = '用户权限表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_role_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_role_privilege` (
    `role_id`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色所属ID',
    `privilege_id` BIGINT UNSIGNED NOT NULL COMMENT '权限所属ID',
    `create_time`  DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`  DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    PRIMARY KEY (`role_id`, `privilege_id`),
    INDEX `idx_privilege_id`(`privilege_id` ASC),
    INDEX `idx_role_id`(`role_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '用户角色与权限中间表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_article`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_article` (
    `id`             BIGINT UNSIGNED                   NOT NULL AUTO_INCREMENT,
    `title`          VARCHAR(30)                       NOT NULL DEFAULT '' COMMENT '文章标题',
    `content`        VARCHAR(500) CHARACTER SET 'big5' NOT NULL DEFAULT '' COMMENT '文章内容',
    `create_time`    DATETIME(0)                       NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`    DATETIME(0)                       NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `system_user_id` BIGINT UNSIGNED                   NOT NULL COMMENT '系统用户所属编号',
    `category_id`    BIGINT UNSIGNED                   NOT NULL COMMENT '分类编号',
    PRIMARY KEY (`id`),
    INDEX `idx_category_id`(`category_id` ASC),
    INDEX `idx_system_user_id`(`system_user_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '系统文章表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_category` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(15)     NOT NULL DEFAULT '' COMMENT '分类名称',
    `create_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time` DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `parent_id`   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父类编号',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id`(`parent_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '文章分类表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_configuration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_configuration` (
    `key`            VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '系统配置项关键字名称',
    `type`           TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '系统配置项类型，0未知类型，1布尔类型，2字符串类型，3数值类型，4系统用户类型',
    `value`          VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '配置内容',
    `description`    VARCHAR(60)         NOT NULL DEFAULT '' COMMENT '配置项描述',
    `key_is_changed` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '系统配置项关键字名称是否允许更改。0不允许，1允许',
    `create_time`    DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`    DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    PRIMARY KEY (`key`)
)
    ENGINE = InnoDB
    COMMENT = '系统配置表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_subcontractor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_subcontractor` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`         VARCHAR(10)     NOT NULL DEFAULT '' COMMENT '分包人姓名',
    `mobile`       VARCHAR(15)     NOT NULL DEFAULT '' COMMENT '分包人联系方式',
    `create_time`  DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`  DATETIME(0)     NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `community_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '分包人所属社区编号',
    PRIMARY KEY (`id`),
    INDEX `idx_community_id`(`community_id` ASC)
)
    ENGINE = InnoDB
    COMMENT = '社区分包人表';


-- -----------------------------------------------------
-- Table `phone_number`.`pm_dormitory_manager`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone_number`.`pm_dormitory_manager` (
    `id`               VARCHAR(15)         NOT NULL COMMENT '编号',
    `name`             VARCHAR(10)         NOT NULL DEFAULT '' COMMENT '楼片长姓名',
    `sex`              TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '性别，0男，1女，2未知',
    `birth`            DATE                NOT NULL DEFAULT '0000-01-01' COMMENT '出生年月',
    `political_status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '政治面貌，0群众，1共产党员，2预备共产党员，3共青团员，4预备共青团员，5其它',
    `work_status`      TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '工作状况，0在职，1退休，2无业',
    `education`        TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '学历，0文盲，1小学，2初中，3中专，4高中，5大专，6本科，7硕士研究生，8博士研究生',
    `address`          VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '家庭住址',
    `manager_address`  VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '管理的地址',
    `manager_count`    INT UNSIGNED        NOT NULL DEFAULT 0 COMMENT '管理的户数',
    `phones`           VARCHAR(50)         NOT NULL DEFAULT '' COMMENT '联系方式，固定电话与移动电话用英文逗号分隔',
    `create_time`      DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '增加时间',
    `update_time`      DATETIME(0)         NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '更新时间',
    `subcontractor_id` BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '社区分包人编号',
    `community_id`     BIGINT UNSIGNED     NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    PRIMARY KEY (`id`),
    INDEX `idx_community_id`(`community_id` ASC),
    INDEX `idx_subcontractor_id`(`subcontractor_id` ASC),
    UNIQUE INDEX `uk_name_address`(`name` ASC, `address` ASC)
)
    ENGINE = InnoDB
    COMMENT = '楼片长信息表';


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
