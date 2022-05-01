package com.github.phonenumbermanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.PhoneNumber;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;

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
            phoneNumber.setPhoneType(PhoneTypeEnum.FIXED_LINE);
        } else {
            phoneNumber.setPhoneType(PhoneTypeEnum.UNKNOWN);
        }
        return phoneNumber;
    }

    /**
     * 联系方式包装为对象集合
     *
     * @param numbers
     *            联系方式字符串
     * @return 联系方式对象
     */
    public static List<PhoneNumber> phoneNumber2List(String... numbers) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        for (String number : numbers) {
            if (StrUtil.isNotEmpty(number)) {
                phoneNumbers.add(phoneNumber2Object(number));
            }
        }
        return phoneNumbers;
    }

    /**
     * 字符串去除空白字符
     *
     * @param str
     *            需要操作的字符串
     * @return 去除后的字符串
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Matcher m = SPECIAL_PATTERN.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }

    /**
     * 通过单位对象集合获取最下级单位编号
     *
     * @param companyIds
     *            取下级单位编号集合
     * @param parentId
     *            上级单位编号
     * @param groupByParentIdMap
     *            根据父级编号分组的单位集合
     * @return 当前的下级单位编号
     */
    public static List<Long> listSubmissionCompanyIds(List<Long> companyIds, Long parentId,
        Map<Long, List<Company>> groupByParentIdMap) {
        List<Long> currentPidLowerChildIdList = new ArrayList<>();
        List<Company> childCompany = groupByParentIdMap.get(parentId);
        if (CollUtil.isEmpty(childCompany)) {
            return null;
        }
        childCompany.forEach(company -> {
            List<Long> lowerChildIdSet =
                listSubmissionCompanyIds(currentPidLowerChildIdList, company.getId(), groupByParentIdMap);
            if (CollUtil.isEmpty(lowerChildIdSet)) {
                currentPidLowerChildIdList.add(company.getId());
            }
        });
        companyIds.addAll(currentPidLowerChildIdList);
        return currentPidLowerChildIdList;
    }

    /**
     * 递归获取链条单位编号
     *
     * @param companyIds
     *            递归单位编号集合
     * @param companies
     *            单位对象集合
     * @param companyAll
     *            所有单位对象集合
     * @param parentId
     *            上级单位编号
     */
    public static void listRecursionCompanyIds(List<Long> companyIds, List<Company> companies, List<Company> companyAll,
        Long parentId) {
        companies.forEach(company -> {
            if (parentId.equals(company.getParentId())) {
                companyIds.add(company.getId());
            } else {
                List<Company> companies1 = companyAll.stream()
                    .filter(company1 -> company1.getParentId().equals(company.getId())).collect(Collectors.toList());
                listRecursionCompanyIds(companyIds, companies1, companyAll, company.getId());
            }
        });
    }

    /**
     * 递归获取链条单位编号
     *
     * @param companyIds
     *            递归单位编号集合
     * @param companies
     *            单位对象集合
     * @param parentId
     *            上级单位编号
     */
    public static void listRecursionCompanyIds(List<Long> companyIds, List<Company> companies, Long parentId) {
        companies.forEach(company -> {
            if (parentId.equals(company.getParentId())) {
                listRecursionCompanyIds(companyIds, companies, company.getId());
                companyIds.add(company.getId());
            }
        });
    }
}
