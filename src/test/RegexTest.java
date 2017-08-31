
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式测试
 */
public class RegexTest {

    @Test
    public void addressTest() {
        Pattern pattern = Pattern.compile("(?iUs)^(.*)(?:社区|居委会)(.*)$");
        String address = "大海阳社区大海阳东街4-2";
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
//        Pattern pattern1 = Pattern.compile("(?iUs)^[(（]?[0-9]{3,4}[-)）]?[0-9]{7}[-转]?[0-9]{2,6}?$");
//        Pattern pattern2 = Pattern.compile("(?iUs)^0?1[3458]{1}[0-9]{9}$");
        Pattern pattern = Pattern.compile("(?iUs)^(?:[\\(（]?(?:[0-9]{3,4})?\\s*[\\)）-]?\\s*[0-9]{7,9}\\s*[-转]?\\s*(?:[0-9]{2,6})?)|(?:[\\(]?[\\+]?[\\(]?(?:86)?[\\)]?0?1[3458]{1}[0-9]{9})$");
        String phone1 = "6242331";
        String phone2 = "13012569219";
        Matcher matcher = pattern.matcher(phone1);
        System.out.println("固定电话：" + matcher.matches());
        System.out.println("固定电话全部----" + matcher.group());
        matcher = pattern.matcher(phone2);
        System.out.println("手机号码：" + matcher.matches());
        System.out.println("手机号码全部----" + matcher.group());
    }
}
