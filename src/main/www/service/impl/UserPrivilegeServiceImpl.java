package www.service.impl;

import org.springframework.stereotype.Service;
import www.entity.UserPrivilege;
import www.service.UserPrivilegeService;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 系统用户权限业务实现
 *
 * @author 廿二月的天
 */
@Service("userPrivilegeService")
public class UserPrivilegeServiceImpl extends BaseServiceImpl<UserPrivilege> implements UserPrivilegeService {
    @Override
    public List<UserPrivilege> findPrivilegesByRoleId(Integer roleId) throws Exception {
        return userPrivilegesDao.selectPrivilegesByRoleId(roleId);
    }

    @Override
    public List<UserPrivilege> findPrivilegesByIsDisplay(Integer isDisplay, HttpSession session) throws Exception {
        @SuppressWarnings("unchecked")
        Set<String> privilegeAuthes = (Set<String>) ((Map<String, Object>) session.getAttribute("privilegeMap")).get("privilegeAuth");
        List<UserPrivilege> userPrivileges = userPrivilegesDao.selectPrivilegesByIsDisplayAndConstraintAuth(isDisplay, privilegeAuthes);
        //userPrivileges;
        return findPrivilegesAndSubPrivileges(userPrivileges, isDisplay);
    }

    @Override
    public List<UserPrivilege> findPrivilegesAndSubPrivilegesAll() throws Exception {
        List<UserPrivilege> userPrivileges = userPrivilegesDao.selectObjectsAll();
        // userPrivileges;
        return findPrivilegesAndSubPrivileges(userPrivileges, null);
    }

    /**
     * 有序排列用户权限
     *
     * @param userPrivileges 需要排列的系统用户权限对象集合
     * @param isDisplay      是否在导航栏中显示
     * @return 排列成功的统用户权限对象集合
     */
    private List<UserPrivilege> findPrivilegesAndSubPrivileges(List<UserPrivilege> userPrivileges, Integer isDisplay) {
        List<UserPrivilege> subUserPrivileges = null;
        Map<Long, UserPrivilege> newUserPrivilegesMap = new LinkedHashMap<>();
        UserPrivilege tempPrivilege;
        for (UserPrivilege userPrivilege : userPrivileges) {
            if (userPrivilege.getHigherPrivilege() == 0) {
                // 顶级分类
                if (isDisplay == null) {
                    subUserPrivileges = userPrivilegesDao.selectPrivilegesByHigherPrivilege(userPrivilege.getPrivilegeId());
                } else {
                    subUserPrivileges = userPrivilegesDao.selectPrivilegesByHigherPrivilegeAndIsDisplay(userPrivilege.getPrivilegeId(), isDisplay);
                }
                userPrivilege.setSubUserPrivileges(subUserPrivileges);
                newUserPrivilegesMap.put(userPrivilege.getPrivilegeId(), userPrivilege);
            } else {
                if (subUserPrivileges != null && subUserPrivileges.size() > 0) {
                    // 第一次加入子分类对象
                    if (subUserPrivileges.get(0).getHigherPrivilege().equals(userPrivilege.getHigherPrivilege())) {
                        // 判断是否为同一父分类
                        tempPrivilege = newUserPrivilegesMap.get(subUserPrivileges.get(0).getHigherPrivilege());
                        if (tempPrivilege.getSubUserPrivileges() != null) {
                            tempPrivilege.setSubUserPrivileges(subUserPrivileges);
                        }
                        subUserPrivileges = new ArrayList<>();
                    }
                } else {
                    subUserPrivileges = new ArrayList<>();
                }
                subUserPrivileges.add(userPrivilege);
            }
        }
        if (subUserPrivileges != null && subUserPrivileges.size() > 0) {
            newUserPrivilegesMap.get(subUserPrivileges.get(0).getHigherPrivilege()).setSubUserPrivileges(subUserPrivileges);
        }
        return new ArrayList<>(newUserPrivilegesMap.values());
    }
}
