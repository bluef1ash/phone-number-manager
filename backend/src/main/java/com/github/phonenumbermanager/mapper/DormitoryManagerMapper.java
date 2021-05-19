package com.github.phonenumbermanager.mapper;

import com.github.phonenumbermanager.entity.DormitoryManager;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 楼片长Mapper接口
 *
 * @author 廿二月的天
 */
@Repository
public interface DormitoryManagerMapper extends CommonMapper<DormitoryManager> {

    /**
     * 通过社区编号查询最后一个编号
     *
     * @param communityId 社区编号
     * @return 楼片长编号
     * @throws DataAccessException 数据库连接异常
     */
    String selectLastIdByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 统计所有楼片长性别
     *
     * @return 性别统计对象
     * @throws DataAccessException 数据库连接异常
     */
    Map<String, Long> countGenderAll() throws DataAccessException;

    /**
     * 通过社区编号统计所有楼片长性别
     *
     * @param communityId 社区编号
     * @return 性别统计对象
     * @throws DataAccessException 数据库连接异常
     */
    Map<String, Long> countGenderByCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号统计所有楼片长性别
     *
     * @param subdistricId 街道级编号
     * @return 楼片长性别对象
     * @throws DataAccessException 数据库连接异常
     */
    Map<String, Long> countGenderBySubdistrictId(Serializable subdistricId) throws DataAccessException;

    /**
     * 统计所有楼片长年龄范围
     *
     * @return 年龄范围集合
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countAgeRangeAll() throws DataAccessException;

    /**
     * 通过社区编号统计统计楼片长年龄范围
     *
     * @param communityId 社区编号
     * @return 楼片长年龄范围对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countAgeRangeCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号统计统计楼片长年龄范围
     *
     * @param subdistricId 街道级编号
     * @return 楼片长年龄范围对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countAgeRangeSubdistrictId(Serializable subdistricId) throws DataAccessException;

    /**
     * 统计所有楼片长文化程度
     *
     * @return 文化程度集合
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countEducationRangeAll() throws DataAccessException;

    /**
     * 通过社区编号统计统计楼片长文化程度
     *
     * @param communityId 社区编号
     * @return 楼片长文化程度对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countEducationRangeCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号统计统计楼片长文化程度
     *
     * @param subdistricId 街道级编号
     * @return 楼片长文化程度对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countEducationRangeSubdistrictId(Serializable subdistricId) throws DataAccessException;

    /**
     * 统计所有楼片长政治面貌
     *
     * @return 政治面貌集合
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countPoliticalStatusRangeAll() throws DataAccessException;

    /**
     * 通过社区编号统计统计楼片长政治面貌
     *
     * @param communityId 社区编号
     * @return 楼片长政治面貌对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countPoliticalStatusRangeCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号统计统计楼片长政治面貌
     *
     * @param subdistricId 街道级编号
     * @return 楼片长政治面貌对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countPoliticalStatusRangeSubdistrictId(Serializable subdistricId) throws DataAccessException;

    /**
     * 统计所有楼片长工作状况
     *
     * @return 工作状况集合
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countWorkStatusRangeAll() throws DataAccessException;

    /**
     * 通过社区编号统计统计楼片长工作状况
     *
     * @param communityId 社区编号
     * @return 楼片长工作状况对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countWorkStatusRangeCommunityId(Serializable communityId) throws DataAccessException;

    /**
     * 通过街道编号统计统计楼片长工作状况
     *
     * @param subdistricId 街道级编号
     * @return 楼片长工作状况对象
     * @throws DataAccessException 数据库连接异常
     */
    LinkedHashMap<String, Long> countWorkStatusRangeSubdistrictId(Serializable subdistricId) throws DataAccessException;

    /**
     * 街道分组统计分包户数求和
     *
     * @return 社区分包户数求和对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> sumManagerCountForGroupSubdistrict() throws DataAccessException;

    /**
     * 通过社区编号分组统计分包户数求和
     *
     * @param subdistrictId 街道编号
     * @return 社区分包户数求和对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> sumManagerCountForGroupByCommunityId(Serializable subdistrictId) throws DataAccessException;

    /**
     * 通过街道单位编号分组统计分包户数求和
     *
     * @param communityId 社区编号
     * @return 社区分包户数求和对象
     * @throws DataAccessException 数据库操作异常
     */
    LinkedList<Map<String, Object>> sumManagerCountForGroupCommunity(Serializable communityId) throws DataAccessException;
}
