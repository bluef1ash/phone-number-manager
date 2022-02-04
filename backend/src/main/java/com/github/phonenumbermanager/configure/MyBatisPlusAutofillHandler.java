package com.github.phonenumbermanager.configure;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.phonenumbermanager.constant.SystemConstant;

/**
 * 时间自动填充处理
 *
 * @author 廿二月的天
 */
@Component
public class MyBatisPlusAutofillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "accountExpireTime", () -> SystemConstant.DATABASE_MAX_DATETIME,
            LocalDateTime.class);
        this.strictInsertFill(metaObject, "credentialExpireTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
