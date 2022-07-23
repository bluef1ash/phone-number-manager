package com.github.phonenumbermanager.constant;

import java.time.LocalDateTime;

/**
 * 系统常量
 *
 * @author 廿二月的天
 */
public final class SystemConstant {
    /**
     * 极验编号
     */
    public static final String GEETEST_ID = "4d0b05d8e88645f0cf5a5271ff97db7a";
    /**
     * 极验密钥
     */
    public static final String GEETEST_KEY = "07ee3abbdc56141cdc63b6d4de712505";
    /**
     * 社区级机构名称正则表达式
     */
    public static final String COMMUNITY_NAME_PATTERN = "(?iUs)[社区村居民委员会]";
    /**
     * 街道级机构名称
     */
    public static final String STREET_NAME_PATTERN = "(?iUs)[乡镇街道办事处]";
    /**
     * 毫秒时间差
     */
    public static final int TIMESTAMP_MILLISECONDS_DIFFERENCE = 1000 * 10;
    /**
     * 通过白名单
     */
    public static final String[] PERMIT_WHITELIST = {"/favicon.ico", "/404", "/swagger-resources/**",
        "/swagger-ui.html", "/v3/api-docs", "/webjars/**", "/swagger-ui/**", "/druid/**"};
    /**
     * 匿名用户白名单
     */
    public static final String[] ANONYMOUS_WHITELIST = {"/account/login", "/account/recaptcha"};
    /**
     * 权限通过
     */
    public static final String[] PERMISSION_PERMITS =
        {"/account/logout", "/index/menu", "/account/current-user", "/index/computed", "system/user/{id}"};
    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer_";
    /**
     * 存放Token的Header Key
     */
    public static final String HEADER_STRING = "Authorization";
    /**
     * 配置键值
     */
    public static final String CONFIGURATIONS_MAP_KEY = "configurationsMap";
    /**
     * Base64密钥
     */
    public static final String BASE64_SECRET =
        "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";
    /**
     * JWT密钥创建标识
     */
    public static final String CLAIM_KEY_CREATED = "created";
    /**
     * 密钥标识
     */
    public static final String AUTHORITIES_KEY = "auth";
    /**
     * 用户名键名
     */
    public static final String USERNAME_KEY = "username";
    /**
     * 系统用户键名
     */
    public static final String SYSTEM_USER_ID_KEY = "system_user_id";
    /**
     * 数据库最小时间
     */
    public static final LocalDateTime DATABASE_MIX_DATETIME = LocalDateTime.of(1000, 1, 1, 0, 0, 0);
    /**
     * 数据库最大时间
     */
    public static final LocalDateTime DATABASE_MAX_DATETIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    /**
     * 匿名用户名
     */
    public static final String ANONYMOUS_USER = "anonymousUser";
    /** 默认日期时间格式 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /** 默认时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    /**
     * 系统权限缓存键值
     */
    public static final String SYSTEM_PERMISSIONS_KEY = "systemPermissionAll";
}
