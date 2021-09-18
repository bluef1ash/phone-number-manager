package com.github.phonenumbermanager.configure;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
import com.github.phonenumbermanager.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源配置
 *
 * @author 廿二月的天
 */
@Configuration
@ConditionalOnClass({DruidDataSource.class})
@AutoConfigureBefore({DruidDataSourceAutoConfigure.class})
@Slf4j
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

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        Connection connection = null;
        PreparedStatement state = null;
        InputStream resourceAsStream = null;
        boolean flag = true;
        String connectUrl = "jdbc:mysql://" + host + ":" + port
            + "/mysql?useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&autoReconnect=true";
        try {
            Class.forName(driverClassName);
            // 创建连接
            connection = DriverManager.getConnection(connectUrl, username, password);
            state = connection.prepareStatement("SHOW DATABASES;");
            ResultSet databases = state.executeQuery();
            while (databases.next()) {
                if ("phone_number_manager".equals(databases.getString("Database"))) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                resourceAsStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
                CommonUtil.executeSql(connection.createStatement(), resourceAsStream);
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (null != state) {
                    state.close();
                }
                if (null != connection) {
                    connection.close();
                }
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
            }
        }
        return DruidDataSourceBuilder.create().build();
    }
}
