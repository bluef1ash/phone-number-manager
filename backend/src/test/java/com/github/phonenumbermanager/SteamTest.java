package com.github.phonenumbermanager;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.mapper.SystemPermissionMapper;

import cn.hutool.core.util.StrUtil;

/**
 * 流测试
 */
@SpringBootTest
public class SteamTest {
    private SystemPermissionMapper systemPermissionMapper;

    @Test
    public void map() {
        List<SystemPermission> systemPermissions = systemPermissionMapper.selectList(null);
        systemPermissions.stream().map(SystemPermission::getUri).filter(StrUtil::isNotEmpty)
            .forEach(System.out::println);
    }

    @Autowired
    public void setSystemPermissionMapper(SystemPermissionMapper systemPermissionMapper) {
        this.systemPermissionMapper = systemPermissionMapper;
    }
}
