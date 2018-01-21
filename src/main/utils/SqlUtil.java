package utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL工具类
 *
 * @author 廿二月的天
 */
public class SqlUtil {
    private SqlUtil() {
    }

    /**
     * mapper.xml中的取值方式为${}时
     *
     * @param str like的查询条件
     * @return
     */
    public static String likeEscape(String str) {
        return likeEscape(str, true, true);
    }

    /**
     * 拼接LIKE语句
     * @param str   like的查询条件
     * @param start 字符串前部是否拼接“%”
     * @param end   字符串尾部是否拼接“%”
     * @return 拼接完成的字符串
     */
    private static String likeEscape(String str, boolean start, boolean end) {
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
                    // 单引号替换成两个单引号
                    buf.append("''");
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
}
