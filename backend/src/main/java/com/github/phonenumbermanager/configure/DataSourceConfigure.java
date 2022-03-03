package com.github.phonenumbermanager.configure;

import java.sql.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * 数据源配置
 *
 * @author 廿二月的天
 */
@Configuration
@ConditionalOnClass({DruidDataSource.class})
@AutoConfigureBefore({DruidDataSourceAutoConfigure.class})
public class DataSourceConfigure {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.host}")
    private String host;
    @Value("${spring.datasource.port}")
    private String port;
    @Value("${spring.datasource.database-name}")
    private String databaseName;
    private final String connectUrl = "jdbc:mysql://" + host + ":" + port
        + "/mysql?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&autoReconnect=true";

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        Connection connection = null;
        PreparedStatement state = null;
        PreparedStatement statement = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(connectUrl, username, password);
            state = connection.prepareStatement(
                "SELECT `SCHEMA_NAME` FROM `information_schema`.`SCHEMATA` WHERE `SCHEMA_NAME` = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = state.executeQuery(databaseName);
            resultSet.last();
            if (resultSet.getRow() == 0) {
                statement = connection
                    .prepareStatement("CREATE DATABASE ? DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                if (!statement.execute(databaseName)) {
                    throw new SQLException();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != state) {
                    state.close();
                }
                if (null != statement) {
                    statement.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return DruidDataSourceBuilder.create().build();
    }
}
