package com.github.phonenumbermanager.constant;

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
     * 社区级机构名称
     */
    public static final String COMMUNITY_NAME = "社区";
    /**
     * 社区级机构别名
     */
    public static final String COMMUNITY_ALIAS_NAME = "居委会";
    /**
     * 街道级机构名称
     */
    public static final String SUBDISTRICT_NAME = "街道";
    /**
     * 街道级机构别名
     */
    public static final String SUBDISTRICT_ALIAS_NAME = "办事处";
    /**
     * 毫秒时间差
     */
    public static final int TIMESTAMP_MILLISECONDS_DIFFERENCE = 1000 * 10;
    /**
     * 通过白名单
     */
    public static final String[] PERMIT_WHITELIST = {"/swagger-resources/**", "/swagger-ui.html", "/v3/api-docs", "/webjars/**", "/swagger-ui/**", "/druid/**"};
    /**
     * 匿名用户白名单
     */
    public static final String[] ANONYMOUS_WHITELIST = {"/account/login", "/account/getRecaptcha", "/loginError"};
    /**
     * 权限通过
     */
    public static final String[] PRIVILEGE_PERMITS = {"/", "/index/getmenu", "/community/select", "/index/getcomputed", "/community/subcontractor/load", "system/user_role/user/edit"};
    /**
     * JWTToken过期时间
     */
    public static final long TOKEN_VALIDITY_IN_MILLISECONDS = 86400L;
    /**
     * JWTToken“记住我”过期时间
     */
    public static final long TOKEN_VALIDITY_IN_MILLISECONDS_FOR_REMEMBER_ME = 108000L;
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
    public static final String BASE64_SECRET = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";
    /**
     * JWT密钥创建标识
     */
    public static final String CLAIM_KEY_CREATED = "created";
    /**
     * 密钥标识
     */
    public static final String AUTHORITIES_KEY = "auth";
    /**
     * 用户名键值
     */
    public static final String USERNAME_KEY = "username";
    /**
     * 姓名最大字符数
     */
    public static final int NAME_MAX_LENGTH = 10;
    /**
     * 地址最大字符数
     */
    public static final int ADDRESS_MAX_LENGTH = 255;
}