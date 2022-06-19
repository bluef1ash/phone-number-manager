DROP TABLE IF EXISTS pm_subcontractor_phone_number;
CREATE TABLE pm_subcontractor_phone_number (
    subcontractor_id BIGINT UNSIGNED      NOT NULL COMMENT '所属社区分包人编号',
    phone_number_id  BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time      DATETIME             NOT NULL COMMENT '增加时间',
    update_time      DATETIME             NOT NULL COMMENT '更新时间',
    version          SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (subcontractor_id, phone_number_id)
) COMMENT = '社区分包人员与联系方式关系表';
