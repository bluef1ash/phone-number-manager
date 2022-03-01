DROP TABLE IF EXISTS pm_system_user;
CREATE TABLE pm_system_user (
    id                     BIGINT UNSIGNED      NOT NULL COMMENT '系统用户编号',
    username               VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '系统用户名称',
    password               CHAR(88)             NOT NULL DEFAULT '' COMMENT '系统用户密码',
    positions              VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '系统用户职务;允许多个',
    titles                 VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '系统用户职称;允许多个',
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
