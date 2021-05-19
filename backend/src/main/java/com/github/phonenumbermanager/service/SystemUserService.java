package com.github.phonenumbermanager.service;

import com.github.phonenumbermanager.entity.SystemUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * 系统用户业务接口
 *
 * @author 廿二月的天
 */
public interface SystemUserService extends BaseService<SystemUser>, UserDetailsService {

    /**
     * 通过系统用户编号查找系统用户和关联角色
     *
     * @param id 系统用户编号
     * @return 查找到的系统用户对象
     */
    @Override
    SystemUser getCorrelation(Serializable id);

    /**
     * 查找系统用户编号与名称
     *
     * @return 查找到的系统用户对象集合
     */
    List<SystemUser> getIdAndName();

    /**
     * 认证密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 是否认证通过
     */
    boolean authentication(String username, String password);

    /**
     * 刷新Token
     *
     * @param token 现有的Token
     * @return 新的Token
     */
    String refreshToken(String token);

    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
