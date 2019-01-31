package www.service.impl;

import org.springframework.stereotype.Service;
import www.entity.UserRole;
import www.service.UserRoleService;

import java.util.List;
import java.util.Map;

/**
 * 系统用户角色业务实现
 *
 * @author 廿二月的天
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements UserRoleService {
    @Override
    public Map<String, Object> findRolesAndSystemUsersAll(Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<UserRole> data = userRolesDao.selectRolesAndSystemUsersAll();
        return findObjectsMethod(data);
    }

    @Override
    public Map<String, Object> findRolesAndPrivileges(Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<UserRole> data = userRolesDao.selectRolesAndPrivilegesAll();
        return findObjectsMethod(data);
    }

    @Override
    public UserRole findRolesAndPrivileges(Long roleId) throws Exception {
        return userRolesDao.selectRolesAndPrivilegesById(roleId);
    }

    @Override
    public List<UserRole> findRolesByRoleName(String roleName) throws Exception {
        return userRolesDao.selectObjectsByName(roleName);
    }
}
