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
    public static final String[] PRIVILEGE_PERMITS = {"/", "permitAll", "/404", "/index/getmenu", "/community/select", "/index/getcomputed", "/community/subcontractor/load", "system/user_role/user/edit"};
    public static final String[] LOGGED_PERMITS = {"/getcsrf", "permitAll", "/404"};
}
