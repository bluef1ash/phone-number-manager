DROP TABLE IF EXISTS pm_company_extra;
CREATE TABLE pm_company_extra (
    id          BIGINT UNSIGNED      NOT NULL COMMENT '编号',
    title       VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '单位额外属性标题',
    description VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '单位额外属性描述',
    name        VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '变量名',
    content     TEXT(255)            NOT NULL COMMENT '变量值',
    field_type  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '字段类型',
    company_id  BIGINT UNSIGNED      NOT NULL COMMENT '所属单位编号',
    create_time DATETIME             NOT NULL COMMENT '增加时间',
    update_time DATETIME             NOT NULL COMMENT '更新时间',
    version     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '单位额外属性表';


CREATE INDEX idx_company_id ON pm_company_extra(company_id);

