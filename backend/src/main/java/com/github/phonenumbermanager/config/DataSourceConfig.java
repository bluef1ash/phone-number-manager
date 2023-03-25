package com.github.phonenumbermanager.config;

import java.sql.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据源配置
 *
 * @author 廿二月的天
 */
@Configuration
@ConditionalOnClass({DruidDataSource.class})
public class DataSourceConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${DATABASE_HOST}")
    private String host;
    @Value("${DATABASE_PORT}")
    private String port;
    @Value("${DATABASE_NAME}")
    private String databaseName;

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        String connectUrl = "jdbc:mysql://" + host + ":" + port
            + "/mysql?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&autoReconnect=true";
        Connection connection = null;
        PreparedStatement databaseStatement = null;
        PreparedStatement createDatabaseStatement = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(connectUrl, username, password);
            databaseStatement = connection.prepareStatement(
                "SELECT `SCHEMA_NAME` FROM `information_schema`.`SCHEMATA` WHERE `SCHEMA_NAME` = ?;",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            databaseStatement.setString(1, databaseName);
            ResultSet resultSet = databaseStatement.executeQuery();
            resultSet.last();
            if (resultSet.getRow() == 0) {
                createDatabaseStatement = connection
                    .prepareStatement("CREATE DATABASE ? DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                createDatabaseStatement.setString(1, databaseName);
                if (!createDatabaseStatement.execute()) {
                    throw new SQLException();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (databaseStatement != null) {
                    databaseStatement.close();
                }
                if (createDatabaseStatement != null) {
                    createDatabaseStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return new DruidDataSource();
    }
}
