DROP TABLE IF EXISTS pm_subcontractor;
CREATE TABLE pm_subcontractor (
    id             BIGINT UNSIGNED      NOT NULL COMMENT '社区分包人员编号',
    name           VARCHAR(30)          NOT NULL DEFAULT '' COMMENT '社区分包人员姓名',
    id_card_number CHAR(18)             NOT NULL DEFAULT '' COMMENT '社区分包人员身份证号码',
    positions      VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '社区分包人员职务;允许多个',
    titles         VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '社区分包人员职称;允许多个',
    company_id     BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属单位编号',
    create_time    DATETIME             NOT NULL COMMENT '增加时间',
    update_time    DATETIME             NOT NULL COMMENT '更新时间',
    version        SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '社区分包人员表';

CREATE UNIQUE INDEX uk_id_card_number ON pm_subcontractor(id_card_number);
