DROP TABLE IF EXISTS pm_community_resident;
CREATE TABLE pm_community_resident (
    id               BIGINT UNSIGNED      NOT NULL COMMENT '社区居民编号',
    name             VARCHAR(20)          NOT NULL DEFAULT '' COMMENT '社区居民姓名',
    address          VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '居住地址',
    company_id       BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    subcontractor_id BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属社区分包人编号',
    create_time      DATETIME             NOT NULL COMMENT '增加时间',
    update_time      DATETIME             NOT NULL COMMENT '更新时间',
    version          SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '社区居民信息表';


CREATE INDEX idx_company_id ON pm_community_resident(company_id);
CREATE INDEX idx_subcontractor_id ON pm_community_resident(subcontractor_id);
