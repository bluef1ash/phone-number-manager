package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串正则表达式验证
 *
 * @author 廿二月的天
 */
public class StringCheckedRegexUtil {
    private static Pattern phonePattern = Pattern.compile("(?iUs)^(?:(?:[(（]?(?:[0-9]{3,4})?\\s*[)）-])?\\d{7,9}(?:[-转]\\d{2,6})?)|(?:[(+]?(?:86)?[)]?0?1[34578]\\d{9})$");

    /**
     * 验证联系方式是否合法
     *
     * @param phone 需要验证的联系方式
     * @return 是否验证成功
     */
    public static boolean checkPhone(String phone) {
        Matcher matcher = phonePattern.matcher(phone);
        return matcher.matches();
    }
}
