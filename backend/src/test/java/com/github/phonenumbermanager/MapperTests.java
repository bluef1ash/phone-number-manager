package com.github.phonenumbermanager;

import com.github.phonenumbermanager.mapper.CommunityResidentMapper;
import com.github.phonenumbermanager.mapper.SystemUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO测试类
 */
@SpringBootTest
public class MapperTests {

    @Resource
    private SystemUserMapper systemUserMapper;
    @Resource
    private CommunityResidentMapper communityResidentMapper;

    @Test
    public void select() {
        System.out.println(systemUserMapper.selectAndRoleById(1));
    }

    @Test
    public void selectByUserData() {
        System.out.println(communityResidentMapper.selectAndCommunityByCommunityId(2));
        List<Map<String, Object>> userDataList = new ArrayList<>();
        Map<String, Object> userData = new HashMap<>(3);
        userData.put("companyType", 1);
        userData.put("companyId", 2L);
        userDataList.add(userData);
        System.out.println(communityResidentMapper.selectByUserData(userDataList, 1, 2));
    }
}
