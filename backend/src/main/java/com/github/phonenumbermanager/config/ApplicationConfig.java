package com.github.phonenumbermanager.config;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.converter.CustomMappingJackson2HttpMessageConverter;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * 应用配置
 *
 * @author 廿二月的天
 */
@Configuration
public class ApplicationConfig {
    private static final String SECURITY_SCHEME_NAME = "Bearer";

    /**
     * Mybatis 插件注册
     *
     * @return 拦截器对象
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * Swagger 配置
     *
     * @return openAPI 对象
     */
    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact();
        contact.setName("廿二月的天");
        contact.setEmail("liangtian_2005@163.com");
        contact.setUrl("https://github.com/bluef1ash");
        License license = new License();
        license.setName("MIT");
        license.setUrl("https://raw.githubusercontent.com/bluef1ash/phone-number-manager/main/LICENSE");
        Info info = new Info().title("社区居民联系方式管理系统开发文档").description(
            "此系统使用 Spring Boot + Mybatis Plus + Spring Security + React + Ant Design Pro 架构编写。数据库采用 MySQL，提供强大的、安全的和完整的管理社区居民的信息的功能。")
            .version("1.0.0").license(license).contact(contact);
        return new OpenAPI().info(info).addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                .name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }

    /**
     * Redis 序列化配置
     *
     * @return Json 序列化对象
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * Redis 序列化配置
     *
     * @param redisConnectionFactory
     *            Redis 连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Spring Cache 格式化 JSON
     *
     * @param connectionFactory
     *            Redis 连接工厂
     * @return Redis 管理配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration configuration =
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)).serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        return new RedisCacheManager(writer, configuration);
    }

    /**
     * 解决前端js处理大数字丢失精度问题，将Long和BigInteger转换成string
     *
     * @return JSON转换器
     */
    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        CustomMappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
            new CustomMappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return jackson2HttpMessageConverter;
    }

    /**
     * 服务监控
     *
     * @param webEndpointsSupplier
     *            网络端点服务提供者
     * @param servletEndpointsSupplier
     *            服务器接口端点服务提供者
     * @param controllerEndpointsSupplier
     *            控制器端点服务提供者
     * @param endpointMediaTypes
     *            端点媒体类型
     * @param corsProperties
     *            COR 配置
     * @param webEndpointProperties
     *            网络端点配置
     * @param environment
     *            环境变量
     * @return 服务监控
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
        ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
        EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
        WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping =
            webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
                || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
            corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
            shouldRegisterLinksMapping);
    }

    /**
     * 异常信息国际化处理
     *
     * @return 信息对象
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:org/springframework/security/messages");
        return messageSource;
    }
}
