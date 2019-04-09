package utils;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类中收集Java编程中WEB开发常用到的一些工具。 为避免生成此类的实例，构造方法被申明为private类型的。
 *
 * @author 廿二月的天
 */
public class CommonUtil {
    /**
     * 替换空字符串的正则表达式
     */
    private static Pattern replaceBlankPattern = Pattern.compile("(?iUs)[\\s\\t\\r\\n]*");
    /**
     * 客户端状态
     */
    private static final String CLIENT_STATUS = "unKnown";

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)半角!
     */
    private static final char DBC_CHAR_START = 33;
    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)半角~
     */
    private static final char DBC_CHAR_END = 126;

    /**
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281全角！
     */
    private static final char SBC_CHAR_START = 65281;

    /**
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374全角～
     */
    private static final char SBC_CHAR_END = 65374;

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移，全角半角转换间隔
     */
    private static final int CONVERT_STEP = 65248;

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理，全角空格 12288
     */
    private static final char SBC_SPACE = 12288;

    /**
     * 半角空格的值，在ASCII中为32(Decimal)半角空格
     */
    private static final char DBC_SPACE = ' ';

    /**
     * 字符串因子
     */
    public static final String HEX_NUMBER_STRING = "0123456789ABCDEF";
    /**
     * 密码加盐数量
     */
    public static final Integer SALT_LENGTH = 12;

    /**
     * 私有构造方法，防止类的实例化，因为工具类不需要实例化。
     */
    private CommonUtil() {
    }

    /**
     * 替换字符串
     *
     * @param strSrc 要进行替换操作的字符串
     * @param strOld 要查找的字符串
     * @param strNew 要替换的字符串
     * @return 替换后的字符串
     */
    public static String replace(String strSrc, String strOld, String strNew) {
        if (strSrc == null || strOld == null || strNew == null) {
            return "";
        }
        int i = 0;
        // 避免新旧字符一样产生死循环
        if (strOld.equals(strNew)) {
            return strSrc;
        }

        if ((i = strSrc.indexOf(strOld, i)) >= 0) {
            char[] arrCsrc = strSrc.toCharArray();
            char[] arrCnew = strNew.toCharArray();
            int intOldLen = strOld.length();
            StringBuilder buf = new StringBuilder(arrCsrc.length);
            buf.append(arrCsrc, 0, i).append(arrCnew);
            i += intOldLen;
            int j = i;
            while ((i = strSrc.indexOf(strOld, i)) > 0) {
                buf.append(arrCsrc, j, i - j).append(arrCnew);
                i += intOldLen;
                j = i;
            }
            buf.append(arrCsrc, j, arrCsrc.length - j);
            return buf.toString();
        }
        return strSrc;
    }

    /**
     * 用于将字符串中的特殊字符转换成Web页中可以安全显示的字符串 可对表单数据据进行处理对一些页面特殊字符进行处理如'
     * <','>','"',''','&'
     *
     * @param strSrc 要进行替换操作的字符串
     * @return 替换特殊字符后的字符串
     */
    public static String htmlEncode(String strSrc) {
        if (strSrc == null) {
            return "";
        }
        char[] arrCsrc = strSrc.toCharArray();
        StringBuilder buf = new StringBuilder(arrCsrc.length);
        for (char anArrCsrc : arrCsrc) {
            if (anArrCsrc == '<') {
                buf.append("&lt;");
            } else if (anArrCsrc == '>') {
                buf.append("&gt;");
            } else if (anArrCsrc == '"') {
                buf.append("&quot;");
            } else if (anArrCsrc == '\'') {
                buf.append("&#039;");
            } else if (anArrCsrc == '&') {
                buf.append("&amp;");
            } else {
                buf.append(anArrCsrc);
            }
        }
        return buf.toString();
    }

    /**
     * 用于将字符串中的特殊字符转换成Web页中可以安全显示的字符串 可对表单数据据进行处理对一些页面特殊字符进行处理如'
     * <','>','"',''','&'
     *
     * @param strSrc 要进行替换操作的字符串
     * @param quotes 为0时单引号和双引号都替换，为1时不替换单引号，为2时不替换双引号，为3时单引号和双引号都不替换
     * @return 替换特殊字符后的字符串
     */
    public static String htmlEncode(String strSrc, int quotes) {
        if (strSrc == null) {
            return "";
        }
        if (quotes == 0) {
            return htmlEncode(strSrc);
        }
        char[] arrCsrc = strSrc.toCharArray();
        StringBuilder buf = new StringBuilder(arrCsrc.length);
        for (char anArrCsrc : arrCsrc) {
            if (anArrCsrc == '<') {
                buf.append("&lt;");
            } else if (anArrCsrc == '>') {
                buf.append("&gt;");
            } else if (anArrCsrc == '"' && quotes == 1) {
                buf.append("&quot;");
            } else if (anArrCsrc == '\'' && quotes == 2) {
                buf.append("&#039;");
            } else if (anArrCsrc == '&') {
                buf.append("&amp;");
            } else {
                buf.append(anArrCsrc);
            }
        }
        return buf.toString();
    }

    /**
     * 和htmlEncode正好相反
     *
     * @param strSrc 要进行转换的字符串
     * @return 转换后的字符串
     */
    public static String htmlDecode(String strSrc) {
        if (strSrc == null) {
            return "";
        }
        strSrc = strSrc.replaceAll("&lt;", "<");
        strSrc = strSrc.replaceAll("&gt;", ">");
        strSrc = strSrc.replaceAll("&quot;", "\"");
        strSrc = strSrc.replaceAll("&#039;", "'");
        strSrc = strSrc.replaceAll("&amp;", "&");
        return strSrc;
    }

    /**
     * 在将数据存入数据库前转换
     *
     * @param strVal 要转换的字符串
     * @return 从“ISO8859_1”到“GBK”得到的字符串
     * @since 1.0
     */
    public static String toChinese(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = strVal.trim();
                strVal = new String(strVal.getBytes("ISO8859_1"), "GBK");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * 编码转换 从UTF-8到GBK
     *
     * @param strVal 需要转换的字符串
     * @return 从“UTF-8”到“GBK”得到的字符串
     */
    public static String toGBK(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = strVal.trim();
                strVal = new String(strVal.getBytes(StandardCharsets.UTF_8), "GBK");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * 将数据从数据库中取出后转换 *
     *
     * @param strVal 要转换的字符串
     * @return 从“GBK”到“ISO8859_1”得到的字符串
     */
    public static String toISO(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes("GBK"), "ISO8859_1");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * GBK编码转换UTF-8
     *
     * @param strVal 要转换的字符串
     * @return 从“GBK”到“UTF-8”得到的字符串
     */
    public static String gbk2utf8(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes("GBK"), StandardCharsets.UTF_8);
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * ISO-8859-1编码转换成UTF-8编码
     *
     * @param strVal 要转换的字符串
     * @return 从“ISO-8859-1”到“UTF-8”得到的字符串
     */
    public static String iso2utf8(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * UTF-8编码转换成ISO-8859-1编码
     *
     * @param strVal 要转换的字符串
     * @return 从“UTF-8”到“ISO-8859-1”得到的字符串
     */
    public static String utf82iso(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * 显示大文本块处理(将字符集转成ISO)
     *
     * @param str 要进行转换的字符串
     * @return 转换成html可以正常显示的字符串
     */
    public static String toISOHtml(String str) {
        return toISO(htmlDecode(null2Blank((str))));
    }

    /**
     * 实际处理 return toChineseNoReplace(null2Blank(str)); 主要应用于老牛的信息发布
     *
     * @param str 要进行处理的字符串
     * @return 转换后的字符串
     */
    public static String toChineseAndHtmlEncode(String str, int quotes) {
        return htmlEncode(toChinese(str), quotes);
    }

    /**
     * 把null值和""值转换成&nbsp; 主要应用于页面表格格的显示
     *
     * @param str 要进行处理的字符串
     * @return 转换后的字符串
     */
    public static String str4Table(String str) {
        if (str == null) {
            return "&nbsp;";
        } else if ("".equals(str)) {
            return "&nbsp;";
        } else {
            return str;
        }
    }

    /**
     * String型变量转换成int型变量
     *
     * @param str 要进行转换的字符串
     * @return 如果str不可以转换成int型数据，返回-1表示异常,否则返回转换后的值
     */
    public static int str2Int(String str) {
        int intVal;
        try {
            intVal = Integer.parseInt(str);
        } catch (Exception e) {
            intVal = 0;
        }
        return intVal;
    }

    /**
     * String型变量转换成double型变量
     *
     * @param str 要进行转换的字符串
     * @return 如果str不可以转换成double型数据，返回-1表示异常,否则返回转换后的值
     */
    public static double str2Double(String str) {
        double dVal = 0;
        try {
            dVal = Double.parseDouble(str);
        } catch (Exception e) {
            dVal = 0;
        }
        return dVal;
    }

    /**
     * String型变量转换成long型变量
     *
     * @param str 要进行转换的字符串
     * @return 如果str不可以转换成long型数据，返回-1表示异常,否则返回转换后的值
     */
    public static long str2Long(String str) {
        long longVal = 0;
        try {
            longVal = Long.parseLong(str);
        } catch (Exception e) {
            longVal = 0;
        }

        return longVal;
    }

    /**
     * String型变量转换成float型变量
     *
     * @param str 要进行转换的字符串
     * @return 如果str不可以转换成float型数据，返回-1表示异常,否则返回转换后的值
     */
    public static float string2Float(String str) {
        return Float.valueOf(str);
    }

    /**
     * float型变量转换成String型变量
     *
     * @param value 要进行转换的字符串
     * @return 如果不可以转换String型数据，返回-1表示异常,否则返回转换后的值
     */
    public static String floatToString(float value) {
        Float floatee = value;
        return floatee.toString();
    }

    /**
     * int型变量转换成String型变量
     *
     * @param intVal 要进行转换的整数
     * @return str 如果intVal不可以转换成String型数据，返回空值表示异常,否则返回转换后的值
     */
    public static String int2Str(int intVal) {
        String str;
        try {
            str = String.valueOf(intVal);
        } catch (Exception e) {
            str = "";
        }
        return str;
    }

    /**
     * long型变量转换成String型变量
     *
     * @param longVal 要进行转换的整数
     * @return str 如果longVal不可以转换成String型数据，返回空值表示异常,否则返回转换后的值
     */
    public static String long2Str(long longVal) {
        String str;
        try {
            str = String.valueOf(longVal);
        } catch (Exception e) {
            str = "";
        }
        return str;
    }

    /**
     * null 处理
     *
     * @param str 要进行转换的字符串
     * @return 如果str为null值，返回空串"",否则返回str
     */
    public static String null2Blank(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * null 处理
     *
     * @param d 要进行转换的日期对像
     * @return 如果d为null值，返回空串"",否则返回d.toString()
     */
    public static String null2Blank(Date d) {
        if (d == null) {
            return "";
        } else {
            return d.toString();
        }
    }

    /**
     * null 处理
     *
     * @param str 要进行转换的字符串
     * @return 如果str为null值，返回空串整数0,否则返回相应的整数
     */
    public static int null2Zero(String str) {
        int intTmp;
        intTmp = str2Int(str);
        if (intTmp == -1) {
            return 0;
        } else {
            return intTmp;
        }
    }

    /**
     * 把null转换为字符串"0"
     *
     * @param str 需要转换的字符串
     * @return 转换成功的字符串
     */
    public static String null2SZero(String str) {
        str = CommonUtil.null2Blank(str);
        if ("".equals(str)) {
            return "0";
        } else {
            return str;
        }
    }

    /**
     * sql语句 处理
     *
     * @param sql    要进行处理的sql语句
     * @param dbtype 数据库类型
     * @return 处理后的sql语句
     */
    public static String sql4DB(String sql, String dbtype) {
        if (!"oracle".equalsIgnoreCase(dbtype)) {
            sql = CommonUtil.toISO(sql);
        }
        return sql;
    }

    /**
     * 对字符串进行md5加密
     *
     * @param s 要加密的字符串
     * @return md5加密后的字符串
     */
    public static String getMd5String(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串从GBK编码转换为Unicode编码
     *
     * @param text 要转换的字符串
     * @return 转换成功的字符串
     */
    public static String string2Unicode(String text) {
        StringBuilder result = new StringBuilder();
        int input;
        StringReader isr;
        try {
            isr = new StringReader(new String(text.getBytes(), "GBK"));
            while ((input = isr.read()) != -1) {
                result.append("&#x").append(Integer.toHexString(input)).append(";");
            }
        } catch (UnsupportedEncodingException e) {
            return "-1";
        } catch (IOException e) {
            return "-2";
        }
        isr.close();
        return result.toString();

    }

    /**
     * Unicode编码
     *
     * @param gbString 要转换的字符串
     * @return 转换成功的字符串
     */
    public static String encodeUnicode(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes.toString();
    }

    /**
     * Unicode解码
     *
     * @param dataStr 要解码的字符串
     * @return 解码成功的字符串
     */
    public static String decodeUnicode(String dataStr) {
        int start = 0;
        int end;
        StringBuilder buffer = new StringBuilder();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2);
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            // 16进制parse整形字符串。
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(letter);
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 二进制数组转换字符串
     *
     * @param b 需要转换的字符数组
     * @return 转换成功的字符串
     */
    public String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
            if (n < b.length - 1) {
                hs.append(":");
            }
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP协议请求对象
     * @return 客户端IP地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !CLIENT_STATUS.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !CLIENT_STATUS.equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @param str 需要处理的字符串
     * @return 处理后的字符串
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = replaceBlankPattern.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 半角字符->全角字符转换，只处理空格，!到˜之间的字符，忽略其他
     *
     * @param src 要转换的字符串
     * @return 转换成功的字符串
     */
    public static String bj2qj(String src) {
        if (src == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char aCa : ca) {
            if (aCa == DBC_SPACE) {
                // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((aCa >= DBC_CHAR_START) && (aCa <= DBC_CHAR_END)) {
                // 字符是!到~之间的可见字符
                buf.append((char) (aCa + CONVERT_STEP));
            } else {
                // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(aCa);
            }
        }
        return buf.toString();
    }

    /**
     * 全角字符->半角字符转换只处理全角的空格，全角！到全角～之间的字符，忽略其他
     *
     * @param src 要转换的字符串
     * @return 转换成功的字符串
     */
    public static String qj2bj(String src) {
        if (src == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) {
                // 如果位于全角！到全角～区间内
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE) {
                // 如果是全角空格
                buf.append(DBC_SPACE);
            } else {
                // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    /**
     * 获取16进制随机数
     *
     * @param len 随机数的长度
     * @return 随机数
     */
    public static String randomHexString(int len) {
        StringBuilder result = new StringBuilder();
        try {
            for (int i = 0; i < len; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString().toUpperCase();
    }

    /**
     * 获取随机整数
     *
     * @param num 随机数的长度
     * @return 随机数
     */
    public static Integer randomInt(int num) {
        Random random = new Random();
        return random.nextInt(num);
    }

    /**
     * 转换系统配置项数值
     *
     * @param object 系统配置项
     * @return 转换完成
     */
    public static Integer convertConfigurationInteger(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

    /**
     * 转换系统配置项数值
     *
     * @param object 系统配置项
     * @return 转换完成
     */
    public static Long convertConfigurationLong(Object object) {
        return Long.parseLong(String.valueOf(object));
    }

    /**
     * 转换系统配置项字符串
     *
     * @param object 系统配置项
     * @return 转换完成
     */
    public static String convertConfigurationString(Object object) {
        return String.valueOf(object);
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex 要转换的字符串
     * @return 转换成功的字节数组
     */
    public static byte[] hexString2Byte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMBER_STRING.indexOf(hexChars[pos]) << 4 | HEX_NUMBER_STRING.indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b 要转换的字符数组
     * @return 转换成功的16进制字符串
     */
    public static String byte2HexString(byte[] b) {
        StringBuilder hexString = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }
}
