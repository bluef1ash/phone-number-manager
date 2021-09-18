package com.github.phonenumbermanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
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
}
