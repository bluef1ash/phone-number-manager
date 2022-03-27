package com.github.phonenumbermanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

/**
 * Excel测试类
 */
@SpringBootTest
public class ExcelTest {

    private void test(Person person) {
        Student student = null;
        if (person instanceof Student) {
            System.out.println("hhhhhhhhhhhhh");
            student = (Student)person;
        }
        if (student != null) {
            student.sleep();
        }
    }

    @Test
    public void run() {
        Student student = new Student();
        test(student);
    }

    @Test
    public void hutoolExport() {
        ExcelWriter writer = ExcelUtil.getWriter("D:/test.xls");
        writer.addHeaderAlias("sequenceNumber", "序号");
        writer.addHeaderAlias("communityName", "社区名称");
        writer.addHeaderAlias("id", "编号");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("genderName", "性别");
        writer.addHeaderAlias("idNumber", "身份证号码");
        writer.addHeaderAlias("politicalStatusName", "政治面貌");
        writer.addHeaderAlias("workStatusName", "工作状况");
        writer.addHeaderAlias("educationName", "文化程度");
        writer.addHeaderAlias("address", "家庭住址（具体到单元号、楼号）");
        writer.addHeaderAlias("managerAddress", "分包楼栋（具体到单元号、楼号）");
        writer.addHeaderAlias("managerCount", "联系户数");
        writer.addHeaderAlias("telephone", "手机");
        writer.addHeaderAlias("landline", "座机");
        writer.addHeaderAlias("subcontractorName", "姓名");
        writer.addHeaderAlias("subcontractorTelephone", "手机");
        writer.merge(0, 1, 0, 0, "序号", true);
        writer.merge(0, 1, 1, 1, "社区名称", true);
        writer.merge(0, 1, 2, 2, "编号", true);
        writer.merge(0, 1, 3, 3, "姓名", true);
        writer.merge(0, 1, 4, 4, "性别", true);
        writer.merge(0, 1, 5, 5, "出生年月", true);
        writer.merge(0, 1, 6, 6, "政治面貌", true);
        writer.merge(0, 1, 7, 7, "工作状况", true);
        writer.merge(0, 1, 8, 8, "文化程度", true);
        writer.merge(0, 1, 9, 9, "家庭住址（具体到单元号、楼号）", true);
        writer.merge(0, 1, 10, 10, "分包楼栋（具体到单元号、楼号）", true);
        writer.merge(0, 1, 11, 11, "联系户数", true);
        writer.merge(0, 1, 12, 12, "手机", true);
        writer.merge(0, 1, 13, 13, "座机", true);
        writer.merge(0, 0, 14, 15, "分包人", true);
        writer.writeCellValue(14, 1, "姓名");
        writer.writeCellValue(15, 1, "手机");
        writer.setCurrentRow(2);
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("sequenceNumber", 1);
        data.put("communityName", "大海阳");
        data.put("id", "DHY00001");
        data.put("name", "姓名");
        data.put("genderName", "性别");
        data.put("idNumber", "");
        data.put("politicalStatusName", "政治面貌");
        data.put("workStatusName", "工作状况");
        data.put("educationName", "文化程度");
        data.put("address", "家庭住址");
        data.put("managerAddress", "分包楼栋");
        data.put("managerCount", 100);
        data.put("telephone", "13000000000");
        data.put("landline", "");
        data.put("subcontractorName", "姓名");
        data.put("subcontractorTelephone", "1300000000");
        ArrayList<LinkedHashMap<String, Object>> list = CollUtil.newArrayList(data);
        writer.write(list);
        writer.flush();
        writer.close();
    }

    private interface Person {
        void eat();
    }

    private static class Student implements Person {
        @Override
        public void eat() {
            System.out.println("111111");
        }

        public void sleep() {
            System.out.println("22222222222");
        }
    }
}
