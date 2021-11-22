package com.github.phonenumbermanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.PhoneNumber;

import cn.hutool.core.util.PhoneUtil;

/**
 * 此类中收集Java编程中WEB开发常用到的一些工具。 为避免生成此类的实例，构造方法被申明为private类型的。
 *
 * @author 廿二月的天
 */
public class CommonUtil {
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[\\s\\t\\r\\n]");

    /**
     * 读取 SQL 文件，获取 SQL 语句
     *
     * @param sqlInputStream
     *            SQL 脚本文件
     * @return List<sql> 返回所有 SQL 语句的 List
     * @throws IOException
     *             IO异常
     */
    public static List<String> loadSql(InputStream sqlInputStream) throws IOException {
        List<String> sqlList = new ArrayList<>();
        StringBuilder sqlStringBuilder = new StringBuilder();
        byte[] buff = new byte[1024];
        int byteRead;
        while ((byteRead = sqlInputStream.read(buff)) != -1) {
            sqlStringBuilder.append(new String(buff, 0, byteRead));
        }
        // Windows 下换行是 \r\n, Linux 下是 \n
        String[] sqlArr = sqlStringBuilder.toString().split("(;\\s*\\r\\n)|(;\\s*\\n)");
        for (String s : sqlArr) {
            String sql = s.replaceAll("--.*", "").trim();
            if (!"".equals(sql)) {
                sqlList.add(sql);
            }
        }
        return sqlList;
    }

    /**
     * 传入连接来执行 SQL 脚本文件，这样可与其外的数据库操作同处一个事物中
     *
     * @param statement
     *            传入数据库连接
     * @param sqlInputStream
     *            SQL 脚本文件
     */
    public static void executeSql(Statement statement, InputStream sqlInputStream) throws IOException, SQLException {
        List<String> sqlList = loadSql(sqlInputStream);
        for (String sql : sqlList) {
            statement.addBatch(sql);
        }
        statement.executeBatch();
    }

    /**
     * 联系方式包装为对象
     *
     * @param number
     *            联系方式字符串
     * @return 联系方式对象
     */
    public static PhoneNumber phoneNumber2Object(String number) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(IdWorker.getId()).setPhoneNumber(number);
        if (PhoneUtil.isMobile(number)) {
            phoneNumber.setPhoneType(PhoneTypeEnum.MOBILE);
        } else if (PhoneUtil.isTel(number)) {
            phoneNumber.setPhoneType(PhoneTypeEnum.LANDLINE);
        } else {
            phoneNumber.setPhoneType(PhoneTypeEnum.UNKNOWN);
        }
        return phoneNumber;
    }

    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Matcher m = SPECIAL_PATTERN.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }
}
