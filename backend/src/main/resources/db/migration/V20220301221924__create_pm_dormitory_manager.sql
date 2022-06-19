DROP TABLE IF EXISTS pm_dormitory_manager;
CREATE TABLE pm_dormitory_manager (
    id                BIGINT UNSIGNED      NOT NULL COMMENT '社区楼片长编号',
    name              VARCHAR(10)          NOT NULL DEFAULT '' COMMENT '楼片长姓名',
    id_number         CHAR(18)             NOT NULL DEFAULT '' COMMENT '身份证号码',
    gender            TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '性别;0：女，1：男，2：未知',
    birth             DATE                 NOT NULL COMMENT '出生年月',
    political_status  TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '政治面貌;0：群众，1：共产党员，2：预备共产党员，3：共青团员，4：预备共青团员，5：其它',
    employment_status TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '工作状况;0：在职，1：退休，2：无业',
    education         TINYINT(1) UNSIGNED  NOT NULL DEFAULT 0 COMMENT '学历;0：文盲，1：小学，2：初中，3：中专，4：高中，5：大专，6：本科，7：硕士研究生，8：博士研究生',
    address           VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '居住地址',
    manager_address   VARCHAR(255)         NOT NULL DEFAULT '' COMMENT '管理的地址',
    manager_count     INT UNSIGNED         NOT NULL DEFAULT 0 COMMENT '管理的户数',
    company_id        BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '社区所属编号',
    subcontractor_id  BIGINT UNSIGNED      NOT NULL DEFAULT 0 COMMENT '所属分包人编号',
    create_time       DATETIME             NOT NULL COMMENT '增加时间',
    update_time       DATETIME             NOT NULL COMMENT '更新时间',
    version           SMALLINT(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id)
) COMMENT = '社区楼片长信息表';


CREATE UNIQUE INDEX uk_id_number ON pm_dormitory_manager(id_number);
CREATE INDEX idx_company_id ON pm_dormitory_manager(company_id);
CREATE INDEX idx_subcontractor_id ON pm_dormitory_manager(subcontractor_id);
