package com.github.phonenumbermanager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.vo.DormitoryManagerSearchVo;

/**
 * 楼片长Mapper接口
 *
 * @author 廿二月的天
 */
@Mapper
public interface DormitoryManagerMapper extends BaseMapper<DormitoryManager> {
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
    List<DormitoryManager> selectBySearchVo(List<Company> companies, DormitoryManagerSearchVo searchVo)
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
    IPage<DormitoryManager> selectBySearchVo(List<Company> companies, DormitoryManagerSearchVo searchVo,
        IPage<DormitoryManager> page) throws DataAccessException;
}
