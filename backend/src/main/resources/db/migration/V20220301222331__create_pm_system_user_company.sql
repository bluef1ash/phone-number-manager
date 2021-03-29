DROP TABLE IF EXISTS pm_system_user_company;
CREATE TABLE pm_system_user_company (
    system_user_id BIGINT UNSIGNED      NOT NULL COMMENT '所属系统用户编号',
    company_id     BIGINT UNSIGNED      NOT NULL COMMENT '所属单位编号',
    create_time    DATETIME             NOT NULL COMMENT '增加时间',
    update_time    DATETIME             NOT NULL COMMENT '更新时间',
    version        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (system_user_id, company_id)
) COMMENT = '系统用户与单位中间表';
