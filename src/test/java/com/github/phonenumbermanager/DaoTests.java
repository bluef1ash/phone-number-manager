package com.github.phonenumbermanager;

import com.github.phonenumbermanager.dao.CommunityResidentDao;
import com.github.phonenumbermanager.dao.SystemUserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTests {

    @Resource
    private SystemUserDao systemUserDao;
    @Resource
    private CommunityResidentDao communityResidentDao;

    @Test
    public void select() {
        System.out.println(systemUserDao.selectAndRoleById(1));
    }

    @Test
    public void selectByUserData() {
        System.out.println(communityResidentDao.selectAndCommunityByCommunityId(2));
        List<Map<String, Object>> userDataList = new ArrayList<>();
        Map<String, Object> userData = new HashMap<>(3);
        userData.put("companyType", 1);
        userData.put("companyId", 2L);
        userDataList.add(userData);
        System.out.println(communityResidentDao.selectByUserData(userDataList, 1, 2));
    }
}
