package www.service;

import www.entity.SystemUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser> {
    /**
     * 系统用户登录
     *
     * @param request    HTTP请求对象
     * @param systemUser 需要验证的系统用户对象
     * @return 验证成功或失败的信息
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> login(HttpServletRequest request, SystemUser systemUser) throws Exception;

    /**
     * 添加系统用户
     *
     * @param systemUser 需要添加的系统用户对象
     * @return 添加数据库影响的行数
     * @throws Exception SERVICE层异常
     */
    int createSystemUser(SystemUser systemUser) throws Exception;

    /**
     * 更新系统用户
     *
     * @param systemUser 需要更新的系统用户对象
     * @return 更新数据库影响的行数
     * @throws Exception SERVICE层异常
     */
    int updateSystemUser(SystemUser systemUser) throws Exception;

    /**
     * 查找所有系统用户和关联角色
     *
     * @param pageNo   分页页码
     * @param pageDataSize 每页显示的条目数
     * @return 查找到的所有系统用户和关联角色与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findSystemUsersAndRoles(Integer pageNo, Integer pageDataSize) throws Exception;

    /**
     * 通过查找系统用户和关联角色
     *
     * @param id 系统用户编号
     * @return 查找到的系统用户对象
     * @throws Exception SERVICE层异常
     */
    SystemUser findSystemUsersAndRoles(Integer id) throws Exception;

    /**
     * 通过系统用户名称查找
     *
     * @param username 系统用户名称
     * @return 查找到的系统用户对象集合（因使用基础类中的方法返回为集合类型）
     * @throws Exception SERVICE层异常
     */
    List<SystemUser> findSystemUserByUserName(String username) throws Exception;

    /**
     * 查找系统用户
     * @return 系统用户集合
     * @throws Exception SERVICE层异常
     */
    List<SystemUser> findSystemUsers() throws Exception;
}
