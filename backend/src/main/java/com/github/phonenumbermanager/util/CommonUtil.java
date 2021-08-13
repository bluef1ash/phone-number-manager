package com.github.phonenumbermanager.util;

import java.util.ArrayList;
import java.util.List;

import com.github.phonenumbermanager.entity.PhoneNumber;

/**
 * 此类中收集Java编程中WEB开发常用到的一些工具。 为避免生成此类的实例，构造方法被申明为private类型的。
 *
 * @author 廿二月的天
 */
public class CommonUtil {

    /**
     * 设置联系方式
     *
     * @param numbers
     *            联系方式集合字符串
     * @return 联系方式集合对象
     */
    public static List<PhoneNumber> setPhoneNumbers(List<String> numbers) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        for (String number : numbers) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber(number);
            phoneNumbers.add(phoneNumber);
        }
        return phoneNumbers;
    }
}
