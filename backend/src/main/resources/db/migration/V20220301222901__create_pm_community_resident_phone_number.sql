DROP TABLE IF EXISTS pm_community_resident_phone_number;
CREATE TABLE pm_community_resident_phone_number (
    community_resident_id BIGINT UNSIGNED      NOT NULL COMMENT '所属社区居民编号',
    phone_number_id       BIGINT UNSIGNED      NOT NULL COMMENT '所属联系方式编号',
    create_time           DATETIME             NOT NULL COMMENT '增加时间',
    update_time           DATETIME             NOT NULL COMMENT '更新时间',
    version               SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (community_resident_id, phone_number_id)
) COMMENT = '社区居民信息与联系方式中间表';
