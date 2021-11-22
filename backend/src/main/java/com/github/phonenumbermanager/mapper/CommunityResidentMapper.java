package com.github.phonenumbermanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.vo.CommunityResidentSearchVo;

/**
 * 社区居民Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface CommunityResidentMapper extends BaseMapper<CommunityResident> {
    /**
     * 通过用户角色查询对象
     *
     * @param companies
     *            登录的系统用户的单位集合
     * @param searchVo
     *            需要查询的对象
     * @return 对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    List<CommunityResident> selectBySearchVo(List<Company> companies, CommunityResidentSearchVo searchVo)
        throws DataAccessException;

    /**
     * 通过单位查询对象（包含分页）
     *
     * @param companies
     *            登录的系统用户的单位集合
     * @param searchVo
     *            需要查询的对象
     * @param page
     *            分页对象
     * @return 对象集合
     * @throws DataAccessException
     *             数据库操作异常
     */
    IPage<CommunityResident> selectBySearchVo(List<Company> companies, CommunityResidentSearchVo searchVo,
        IPage<CommunityResident> page) throws DataAccessException;
}
