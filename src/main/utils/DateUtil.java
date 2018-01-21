package utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * 时间工具类
 *
 * @author 廿二月的天
 */
public class DateUtil {

    private static String datePattern = "MM/dd/yyyy";

    private static String timePattern = datePattern + " HH:MM a";

    private DateUtil() {
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     *
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        return datePattern;
    }

    /**
     * This method attempts to convert an Oracle-formatted date in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(datePattern);
            returnValue = df.format(aDate);
        }
        return returnValue;
    }

    /**
     * 日期转换成字符串
     *
     * @param aDate 要转换的日期
     * @return 转换成功的字符串
     */
    public static String date2Str(Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(datePattern);
            returnValue = df.format(aDate);
        }
        return returnValue;
    }

    /**
     * 日期转换成字符串
     *
     * @param pattern 转换的格式
     * @param aDate   要转换的日期
     * @return 转换成功的字符串
     */
    public static String date2Str(String pattern, Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(aDate);
        }
        return returnValue;
    }

    /**
     * This method generates a string representation of a date/time in the format you specify on input
     *
     * @param aMask   the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @throws ParseException 转换异常
     */
    public static Date convertStringToDate(String aMask, String strDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(aMask);
        return df.parse(strDate);
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(timePattern, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException 转换异常
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(datePattern);
        // This seems like quite a hack (date -> string -> date), but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(Objects.requireNonNull(convertStringToDate(todayAsString)));
        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     */
    public static String getDateTime(String aMask, Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            SimpleDateFormat df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return returnValue;
    }

    /**
     * This method generates a string representation of a date based on the System Property 'dateFormat' in the format you specify on input
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(datePattern, aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException 转换异常
     */
    public static Date convertStringToDate(String strDate) throws ParseException {
        return convertStringToDate(datePattern, strDate);
    }

    /**
     * 日期格式转换成时间戳
     *
     * @param pattern 转换的格式
     * @param strDate 日期
     * @return 时间戳
     * @throws ParseException 转换异常
     */
    public static long getTimeStamp(String pattern, String strDate) throws ParseException {
        long returnTimeStamp = 0;
        Date aDate = convertStringToDate(pattern, strDate);
        if (aDate != null) {
            returnTimeStamp = aDate.getTime();
        }
        return returnTimeStamp;
    }

    /**
     * 获取当前日期的邮戳
     *
     * @return 当前时间的时间戳
     * @throws ParseException 转换异常
     */
    public static long getNowTimeStamp() throws ParseException {
        long returnTimeStamp = 0;
        Date aDate = convertStringToDate("yyyy-MM-dd HH:mm:ss", getNowDateTime());
        if (aDate != null) {
            returnTimeStamp = aDate.getTime();
        }
        return returnTimeStamp;
    }

    /**
     * 得到格式化后的系统当前日期
     *
     * @param strScheme 格式模式字符串
     * @return 格式化后的系统当前时间，如果有异常产生，返回空串""
     */
    public static String getNowDateTime(String strScheme) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(strScheme);
        return sdf.format(now);
    }

    /**
     * 获取当前现在的日期时间
     *
     * @return 日期时间字符串
     */
    public static String getNowDateTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(now);
    }

    /**
     * 转化日期格式"MM/dd/YY、MM.dd.YY、MM-dd-YY、MM/dd/YY"，并输出为正常的格式yyyy-MM-dd
     *
     * @param strDate 待转换的日期格式
     * @return 格式化后的日期
     * @throws ParseException 转换异常
     */
    public static String convertNormalDate(String strDate) throws ParseException {
        //先把传入参数分格符转换成java认识的分格符
        String[] dateArr = strDate.split("[./\\-]");
        if (dateArr.length == 3) {
            if (dateArr[2].length() != 4) {
                String nowYear = getNowDateTime("yyyy");
                dateArr[2] = nowYear.substring(0, 2) + dateArr[2];
            }
            return getDateTime("yyyy-MM-dd", convertStringToDate(combineStringArray(dateArr, "/")));
        }
        return "";
    }

    /**
     * 将字符串数组使用指定的分隔符合并成一个字符串。
     *
     * @param array 字符串数组
     * @param delim 分隔符，为null的时候使用""作为分隔符（即没有分隔符）
     * @return 合并后的字符串
     */
    public static String combineStringArray(String[] array, String delim) {
        int length = array.length - 1;
        if (delim == null) {
            delim = "";
        }
        StringBuilder result = new StringBuilder(length * 8);
        for (int i = 0; i < length; i++) {
            result.append(array[i]);
            result.append(delim);
        }
        result.append(array[length]);
        return result.toString();
    }

    /**
     * 英文星期转换数字
     *
     * @param strWeek 要转换的星期
     * @return 转换后的星期
     */
    public static int getWeekNum(String strWeek) {
        int returnValue = 0;
        switch (strWeek) {
            case "Mon":
                returnValue = 1;
                break;
            case "Tue":
                returnValue = 2;
                break;
            case "Wed":
                returnValue = 3;
                break;
            case "Thu":
                returnValue = 4;
                break;
            case "Fri":
                returnValue = 5;
                break;
            case "Sat":
                returnValue = 6;
                break;
            case "Sun":
                returnValue = 0;
                break;
            default:
                returnValue = 0;
                break;
        }
        return returnValue;
    }

    /**
     * 获取日期字符串中的中文时间表示字符串
     *
     * @param strDate 要转换的日期字符串
     * @return 转换后的日期字符串
     * @throws ParseException 转换异常
     */
    public static String getSabreTime(String strDate) throws ParseException {
        Date d = convertStringToDate("yyyy-MM-dd HH:mm:ss", CommonUtil.replace(strDate, "T", " "));
        return date2Str("hh:mm aaa", d);
    }

    /**
     * 获取日期字符串中的中文日期表示字符串
     *
     * @param strDate 要转换的日期字符串
     * @return 转换后的日期字符串
     * @throws ParseException 转换异常
     */
    public static String getSabreDate(String strDate) throws ParseException {
        String p;
        if (strDate.length() > 10) {
            p = "yyyy-MM-dd HH:mm:ss";
        } else {
            p = "yyyy-MM-dd";
        }
        Date d = convertStringToDate(p, CommonUtil.replace(strDate, "T", " "));
        return date2Str("EEE d-MMM", d);
    }

    /**
     * 获取日期字符串的中文日期时间表示
     *
     * @param strDate 要转换的日期字符串
     * @return 转换后的日期字符串
     * @throws ParseException 转换异常
     */
    public static String getSabreDateTime(String strDate) throws ParseException {
        String p;
        if (strDate.length() > 10) {
            p = "yyyy-MM-dd HH:mm:ss";
        } else {
            p = "yyyy-MM-dd";
        }
        Date d = convertStringToDate(p, CommonUtil.replace(strDate, "T", " "));
        return date2Str("EEE d-MMM hh:mm aaa", d);
    }

    /**
     * 得到格式化后的指定日期
     *
     * @param strScheme 格式模式字符串
     * @param date      指定的日期值
     * @return 格式化后的指定日期
     */
    public static String getDateTime(Date date, String strScheme) {
        SimpleDateFormat sdf = new SimpleDateFormat(strScheme);
        return sdf.format(date);
    }

    /**
     * 获取日期
     *
     * @param timeType 时间类型，譬如：Calendar.DAY_OF_YEAR
     * @param timeNum  时间数字，譬如：-1 昨天，0 今天，1 明天
     * @return 日期
     */
    public static Date getDateFromNow(int timeType, int timeNum) {
        Calendar cld = Calendar.getInstance();
        cld.set(timeType, cld.get(timeType) + timeNum);
        return cld.getTime();
    }

    /**
     * 获取日期
     *
     * @param timeType     时间类型，譬如：Calendar.DAY_OF_YEAR
     * @param timeNum      时间数字，譬如：-1 昨天，0 今天，1 明天
     * @param formatString 时间格式，譬如："yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static String getDateFromNow(int timeType, int timeNum, String formatString) {
        if (StringUtils.isEmpty(formatString)) {
            formatString = "yyyy-MM-dd HH:mm:ss";
        }
        Calendar cld = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(formatString);
        cld.set(timeType, cld.get(timeType) + timeNum);
        Date date = cld.getTime();
        return df.format(date);
    }

    /**
     * 获取当前日期的字符串
     *
     * @param formatString 时间格式，譬如："yyyy-MM-dd HH:mm:ss"
     * @return 字符串
     */
    public static String getDateNow(String formatString) {
        if (StringUtils.isEmpty(formatString)) {
            formatString = "yyyy-MM-dd HH:mm:ss";
        }
        Calendar cld = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(formatString);
        return df.format(cld.getTime());
    }

    /**
     * 获取当前日期的数据库时间戳
     *
     * @param date 日期对象
     * @return 数据库时间戳
     */
    public static Timestamp getDateNow(Date date) {
        return new Timestamp(date.getTime());
    }
}
