DROP TABLE IF EXISTS pm_company;
CREATE TABLE pm_company (
    id          BIGINT UNSIGNED      NOT NULL COMMENT '单位编号',
    name        VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '单位名称',
    parent_id   BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级所属编号',
    create_time DATETIME             NOT NULL COMMENT '增加时间',
    update_time DATETIME             NOT NULL COMMENT '更新时间',
    version     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '单位信息表';


CREATE UNIQUE INDEX uk_name ON pm_company(name);
CREATE INDEX idx_parent_id ON pm_company(parent_id);
