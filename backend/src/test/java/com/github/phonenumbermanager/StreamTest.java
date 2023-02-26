package com.github.phonenumbermanager;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * 流测试类
 *
 * @author 廿二月的天
 */
@SpringBootTest
@AllArgsConstructor
public class StreamTest {
    private final SystemPermissionMapper systemPermissionMapper;

    @Test
    public void map() {
        List<SystemPermission> systemPermissions = systemPermissionMapper.selectList(null);
        systemPermissions.stream().map(SystemPermission::getUri).filter(StrUtil::isNotEmpty)
            .forEach(System.out::println);
    }
}
