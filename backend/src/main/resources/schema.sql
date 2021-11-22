CREATE SCHEMA IF NOT EXISTS `phone_number_manager` DEFAULT CHARACTER SET utf8mb4;
USE `phone_number_manager`;


DROP TABLE IF EXISTS pm_phone_number;
CREATE TABLE pm_phone_number (
    id           BIGINT UNSIGNED      NOT NULL COMMENT '联系方式编号',
    phone_number VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '联系方式号码;固定电话与移动电话',
    phone_type   TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '联系方式类型;0：未知，1：手机号码，2：固定电话号码',
    create_time  DATETIME             NOT NULL COMMENT '增加时间',
    update_time  DATETIME             NOT NULL COMMENT '更新时间',
    version      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '联系方式表';


CREATE INDEX idx_phone_number_type ON pm_phone_number(phone_number, phone_type);

DROP TABLE IF EXISTS pm_community_resident;
CREATE TABLE pm_community_resident (
    id             BIGINT UNSIGNED      NOT NULL COMMENT '社区居民编号',
    name           VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '社区居民姓名',
    address        VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '居住地址',
    company_id     BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    system_user_id BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属社区分包人编号',
    create_time    DATETIME             NOT NULL COMMENT '增加时间',
    update_time    DATETIME             NOT NULL COMMENT '更新时间',
    version        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '社区居民信息表';


CREATE UNIQUE INDEX uk_name_address ON pm_community_resident(name, address);
CREATE INDEX idx_company_id ON pm_community_resident(company_id);
CREATE INDEX idx_subcontractor_id ON pm_community_resident(system_user_id);

DROP TABLE IF EXISTS pm_dormitory_manager;
CREATE TABLE pm_dormitory_manager (
    id                BIGINT UNSIGNED      NOT NULL COMMENT '社区楼片长编号',
    name              VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '楼片长姓名',
    gender            TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '性别;0:男，1:女，2:未知',
    birth             DATE                 NOT NULL COMMENT '出生年月',
    political_status  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '政治面貌;0：群众，1：共产党员，2：预备共产党员，3：共青团员，4：预备共青团员，5：其它',
    employment_status TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '工作状况;0：在职，1：退休，2：无业',
    education         TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '学历;0：文盲，1：小学，2：初中，3：中专，4：高中，5：大专，6：本科，7：硕士研究生，8：博士研究生',
    address           VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '居住地址',
    manager_address   VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '管理的地址',
    manager_count     INT UNSIGNED         NOT NULL DEFAULT 0 COMMENT '管理的户数',
    company_id        BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    system_user_id    BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属分包人编号',
    create_time       DATETIME             NOT NULL COMMENT '增加时间',
    update_time       DATETIME             NOT NULL COMMENT '更新时间',
    version           SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '社区楼片长信息表';


CREATE UNIQUE INDEX uk_name_address ON pm_dormitory_manager(name, address);
CREATE INDEX idx_company_id ON pm_dormitory_manager(company_id);
CREATE INDEX idx_system_user_id ON pm_dormitory_manager(system_user_id);

DROP TABLE IF EXISTS pm_company;
CREATE TABLE pm_company (
    id            BIGINT UNSIGNED       NOT NULL COMMENT '单位编号',
    name          VARCHAR(10)           NOT NULL DEFAULT '' COMMENT '单位名称',
    actual_number MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT 0 COMMENT '辖区内人数',
    level         TINYINT(2) UNSIGNED   NOT NULL DEFAULT 0 COMMENT '单位层级;0：最底层，依次递增',
    parent_id     BIGINT UNSIGNED       NOT NULL DEFAULT 0 COMMENT '上级所属编号',
    create_time   DATETIME              NOT NULL COMMENT '增加时间',
    update_time   DATETIME              NOT NULL COMMENT '更新时间',
    version       SMALLINT(5) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '单位信息表';


CREATE UNIQUE INDEX uk_name ON pm_company(name);
CREATE INDEX idx_parent_id ON pm_company(parent_id);

DROP TABLE IF EXISTS pm_system_user_company;
CREATE TABLE pm_system_user_company (
    system_user_id BIGINT UNSIGNED      NOT NULL COMMENT '所属系统用户编号',
    company_id     BIGINT UNSIGNED      NOT NULL COMMENT '所属单位编号',
    create_time    DATETIME             NOT NULL COMMENT '增加时间',
    update_time    DATETIME             NOT NULL COMMENT '更新时间',
    version        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (system_user_id, company_id)
) COMMENT = '系统用户与单位中间表';

DROP TABLE IF EXISTS pm_system_user;
CREATE TABLE pm_system_user (
    id                     BIGINT UNSIGNED      NOT NULL COMMENT '系统用户编号',
    username               VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '系统用户名称',
    password               CHAR(88)             NOT NULL DEFAULT '' COMMENT '系统用户密码',
    position               VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '系统用户职务',
    login_ip               VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '最后一次登录的IP地址',
    login_time             DATETIME             NOT NULL COMMENT '最后一次登录的时间',
    is_locked              TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否已锁定;0：未锁定，1：已锁定',
    is_enabled             TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '是否已启用;0：已禁用，1：已启用',
    account_expire_time    DATETIME             NOT NULL COMMENT '账号过期时间',
    credential_expire_time DATETIME             NOT NULL COMMENT '凭证过期时间',
    is_subcontract         TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否参与分包;0：不参加分包，1：参加分包',
    phone_number_id        BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属联系方式编号',
    create_time            DATETIME             NOT NULL COMMENT '增加时间',
    update_time            DATETIME             NOT NULL COMMENT '更新时间',
    version                SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '系统用户表';


CREATE INDEX idx_phone_number_id ON pm_system_user(phone_number_id);

DROP TABLE IF EXISTS pm_company_permission;
CREATE TABLE pm_company_permission (
    company_id    BIGINT UNSIGNED      NOT NULL COMMENT '单位编号',
    permission_id BIGINT UNSIGNED      NOT NULL COMMENT '系统权限所属编号',
    create_time   DATETIME             NOT NULL COMMENT '增加时间',
    update_time   DATETIME             NOT NULL COMMENT '更新时间',
    version       SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (company_id, permission_id)
) COMMENT = '单位与系统权限中间表';

DROP TABLE IF EXISTS pm_system_permission;
CREATE TABLE pm_system_permission (
    id            BIGINT UNSIGNED      NOT NULL COMMENT '系统用户权限编号',
    name          VARCHAR(30)          NOT NULL DEFAULT '' COMMENT '系统用户权限名称',
    function_name VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '系统用户权限约束描述',
    uri           VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '访问URI地址',
    http_method   VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '方法类型;可有多个，英文逗号分隔，0：ALL，1：GET，2：HEAD，3：POST，4：PUT，5：PATCH，6：DELETE，7：OPTIONS，8：TRACE',
    level         TINYINT(2) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '系统用户权限层级;0：最顶层，依次递增',
    parent_id     BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级系统用户权限编号',
    icon_name     VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '图标名称',
    order_by      TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '菜单排序',
    is_display    TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否在菜单栏中显示;0：不显示，1：显示',
    create_time   DATETIME             NOT NULL COMMENT '增加时间',
    update_time   DATETIME             NOT NULL COMMENT '更新时间',
    version       SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '系统用户权限表';


CREATE UNIQUE INDEX uk_name ON pm_system_permission(name);
CREATE UNIQUE INDEX uk_uri_method ON pm_system_permission(uri, http_method);

DROP TABLE IF EXISTS pm_community_resident_phone_number;
CREATE TABLE pm_community_resident_phone_number (
    community_resident_id BIGINT UNSIGNED      NOT NULL COMMENT '所属社区居民编号',
    phone_number_id       BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time           DATETIME             NOT NULL COMMENT '增加时间',
    update_time           DATETIME             NOT NULL COMMENT '更新时间',
    version               SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (community_resident_id, phone_number_id)
) COMMENT = '社区居民信息与联系方式中间表';

DROP TABLE IF EXISTS pm_company_phone_number;
CREATE TABLE pm_company_phone_number (
    company_id      BIGINT UNSIGNED      NOT NULL COMMENT '所属单位编号',
    phone_number_id BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time     DATETIME             NOT NULL COMMENT '增加时间',
    update_time     DATETIME             NOT NULL COMMENT '更新时间',
    version         SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (company_id, phone_number_id)
) COMMENT = '单位与联系方式中间表';

DROP TABLE IF EXISTS pm_dormitory_manager_phone_number;
CREATE TABLE pm_dormitory_manager_phone_number (
    dormitory_manager_id BIGINT UNSIGNED      NOT NULL COMMENT '所属楼片长信息编号',
    phone_number_id      BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time          DATETIME             NOT NULL COMMENT '增加时间',
    update_time          DATETIME             NOT NULL COMMENT '更新时间',
    version              SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (dormitory_manager_id, phone_number_id)
) COMMENT = '楼片长信息与联系方式中间表';

DROP TABLE IF EXISTS pm_configuration;
CREATE TABLE pm_configuration (
    id          BIGINT UNSIGNED      NOT NULL COMMENT '配置编号',
    title       VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '配置标题',
    description VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '配置描述',
    name        VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '变量名',
    content     TEXT(255)            NOT NULL COMMENT '变量值',
    field_type  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '字段类型',
    field_value VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '字段类型值',
    order_by    TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME             NOT NULL COMMENT '增加时间',
    update_time DATETIME             NOT NULL COMMENT '更新时间',
    version     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '系统配置表';

