package com.github.phonenumbermanager.utils;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;

import java.util.regex.Pattern;

/**
 * 字符串正则表达式验证
 *
 * @author 廿二月的天
 */
public class StringCheckedRegexUtils {
    private static Pattern phonePattern = Pattern.compile("(?iUs)^[(+]?(?:86)?[)]?0?1[34578]\\d{9}$");
    private static Pattern landlinePattern = Pattern.compile("(?iUs)^[(（]?(?:\\d{3,4})?\\s*[)）-]?\\d{7,9}(?:[-转]\\d{2,6})?$");

    /**
     * 验证联系方式是否合法
     *
     * @param phone 需要验证的联系方式
     * @return 1验证为移动联系方式，2座机，0非法联系方式
     */
    public static PhoneCheckedTypes checkPhone(String phone) {
        if (phonePattern.matcher(phone).matches()) {
            return PhoneCheckedTypes.MOBILE;
        } else if (landlinePattern.matcher(phone).matches()) {
            return PhoneCheckedTypes.LANDLINE;
        }
        return PhoneCheckedTypes.FAILED;
    }
}
