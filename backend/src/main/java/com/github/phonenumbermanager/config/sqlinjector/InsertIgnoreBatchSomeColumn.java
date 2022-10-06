package com.github.phonenumbermanager.config.sqlinjector;

import java.util.List;
import java.util.function.Predicate;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

/**
 * 批量插入时忽略重复数据
 *
 * @author 廿二月的天
 */
public class InsertIgnoreBatchSomeColumn extends AbstractMethod {
    private final Predicate<TableFieldInfo> predicate;

    public InsertIgnoreBatchSomeColumn(Predicate<TableFieldInfo> predicate) {
        super("insertIgnoreBatchSomeColumn");
        this.predicate = predicate;
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(true, false)
            + filterTableFieldInfo(fieldList, predicate, TableFieldInfo::getInsertSqlColumn, "");
        String columnScript = "(" + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + ")";
        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(true, "et.", false)
            + filterTableFieldInfo(fieldList, predicate, (i) -> i.getInsertSqlProperty("et."), "");
        insertSqlProperty = "(" + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + ")";
        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", null, "et", ",");
        if (tableInfo.havePK()) {
            setKeysProperty(tableInfo);
        }
        String sql = String.format("<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>", tableInfo.getTableName(),
            columnScript, valuesScript);
        SqlSource sqlSource = this.languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty,
            keyColumn);
    }
}
