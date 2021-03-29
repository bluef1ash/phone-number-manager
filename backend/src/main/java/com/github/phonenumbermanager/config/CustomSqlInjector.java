package com.github.phonenumbermanager.config;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.github.phonenumbermanager.config.sqlinjector.InsertIgnore;
import com.github.phonenumbermanager.config.sqlinjector.InsertIgnoreBatchSomeColumn;

/**
 * SQL 自定义注入器
 *
 * @author 廿二月的天
 */
@Component
public class CustomSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn(tableFieldInfo -> !tableFieldInfo.isVersion()));
        methodList.add(new InsertIgnore());
        methodList.add(new InsertIgnoreBatchSomeColumn(tableFieldInfo -> !tableFieldInfo.isVersion()));
        return methodList;
    }
}
