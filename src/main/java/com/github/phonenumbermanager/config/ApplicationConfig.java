package com.github.phonenumbermanager.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 应用配置
 *
 * @author 廿二月的天
 */
@Configuration
@EnableTransactionManagement
public class ApplicationConfig {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${mybatis.mapper-locations}")
    private String locationPattern;
    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sqlFactory = new SqlSessionFactoryBean();
        sqlFactory.setDataSource(dataSource());
        sqlFactory.setVfs(SpringBootVFS.class);
        sqlFactory.setTypeAliasesPackage(typeAliasesPackage);
        sqlFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        sqlFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(locationPattern));
        return sqlFactory;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public ErrorPageRegistrar containerCustomizer() {
        return (errorPageRegistry -> {
            errorPageRegistry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/exception"), new ErrorPage(HttpStatus.FORBIDDEN, "/exception"));
        });
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
