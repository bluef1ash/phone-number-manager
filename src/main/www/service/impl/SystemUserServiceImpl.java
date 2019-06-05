package www.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import www.entity.Configuration;
import www.entity.SystemUser;
import www.entity.UserPrivilege;
import www.service.SystemUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 系统用户Service实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUser> implements SystemUserService, UserDetailsService {

    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUsersDao.selectSystemUserAndRoleAndPrivilegesByName(username);
        if (systemUser == null) {
            throw new UsernameNotFoundException(username + "系统用户不存在！");
        }
        List<Configuration> configurations = configurationsDao.selectObjectsAll();
        Map<String, Object> configurationsMap = new HashMap<>(configurations.size() + 1);
        for (Configuration configuration : configurations) {
            configurationsMap.put(configuration.getKey(), configuration.getValue());
        }
        Integer systemIsActive = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_is_active"));
        Long systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
        if (systemIsActive == 0 && !systemAdministratorId.equals(systemUser.getSystemUserId())) {
            throw new UsernameNotFoundException("该系统已经禁止登录，请联系管理员！");
        }
        // 系统用户权限
        Set<UserPrivilege> userPrivilegesAll = new LinkedHashSet<>(userPrivilegesDao.selectObjectsAll());
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        if (systemAdministratorId.equals(systemUser.getSystemUserId())) {
            userPrivileges = userPrivilegesAll;
        } else {
            Set<UserPrivilege> privileges = systemUser.getUserRole().getUserPrivileges();
            for (UserPrivilege privilege : privileges) {
                userPrivileges.add(privilege);
                userPrivileges.addAll(recursionPrivileges(userPrivilegesAll, privilege.getPrivilegeId()));
            }
            systemUser.getUserRole().setUserPrivileges(userPrivileges);
        }
        HttpSession session = request.getSession();
        session.setAttribute("systemUser", systemUser);
        session.setAttribute("userPrivileges", userPrivileges);
        session.setAttribute("configurationsMap", configurationsMap);
        return systemUser;
    }

    @Override
    public long createObject(SystemUser systemUser) throws Exception {
        return baseDao.insertObject(systemUser);
    }

    @Override
    public int updateObject(SystemUser systemUser) throws Exception {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            systemUser.setPassword(bCryptPasswordEncoder.encode(systemUser.getPassword()));
        }
        return systemUsersDao.updateObject(systemUser);
    }

    @Override
    public Map<String, Object> findSystemUsersAndRoles(Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<SystemUser> data = systemUsersDao.selectSystemUsersAndRolesAll();
        return findObjectsMethod(data);
    }

    @Override
    public SystemUser findSystemUsersAndRoles(Integer id) throws Exception {
        return systemUsersDao.selectSystemUserAndRoleById(id);
    }

    @Override
    public List<SystemUser> findSystemUserByUserName(String username) throws Exception {
        return systemUsersDao.selectObjectsByName(username);
    }

    @Override
    public List<SystemUser> findSystemUsers() throws Exception {
        return systemUsersDao.selectSystemUsersAll();
    }

    /**
     * 递归系统用户权限
     *
     * @param userPrivilegesAll 全部的系统权限
     * @param privilegeId       上级系统权限编号
     * @return 处理后的系统权限集合
     */
    private Set<UserPrivilege> recursionPrivileges(Set<UserPrivilege> userPrivilegesAll, Long privilegeId) {
        Set<UserPrivilege> userPrivileges = new LinkedHashSet<>();
        for (UserPrivilege userPrivilege : userPrivilegesAll) {
            if (userPrivilege.getHigherPrivilege().equals(privilegeId)) {
                userPrivileges.add(userPrivilege);
                userPrivileges.addAll(recursionPrivileges(userPrivilegesAll, userPrivilege.getPrivilegeId()));
            }
        }
        return userPrivileges;
    }
}
