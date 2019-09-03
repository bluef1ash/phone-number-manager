package com.github.phonenumbermanager.service;

import com.alibaba.fastjson.JSONArray;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 社区楼长Service接口
 *
 * @author 廿二月的天
 */
public interface DormitoryManagerService extends BaseService<DormitoryManager> {
    /**
     * 查找所有社区楼长及所属社区
     *
     * @param systemUser             登录的系统用户对象
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param pageNumber             分页页码
     * @param pageDataSize           每页展示的数量
     * @return 查找到的社区楼长集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> findCorrelation(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过楼长编号查找楼长与所属社区
     *
     * @param id 楼长编号
     * @return 楼长对象
     * @throws Exception SERVICE层异常
     */
    DormitoryManager findCorrelation(Serializable id) throws Exception;

    /**
     * 从Excel导入数据
     *
     * @param workbook          Excel工作簿对象
     * @param subdistrictId     导入的街道编号
     * @param configurationsMap 系统配置
     * @return 导入的行数
     * @throws Exception SERVICE层异常
     */
    long create(Workbook workbook, Serializable subdistrictId, Map<String, Object> configurationsMap) throws Exception;

    /**
     * 通过系统用户角色编号与定位角色编号查找社区楼长及所属社区
     *
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param userData               用户数据
     * @param titles                 标题数组
     * @return 社区楼长与所属社区集合转换的JSON对象
     * @throws Exception SERVICE层异常
     */
    JSONArray findCorrelation(Serializable communityCompanyType, Serializable subdistrictCompanyType, List<Map<String, Object>> userData, String[] titles) throws Exception;

    /**
     * 通过社区楼长查找匹配的社区楼长
     *
     * @param systemUser             登录的系统用户对象
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @param dormitoryManager       需要查找的社区楼长
     * @param companyId              查找的范围单位的编号
     * @param companyRoleId          查找的范围单位的类别编号
     * @param pageNumber             分页页码
     * @param pageDataSize           每页展示的数量
     * @return 查找到的社区楼长集合与分页对象
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(SystemUser systemUser, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType, DormitoryManager dormitoryManager, Serializable companyId, Serializable companyRoleId, Integer pageNumber, Integer pageDataSize) throws Exception;

    /**
     * 通过社区编号查找最后一个编号
     *
     * @param communityId     社区编号
     * @param communityName   社区名称
     * @param subdistrictName 街道办事处名称
     * @return 最后一个编号
     * @throws Exception SERVICE层异常
     */
    String find(Serializable communityId, String communityName, String subdistrictName) throws Exception;

    /**
     * 获取社区楼长柱状图数据
     *
     * @param systemUser             正在登录中的系统用户对象
     * @param companyId              单位编号
     * @param companyType            单位类型
     * @param typeParam              类型参数
     * @param systemCompanyType      系统单位类型编号
     * @param communityCompanyType   社区单位类型编号
     * @param subdistrictCompanyType 街道单位类型编号
     * @return 柱状图数据
     * @throws Exception SERVICE层异常
     */
    Map<String, Object> find(SystemUser systemUser, Serializable companyId, Serializable companyType, Boolean typeParam, Serializable systemCompanyType, Serializable communityCompanyType, Serializable subdistrictCompanyType) throws Exception;

    /**
     * 获取文件标题
     *
     * @return 文件标题
     */
    String getFileTitle();

    /**
     * 获取Excel数据处理匿名类
     *
     * @return Excel数据处理匿名类
     */
    ExcelUtils.DataHandler getExcelDataHandler();
}
