DROP TABLE IF EXISTS pm_phone_number;
CREATE TABLE pm_phone_number (
    id           BIGINT UNSIGNED      NOT NULL COMMENT '联系方式编号',
    phone_number VARCHAR(15)          NOT NULL DEFAULT '' COMMENT '联系方式号码;固定电话与移动电话',
    phone_type   TINYINT(1) UNSIGNED  NOT NULL DEFAULT 1 COMMENT '联系方式类型;0：未知，1：手机号码，2：固定电话号码',
    create_time  DATETIME             NOT NULL COMMENT '增加时间',
    update_time  DATETIME             NOT NULL COMMENT '更新时间',
    version      SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '联系方式表';

CREATE INDEX idx_phone_number_type ON pm_phone_number(phone_number, phone_type);
CREATE UNIQUE INDEX uk_phone_number_phone_number ON pm_phone_number(phone_number);
