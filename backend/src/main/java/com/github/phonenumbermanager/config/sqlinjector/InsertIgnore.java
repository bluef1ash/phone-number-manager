package com.github.phonenumbermanager.config.sqlinjector;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

/**
 * 插入时忽略重复数据
 *
 * @author 廿二月的天
 */
public class InsertIgnore extends AbstractMethod {

    public InsertIgnore() {
        super("insertIgnore");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String columnScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null), LEFT_BRACKET,
            RIGHT_BRACKET, null, COMMA);
        String valuesScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null), LEFT_BRACKET,
            RIGHT_BRACKET, null, COMMA);
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            setKeysProperty(tableInfo);
        }
        String sql = String.format("<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>", tableInfo.getTableName(),
            columnScript, valuesScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty,
            keyColumn);
    }
}
