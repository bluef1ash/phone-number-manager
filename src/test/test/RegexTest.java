package test;

import org.apache.poi.util.SystemOutLogger;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式测试
 */
public class RegexTest {

    @Test
    public void addressTest() {
        Pattern pattern = Pattern.compile("(?iUs)^(.*[社区居委会])?(.*)$");
        String address = "大海阳中街11-11";
        Matcher matcher = pattern.matcher(address);
//        System.out.println(matcher.matches());
        while (matcher.find()) {
            System.out.println("全部----" + matcher.group());
            System.out.println("第一个----" + matcher.group(1));
            System.out.println("第二个----" + matcher.group(2));
        }
    }

    @Test
    public void phoneTest() {
        Pattern pattern = Pattern.compile("(?iUs)^(?:(?:[\\(（]?(?:[0-9]{3,4})?\\s*[\\)）-])?\\d{7,9}(?:[-转]\\d{2,6})?)|(?:[\\(\\+]?(?:86)?[\\)]?0?1[34578]{1}\\d{9})$");
        String phone1 = "6242331";
        String phone2 = "13012569219";
        String phone3 = "1111111111";
        Matcher matcher = pattern.matcher(phone1);
        System.out.println("固定电话：" + matcher.matches());
        System.out.println("固定电话全部----" + matcher.group());
        matcher = pattern.matcher(phone2);
        System.out.println("手机号码：" + matcher.matches());
        System.out.println("手机号码全部----" + matcher.group());
        matcher = pattern.matcher(phone3);
        System.out.println("错误号码：" + matcher.matches());
        System.out.println("错误号码全部----" + matcher.group());
    }

    @Test
    public void replaceString() {
        String phone1 = "   6242  331";
        System.out.println(phone1.replaceAll("[\\s\\t\\r\\n]", ""));
        String phone2 = "0535 - 6242331\n\n";
        System.out.println(phone2.replaceAll("[\\s\\t\\r\\n]", ""));
    }
}
