package la.isx.phone_number_manager.utils;

import org.apache.commons.lang3.StringUtils;

public class SqlUtil {
	 /** 
     * mapper.xml中的取值方式为${}时 
     *  
     * @param str 
     *            like的查询条件 
     * @return 
     */
    public static String likeEscape(String str) {
        return likeEscape(str, true, true);
    }
    /** 
     * @param str 
     *            like的查询条件 
     * @param start 
     *            字符串前部是否拼接“%” 
     * @param end 
     *            字符串尾部是否拼接“%” 
     * @return 
     */  
    public static String likeEscape(String str, boolean start, boolean end) {
        if (StringUtils.isBlank(str)) {
            return null;  
        }
        StringBuilder buf = new StringBuilder();
        // 拼接顺序不能改变 
        buf.append(" '");
        if (start) {
            buf.append("%");
        }
        boolean flag = false;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            switch (c) {
	            case '\'':
	                buf.append("''");// 单引号替换成两个单引号 
	                break;
	            case '_':
	                buf.append("\\_");
	                flag = true;
	                break;
	            case '%':
	                buf.append("\\%");
	                flag = true;
	                break;
	            default:
	                buf.append(c);
            }
        }
        if (end) {
            buf.append("%");
        }
        buf.append("' ");
        if (flag) {
            buf.append("escape '\\' ");
        }
        return buf.toString();
    }
    private SqlUtil() {}
}