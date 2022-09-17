package com.github.phonenumbermanager.constant;

import java.time.LocalDateTime;

/**
 * 系统常量
 *
 * @author 廿二月的天
 */
public final class SystemConstant {
    /**
     * 社区级机构名称正则表达式
     */
    public static final String COMMUNITY_NAME_PATTERN = "(?iUs)[社区村居民委员会]";
    /**
     * 街道级机构名称
     */
    public static final String STREET_NAME_PATTERN = "(?iUs)[乡镇街道办事处]";
    /**
     * 匿名用户白名单
     */
    public static final String[] ANONYMOUS_WHITELIST =
        {"/favicon.ico", "/404", "/swagger-resources/**", "/swagger-ui.html", "/v3/api-docs", "/webjars/**",
            "/swagger-ui/**", "/druid/**", "/account/login", "/account/captcha"};
    /**
     * 权限通过白名单
     */
    public static final String[] PERMISSION_WHITELIST = {"/account/logout", "/index/menu", "/account/current-user",
        "/company/select-list", "/system/permission/select-list", "/company/subcontractor/select-list",
        "/resident/computed/message", "/dormitory/computed/message", "/resident/computed/chart",
        "/dormitory/computed/chart", "/company/subcontractor/computed/chart"};
    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer_";
    /**
     * 存放Token的Header Key
     */
    public static final String HEADER_STRING = "Authorization";
    /**
     * 系统配置缓存键值
     */
    public static final String CONFIGURATIONS_MAP_KEY = "configurations_map";
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
     * 系统用户编号缓存键名
     */
    public static final String SYSTEM_USER_ID_KEY = "system_user_id";
    /**
     * 系统菜单缓存键名
     */
    public static final String SYSTEM_MENU_KEY = "system_menu_id";
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
    public static final String SYSTEM_PERMISSIONS_KEY = "system_permission_all";
}
