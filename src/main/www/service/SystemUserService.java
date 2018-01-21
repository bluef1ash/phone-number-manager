package www.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import www.entity.SystemUser;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser> {
    /**
     * 登录验证
     *
     * @param request    HTTP请求对象
     * @param systemUser 需要验证的系统用户对象
     * @param captcha    验证码
     * @param sRand      随机数
     * @return 验证成功或失败的信息
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> loginCheck(HttpServletRequest request, SystemUser systemUser, String captcha, String sRand) throws Exception;

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
     * @param pageSize 每页显示的条目数
     * @return 查找到的所有系统用户和关联角色与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findSystemUsersAndRoles(Integer pageNo, Integer pageSize) throws Exception;

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
}
