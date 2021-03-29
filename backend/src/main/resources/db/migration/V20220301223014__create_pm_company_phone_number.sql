DROP TABLE IF EXISTS pm_company_phone_number;
CREATE TABLE pm_company_phone_number (
    company_id      BIGINT UNSIGNED      NOT NULL COMMENT '所属单位编号',
    phone_number_id BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time     DATETIME             NOT NULL COMMENT '增加时间',
    update_time     DATETIME             NOT NULL COMMENT '更新时间',
    version         SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (company_id, phone_number_id)
) COMMENT = '单位与联系方式中间表';
