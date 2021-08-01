package com.github.phonenumbermanager;

import com.github.phonenumbermanager.entity.UserPrivilege;
import com.github.phonenumbermanager.mapper.UserPrivilegeMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 流测试
 */
@SpringBootTest
public class SteamTest {

    @Resource
    private UserPrivilegeMapper userPrivilegeMapper;

    @Test
    public void map() {
        List<UserPrivilege> userPrivileges = userPrivilegeMapper.selectList(null);
        String[] uri = userPrivileges.stream().map(UserPrivilege::getUri).filter(StringUtils::isNotEmpty).toArray(String[]::new);
        System.out.println(Arrays.toString(uri));
    }
}
