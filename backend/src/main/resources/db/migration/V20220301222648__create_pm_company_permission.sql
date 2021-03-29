DROP TABLE IF EXISTS pm_company_permission;
CREATE TABLE pm_company_permission (
    company_id    BIGINT UNSIGNED      NOT NULL COMMENT '单位编号',
    permission_id BIGINT UNSIGNED      NOT NULL COMMENT '系统权限所属编号',
    create_time   DATETIME             NOT NULL COMMENT '增加时间',
    update_time   DATETIME             NOT NULL COMMENT '更新时间',
    version       SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (company_id, permission_id)
) COMMENT = '单位与系统权限中间表';
