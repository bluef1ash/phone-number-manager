package main.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import main.entity.SystemUser;
/**
 * 系统用户业务接口
 *
 */
public interface SystemUserService extends BaseService<SystemUser> {
	/**
	 * 登录验证
	 * @param request
	 * @param systemUser
	 * @param captcha
	 * @param sRand
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> loginCheck(HttpServletRequest request, SystemUser systemUser, String captcha, String sRand) throws Exception;
	/**
	 * 添加系统用户
	 * @param systemUser
	 * @return
	 * @throws Exception
	 */
	public int createSystemUser(SystemUser systemUser) throws Exception;
	/**
	 * 更新系统用户
	 * @param systemUser
	 * @return
	 * @throws Exception
	 */
	public int updateSystemUser(SystemUser systemUser) throws Exception;
	/**
	 * 查找所有系统用户和关联角色
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findSystemUsersAndRoles(Integer pageNo,Integer pageSize) throws Exception;
	/**
	 * 通过查找系统用户和关联角色
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SystemUser findSystemUsersAndRoles(Integer id) throws Exception;
}