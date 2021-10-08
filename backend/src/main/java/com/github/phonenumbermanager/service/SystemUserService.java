package com.github.phonenumbermanager.service;

import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.github.phonenumbermanager.entity.SystemUser;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser>, UserDetailsService {

    /**
     * 查找系统用户编号与名称
     *
     * @return 查找到的系统用户对象集合
     */
    List<SystemUser> getIdAndName();

    /**
     * 认证密码
     *
     * @param username
     *            用户名
     * @param password
     *            密码
     * @throws LoginException
     *             登录异常
     * @return 认证对象
     */
    Authentication authentication(String username, String password) throws LoginException;

    /**
     * 判断权限
     *
     * @param request
     *            HTTP请求
     * @param authentication
     *            认证对象
     * @return 是否享受
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
