DROP TABLE IF EXISTS pm_configuration;
CREATE TABLE pm_configuration (
    id          BIGINT UNSIGNED      NOT NULL COMMENT '配置编号',
    title       VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '配置标题',
    description VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '配置描述',
    name        VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '变量名',
    content     TEXT                 NOT NULL COMMENT '变量值',
    field_type  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '字段类型',
    field_value VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '字段类型值',
    order_by    TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME             NOT NULL COMMENT '增加时间',
    update_time DATETIME             NOT NULL COMMENT '更新时间',
    version     SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '系统配置表';
