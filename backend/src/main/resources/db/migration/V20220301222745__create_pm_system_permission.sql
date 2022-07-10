DROP TABLE IF EXISTS pm_system_permission;
CREATE TABLE pm_system_permission (
    id            BIGINT UNSIGNED      NOT NULL COMMENT '系统用户权限编号',
    name          VARCHAR(30)          NOT NULL DEFAULT '' COMMENT '系统用户权限名称',
    function_name VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '系统用户权限约束描述',
    uri           VARCHAR(100)         NOT NULL DEFAULT '' COMMENT '访问URI地址',
    http_methods  VARCHAR(50)          NOT NULL DEFAULT '' COMMENT '方法类型;可有多个',
    parent_id     BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '上级系统用户权限编号',
    order_by      TINYINT(3) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '菜单排序',
    menu_type     TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '菜单类型;0：全部，1：前端，2：后端',
    is_display    TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '是否在菜单栏中显示;0：不显示，1：显示',
    create_time   DATETIME             NOT NULL COMMENT '增加时间',
    update_time   DATETIME             NOT NULL COMMENT '更新时间',
    version       SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '系统用户权限表';
