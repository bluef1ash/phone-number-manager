package www.service.impl;

import constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import utils.CommonUtil;
import utils.DateUtil;
import www.entity.Configuration;
import www.entity.SystemUser;
import www.entity.UserPrivilege;
import www.service.SystemUserService;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * 系统用户Service实现
 *
 * @author 廿二月的天
 */
@Service("systemUserService")
public class SystemUserServiceImpl extends BaseServiceImpl<SystemUser> implements SystemUserService {

    @Override
    public Map<String, Object> login(HttpServletRequest request, SystemUser systemUser) throws Exception {
        Map<String, Object> map = new HashMap<>(6);
        SystemUser user = systemUsersDao.selectSystemUserAndRoleAndPrivilegesByName(systemUser.getUsername());
        user.setLoginTime(DateUtil.getTimestamp(new Date()));
        user.setLoginIp(CommonUtil.getIp(request));
        systemUsersDao.updateObject(user);
        user.setPassword(null);
        List<Configuration> configurations = configurationsDao.selectObjectsAll();
        Map<String, Object> configurationsMap = new HashMap<>(configurations.size() + 1);
        for (Configuration configuration : configurations) {
            configurationsMap.put(configuration.getKey(), configuration.getValue());
        }
        // 系统用户权限
        Set<String> privilegeAuth = new HashSet<>();
        Set<String> privilegeParents = new HashSet<>();
        Map<String, Set<String>> privilegeMap = new HashMap<>(3);
        Set<UserPrivilege> userPrivileges;
        Integer systemAdministratorId = CommonUtil.convertConfigurationInteger(configurationsMap.get("system_administrator_id"));
        if (user.getSystemUserId().equals(systemAdministratorId)) {
            userPrivileges = new HashSet<>(userPrivilegesDao.selectPrivilegesByHigherPrivilegeAndIsDisplay(0, 1));
        } else {
            userPrivileges = user.getUserRole().getUserPrivileges();
        }
        for (UserPrivilege userPrivilege : userPrivileges) {
            Integer higherPrivilegeId = userPrivilege.getHigherPrivilege();
            privilegeAuth.add(userPrivilege.getConstraintAuth());
            if (higherPrivilegeId != 0) {
                UserPrivilege privilege = userPrivilegesDao.selectObjectById(higherPrivilegeId);
                privilegeParents.add(privilege.getConstraintAuth());
            }
        }
        privilegeMap.put("privilegeAuth", privilegeAuth);
        privilegeMap.put("privilegeParents", privilegeParents);
        map.put("state", 1);
        map.put("message", "登录成功！");
        map.put("systemUser", user);
        map.put("privilegeMap", privilegeMap);
        map.put("configurationsMap", configurationsMap);
        return map;
    }

    @Override
    public int createSystemUser(SystemUser systemUser) throws Exception {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            systemUser.setPassword(CommonUtil.getMd5String(systemUser.getPassword()));
        }
        return baseDao.insertObject(systemUser);
    }

    @Override
    public int updateSystemUser(SystemUser systemUser) throws Exception {
        if (StringUtils.isNotEmpty(systemUser.getPassword())) {
            systemUser.setPassword(getEncryptedPassword(systemUser.getPassword()));
        }
        return systemUsersDao.updateObject(systemUser);
    }

    @Override
    public Map<String, Object> findSystemUsersAndRoles(Integer pageNum, Integer pageSize) throws Exception {
        setPageHelper(pageNum, pageSize);
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
     * 获得加密后的16进制形式口令
     *
     * @param password 需要加密的密码字符串
     * @return 加密完成的字符串
     * @throws Exception 加密异常
     */
    private String getEncryptedPassword(String password) throws Exception {
        //声明加密后的口令数组变量
        byte[] pwd;
        //随机数生成器
        SecureRandom random = new SecureRandom();
        //声明盐数组变量   12
        byte[] salt = new byte[SystemConstant.SALT_LENGTH];
        //将随机数放入盐变量中
        random.nextBytes(salt);
        //创建消息摘要
        MessageDigest md = MessageDigest.getInstance(SystemConstant.PASSWORD_MODE);
        //将盐数据传入消息摘要对象
        md.update(salt);
        //将口令的数据传给消息摘要对象
        md.update(password.getBytes("UTF-8"));
        //获得消息摘要的字节数组
        byte[] digest = md.digest();
        //因为要在口令的字节数组中存放盐，所以加上盐的字节长度
        pwd = new byte[digest.length + SystemConstant.SALT_LENGTH];
        //将盐的字节拷贝到生成的加密口令字节数组的前12个字节，以便在验证口令时取出盐
        System.arraycopy(salt, 0, pwd, 0, SystemConstant.SALT_LENGTH);
        //将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节
        System.arraycopy(digest, 0, pwd, SystemConstant.SALT_LENGTH, digest.length);
        //将字节数组格式加密后的口令转化为16进制字符串格式的口令
        return CommonUtil.byte2HexString(pwd);
    }
}
