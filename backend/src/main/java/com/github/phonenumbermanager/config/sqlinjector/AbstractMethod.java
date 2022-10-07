package com.github.phonenumbermanager.config.sqlinjector;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

/**
 * 自定义 SQL 语句配置
 *
 * @author 廿二月的天
 */
public abstract class AbstractMethod extends com.baomidou.mybatisplus.core.injector.AbstractMethod {

    protected KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
    protected String keyProperty = null;
    protected String keyColumn = null;

    protected AbstractMethod(String methodName) {
        super(methodName);
    }

    /**
     * 设置数据表键值属性
     *
     * @param tableInfo
     *            数据表信息
     */
    protected void setKeysProperty(TableInfo tableInfo) {
        if (tableInfo.getIdType() == IdType.AUTO) {
            keyGenerator = Jdbc3KeyGenerator.INSTANCE;
            keyProperty = tableInfo.getKeyProperty();
            keyColumn = tableInfo.getKeyColumn();
        } else {
            if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }
    }
}
