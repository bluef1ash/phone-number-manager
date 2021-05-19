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
     * 权限通过
     */
    public static final String[] PRIVILEGE_PERMITS = {"/", "/index/getmenu", "/community/select", "/index/getcomputed", "/community/subcontractor/load", "system/user_role/user/edit"};
    /**
     * JWTToken过期时间
     */
    public static final long EXPIRATION_TIME = 432_000_000;
    /**
     * JWT密码
     */
    public static final String SECRET = "CodeSheepSecret";
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
}
