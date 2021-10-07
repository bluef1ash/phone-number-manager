package com.github.phonenumbermanager.configure;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.ConfigurationTypeEnum;
import com.github.phonenumbermanager.entity.*;
import com.github.phonenumbermanager.service.*;

/**
 * 数据库数据初始化
 *
 * @author 廿二月的天
 */
@Component
public class DatabaseInitializeRunner implements CommandLineRunner {
    @Resource
    private ConfigurationService configurationService;
    @Resource
    private UserPrivilegeService userPrivilegeService;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private SystemUserRoleService systemUserRoleService;
    private String administratorName;

    @Override
    public void run(String... args) {
        systemUerAndRoleDataInitialize();
        configurationDataInitialize();
        privilegeDataInitialize();
    }

    /**
     * 配置数据初始化
     */
    private void configurationDataInitialize() {
        if (configurationService.list().isEmpty()) {
            List<Configuration> configurations = new ArrayList<>();
            configurations.add((Configuration)new Configuration().setKey("system_administrator_id")
                .setType(ConfigurationTypeEnum.STRING).setValue(administratorName).setDescription("系统管理员用户名称")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("system_company_type")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("0").setDescription("系统用户角色编号").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("community_company_type")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("1").setDescription("社区用户角色编号").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("subdistrict_company_type")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("2").setDescription("街道用户角色编号").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_title_up")
                .setType(ConfigurationTypeEnum.STRING).setValue("附件2").setDescription("电话库Excel表标题上文字")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_title")
                .setType(ConfigurationTypeEnum.STRING).setValue("“评社区”活动电话库登记表").setDescription("电话库Excel表标题")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("read_resident_excel_start_row_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("4").setDescription("读取电话库Excel表开始行号，从内容开始的行数加，包含开始行数")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_subdistrict_name_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("0").setDescription("电话库Excel表中“街道”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_community_name_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("1").setDescription("电话库Excel表中“社区”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_name_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("2").setDescription("电话库Excel表中“户主姓名”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_address_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("3").setDescription("电话库Excel表中“家庭住址”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_phone1_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("4").setDescription("电话库Excel表中“联系方式一”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_phone2_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("5").setDescription("电话库Excel表中“联系方式二”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_resident_phone3_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("6").setDescription("电话库Excel表中“联系方式三”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations
                .add((Configuration)new Configuration().setKey("excel_resident_subcontractor_name_cell_number")
                    .setType(ConfigurationTypeEnum.NUMBER).setValue("7").setDescription("电话库Excel表中“分包人”所在列的位置序号，从0开始")
                    .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_title")
                .setType(ConfigurationTypeEnum.STRING).setValue("${subdistrictName}街道（园区）社区楼片长花名册")
                .setDescription("楼长Excel表标题").setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_title_up")
                .setType(ConfigurationTypeEnum.STRING).setValue("附件1").setDescription("楼长Excel表标题上文字")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("read_dormitory_excel_start_row_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("6").setDescription("读取社区楼长Excel表开始行号，从内容开始的行数加，包含开始行数")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_community_name_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("1").setDescription("社区楼长Excel表中“社区”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_id_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("2").setDescription("社区楼长Excel表中“编号”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_name_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("3").setDescription("社区楼长Excel表中“楼片长姓名”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_gender_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("4").setDescription("社区楼长Excel表中“性别”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_birth_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("5").setDescription("社区楼长Excel表中“出生年月”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_political_status_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("6").setDescription("社区楼长Excel表中“政治面貌”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_work_status_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("7").setDescription("社区楼长Excel表中“工作状况”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_education_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("8").setDescription("社区楼长Excel表中“学历”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_address_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("9").setDescription("社区楼长Excel表中“家庭住址”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_manager_address_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("10").setDescription("社区楼长Excel表中“管理的地址”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_manager_count_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("11").setDescription("社区楼长Excel表中“管理的户数”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_telephone_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("12").setDescription("社区楼长Excel表中“手机号码”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("excel_dormitory_landline_cell_number")
                .setType(ConfigurationTypeEnum.NUMBER).setValue("13").setDescription("社区楼长Excel表中“座机号码”所在列的位置序号，从0开始")
                .setKeyIsChanged(false).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration()
                .setKey("excel_dormitory_subcontractor_name_cell_number").setType(ConfigurationTypeEnum.NUMBER)
                .setValue("14").setDescription("社区楼长Excel表中“分包人姓名”所在列的位置序号，从0开始").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration()
                .setKey("excel_dormitory_subcontractor_telephone_cell_number").setType(ConfigurationTypeEnum.NUMBER)
                .setValue("15").setDescription("社区楼长Excel表中“分包人手机号码”所在列的位置序号，从0开始").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurations.add((Configuration)new Configuration().setKey("system_is_active")
                .setType(ConfigurationTypeEnum.BOOLEAN).setValue("1").setDescription("系统是否开启").setKeyIsChanged(false)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            configurationService.saveBatch(configurations);
        }
    }

    /**
     * 用户权限数据初始化
     */
    private void privilegeDataInitialize() {
        if (userPrivilegeService.list().isEmpty()) {
            List<UserPrivilege> userPrivileges = new ArrayList<>();
            long topId = 0L;
            long indexId = IdWorker.getId();
            long communityResidentTopId = IdWorker.getId();
            long communityTopId = IdWorker.getId();
            long subdistrictTopId = IdWorker.getId();
            long systemTopId = IdWorker.getId();
            long communityResidentManagerId = IdWorker.getId();
            long dormitoryManagerManagerId = IdWorker.getId();
            long communityManagerId = IdWorker.getId();
            long subdistrictManagerId = IdWorker.getId();
            long userManagerId = IdWorker.getId();
            long systemManagerId = IdWorker.getId();
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setId(indexId).setName("首页相关").setDescription("indexController")
                    .setParentId(topId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(communityResidentTopId).setName("社区居民相关")
                .setDescription("communityResidentTitle").setParentId(topId).setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(communityTopId).setName("社区相关")
                .setDescription("communityTitle").setParentId(topId).setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(subdistrictTopId).setName("街道相关")
                .setDescription("subdistrictTitle").setParentId(topId).setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setId(systemTopId).setName("系统相关").setDescription("systemTitle")
                    .setParentId(topId).setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(communityResidentManagerId).setName("社区居民管理")
                .setDescription("communityResidentController").setParentId(communityResidentTopId)
                .setIconName("fa fa-phone").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(dormitoryManagerManagerId).setName("社区楼长管理")
                .setDescription("dormitoryManagerController").setParentId(communityResidentTopId)
                .setIconName("fa fa-university").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(communityManagerId).setName("社区管理")
                .setDescription("communityController").setParentId(communityTopId).setIconName("fa fa-building")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(subdistrictManagerId).setName("街道管理")
                .setDescription("subdistrictController").setParentId(subdistrictTopId).setIconName("fa fa-laptop")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(userManagerId).setName("用户和角色管理")
                .setDescription("userAndRoleController").setParentId(systemTopId).setIconName("fa fa-user")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setId(systemManagerId).setName("系统管理")
                .setDescription("systemController").setParentId(systemTopId).setIconName("fa fa-cog").setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("获取首页菜单栏内容").setDescription("getMenu")
                .setUri("/getmenu").setParentId(communityResidentTopId).setIconName("fa fa-list")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("获取图表数据").setDescription("getComputedCount")
                .setUri("/getcomputed").setParentId(communityResidentTopId).setIconName("fa fa-list")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("居民电话列表")
                .setDescription("communityResidentList").setUri("/resident").setParentId(communityResidentManagerId)
                .setIconName("fa fa-list").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加居民信息").setDescription("createCommunityResident")
                    .setUri("/resident/create").setParentId(communityResidentManagerId).setIconName("fa fa-plus")
                    .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("修改居民信息").setDescription("editCommunityResident")
                    .setUri("/resident/edit").setParentId(communityResidentManagerId)
                    .setIconName("fa fa-pencil-square-o").setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加居民信息处理")
                .setDescription("communityResidentCreateHandle").setUri("/resident")
                .setParentId(communityResidentManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加居民信息处理")
                .setDescription("communityResidentModifyHandle").setUri("/resident")
                .setParentId(communityResidentManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除居民信息")
                .setDescription("deleteCommunityResidentForAjax").setUri("/resident")
                .setParentId(communityResidentManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("导入居民信息进系统")
                .setDescription("communityResidentImportAsSystem").setUri("/resident/import")
                .setParentId(communityResidentManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("导出居民信息到Excel")
                .setDescription("communityResidentSaveAsExcel").setUri("/resident/download")
                .setParentId(communityResidentManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("使用AJAX技术加载社区居民列表")
                .setDescription("findCommunityResidentsForAjax").setUri("/resident/list")
                .setParentId(communityResidentManagerId).setIconName("fa fa-list")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("楼长信息列表")
                .setDescription("dormitoryManagerList").setUri("/dormitory").setParentId(dormitoryManagerManagerId)
                .setIconName("fa fa-list").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加楼长信息").setDescription("createDormitoryManager")
                    .setUri("/dormitory/create").setParentId(dormitoryManagerManagerId).setIconName("fa fa-plus")
                    .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改楼长信息")
                .setDescription("editDormitoryManager").setUri("/dormitory/edit").setParentId(dormitoryManagerManagerId)
                .setIconName("fa fa-pencil-square-o").setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加楼长信息处理")
                .setDescription("dormitoryManagerCreateHandle").setUri("/dormitory")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改楼长信息处理")
                .setDescription("dormitoryManagerModifyHandle").setUri("/dormitory")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除楼长信息")
                .setDescription("deleteDormitoryManagerForAjax").setUri("/dormitory")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("导入楼长信息进系统")
                .setDescription("dormitoryManagerImportAsSystem").setUri("/dormitory/import")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("导出楼长信息到Excel")
                .setDescription("dormitoryManagerSaveAsExcel").setUri("/dormitory/download")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("使用AJAX技术加载社区楼长列表")
                .setDescription("findDormitoryManagersForAjax").setUri("/dormitory/list")
                .setParentId(dormitoryManagerManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("社区列表").setDescription("communityList")
                .setUri("/community").setParentId(communityManagerId).setIconName("fa fa-list").setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加社区信息").setDescription("createCommunity")
                .setUri("/community/create").setParentId(communityManagerId).setIconName("fa fa-plus")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改社区信息").setDescription("editCommunity")
                .setUri("/community/edit").setParentId(communityManagerId).setIconName("fa fa-pencil-square-o")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加社区信息处理")
                .setDescription("communityCreateHandle").setUri("/community").setParentId(communityManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改社区信息处理")
                .setDescription("communityModifyHandle").setUri("/community").setParentId(communityManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除社区信息")
                .setDescription("deleteCommunityForAjax").setUri("/community").setParentId(communityManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过街道编号使用AJAX技术列出社区居委会")
                .setDescription("findCommunitiesForAjax").setUri("/community/select").setParentId(communityManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("更改社区是否允许更删改信息")
                .setDescription("chooseSubmitForAjax").setUri("/community/choose_submit")
                .setParentId(communityManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过社区编号使用Ajax技术加载社区及所属街道")
                .setDescription("loadCommunityForAjax").setUri("/community/load").setParentId(communityManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("社区分包人列表").setDescription("subcontractorList")
                .setUri("/community/subcontractor").setParentId(communityManagerId).setIconName("fa fa-list")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加社区分包人信息").setDescription("createSubcontractor")
                    .setUri("/community/subcontractor/create").setParentId(communityManagerId).setIconName("fa fa-plus")
                    .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("修改社区分包人信息").setDescription("editSubcontractor")
                    .setUri("/community/subcontractor/edit").setParentId(communityManagerId)
                    .setIconName("fa fa-pencil-square-o").setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加社区分包人信息处理")
                .setDescription("subcontractorCreateHandle").setUri("/community/subcontractor")
                .setParentId(communityManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改社区分包人信息处理")
                .setDescription("subcontractorModifyHandle").setUri("/community/subcontractor")
                .setParentId(communityManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除社区分包人信息")
                .setDescription("deleteSubcontractorForAjax").setUri("/community/subcontractor")
                .setParentId(communityManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("街道列表").setDescription("subdistrictList")
                .setUri("/subdistrict").setParentId(subdistrictManagerId).setIconName("fa fa-list").setIsDisplay(true)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加街道信息").setDescription("createSubdistrict")
                .setUri("/subdistrict/create").setParentId(subdistrictManagerId).setIconName("fa fa-plus")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改街道信息").setDescription("editSubdistrict")
                .setUri("/subdistrict/edit").setParentId(subdistrictManagerId).setIconName("fa fa-pencil-square-o")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加、修改街道信息处理")
                .setDescription("subdistrictCreateOrEditHandle").setUri("/subdistrict")
                .setParentId(subdistrictManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除街道信息")
                .setDescription("deleteSubdistrictForAjax").setUri("/subdistrict").setParentId(subdistrictManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过Ajax技术获取街道信息")
                .setDescription("getSubdistrictForAjax").setUri("/subdistrict/load").setParentId(subdistrictManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("系统用户列表").setDescription("systemUserList")
                .setUri("/system/user_role/user").setParentId(userManagerId).setIconName("fa fa-list")
                .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("系统用户锁定与解锁")
                .setDescription("systemUserLockedForAjax").setUri("/system/user_role/user/lock")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加系统用户").setDescription("createSystemUser")
                .setUri("/system/user_role/user/create").setParentId(userManagerId).setIconName("fa fa-plus")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改系统用户").setDescription("editSystemUser")
                .setUri("/system/user_role/user/edit").setParentId(userManagerId).setIconName("fa fa-pencil-square-o")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加处理系统用户")
                .setDescription("systemUserCreateHandle").setUri("/system/user_role/user").setParentId(userManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改处理系统用户")
                .setDescription("systemUserModifyHandle").setUri("/system/user_role/user").setParentId(userManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除系统用户")
                .setDescription("deleteSystemUserForAjax").setUri("/system/user_role/user").setParentId(userManagerId)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add(
                (UserPrivilege)new UserPrivilege().setName("使用Ajax技术获取所有系统用户").setDescription("getSystemUsersForAjax")
                    .setUri("/system/user_role/user/load").setParentId(userManagerId).setIconName("fa fa-list")
                    .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("系统用户角色列表")
                .setDescription("systemUserRoleList").setUri("/system/user_role/role").setParentId(userManagerId)
                .setIconName("fa fa-list").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加系统用户角色").setDescription("createSystemUserRole")
                    .setUri("/system/user_role/role/create").setParentId(userManagerId).setIconName("fa fa-plus")
                    .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改系统用户角色")
                .setDescription("editSystemUserRole").setUri("/system/user_role/user/edit").setParentId(userManagerId)
                .setIconName("fa fa-pencil-square-o").setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加处理系统用户角色")
                .setDescription("systemUserRoleCreateHandle").setUri("/system/user_role/role")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改处理系统用户角色")
                .setDescription("systemUserRoleModifyHandle").setUri("/system/user_role/role")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除系统用户角色")
                .setDescription("deleteSystemUserRoleForAjax").setUri("/system/user_role/role")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("系统用户权限列表").setDescription("systemUserPrivilegeList")
                    .setUri("/system/user_role/privilege").setParentId(userManagerId).setIconName("fa fa-list")
                    .setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加系统用户权限").setDescription("createSystemUserPrivilege")
                    .setUri("/system/user_role/privilege/create").setParentId(userManagerId).setIconName("fa fa-plus")
                    .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("修改系统用户权限").setDescription("editSystemUserPrivilege")
                    .setUri("/system/user_role/privilege/edit").setParentId(userManagerId)
                    .setIconName("fa fa-pencil-square-o").setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加处理系统用户权限")
                .setDescription("systemUserPrivilegeCreateHandle").setUri("/system/user_role/privilege")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改处理系统用户权限")
                .setDescription("systemUserPrivilegeModifyHandle").setUri("/system/user_role/privilege")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除系统用户权限")
                .setDescription("deleteSystemUserPrivilegeForAjax").setUri("/system/user_role/privilege")
                .setParentId(userManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术获取系统用户角色拥有的权限")
                .setDescription("getPrivilegesByRoleIdForAjax").setUri("/system/user_role/privilege/load")
                .setParentId(userManagerId).setIconName("fa fa-list")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("系统配置列表")
                .setDescription("systemConfigurationList").setUri("/system/configuration").setParentId(systemManagerId)
                .setIconName("fa fa-list").setIsDisplay(true).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges
                .add((UserPrivilege)new UserPrivilege().setName("添加系统配置").setDescription("createConfiguration")
                    .setUri("/system/configuration/create").setParentId(systemManagerId).setIconName("fa fa-plus")
                    .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                    .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("修改系统配置").setDescription("editConfiguration")
                .setUri("/system/configuration/edit").setParentId(systemManagerId).setIconName("fa fa-pencil-square-o")
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加处理系统配置")
                .setDescription("systemConfigurationCreateHandle").setUri("/system/configuration")
                .setParentId(systemManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("添加与修改处理系统配置")
                .setDescription("systemConfigurationModifyHandle").setUri("/system/configuration")
                .setParentId(systemManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivileges.add((UserPrivilege)new UserPrivilege().setName("通过AJAX技术删除系统配置")
                .setDescription("deleteConfigurationForAjax").setUri("/system/configuration")
                .setParentId(systemManagerId).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
            userPrivilegeService.saveBatch(userPrivileges);
        }
    }

    /**
     * 系统用户与角色数据初始化
     */
    private void systemUerAndRoleDataInitialize() {
        Long administratorId = null;
        if (systemUserService.list().isEmpty()) {
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername("admin").setPassword(new BCryptPasswordEncoder().encode("admin888"))
                .setAccountExpireTime(SystemConstant.DATABASE_DATETIME_MAX)
                .setCredentialExpireTime(SystemConstant.DATABASE_DATETIME_MAX)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN);
            systemUserService.save(systemUser);
            administratorId = systemUser.getId();
            administratorName = systemUser.getUsername();
        }
        UserRole systemUserRole = new UserRole();
        if (userRoleService.list().isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            systemUserRole.setName("ROLE_system").setDescription("系统管理员").setParentId(0L)
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN);
            userRoles.add(systemUserRole);
            UserRole subdistrictUserRole = new UserRole();
            subdistrictUserRole.setName("ROLE_subdistrict").setDescription("街道管理员").setParentId(systemUserRole.getId())
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN);
            userRoles.add(subdistrictUserRole);
            UserRole communityUserRole = new UserRole();
            communityUserRole.setName("ROLE_community").setDescription("社区管理员").setParentId(subdistrictUserRole.getId())
                .setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN);
            userRoles.add(communityUserRole);
            userRoleService.saveBatch(userRoles);
        }
        if (systemUserRoleService.list().isEmpty()) {
            systemUserRoleService.save((UserRoleRelation)new UserRoleRelation().setUserId(administratorId)
                .setRoleId(systemUserRole.getId()).setCreateTime(SystemConstant.DATABASE_DATETIME_MIN)
                .setUpdateTime(SystemConstant.DATABASE_DATETIME_MIN));
        }
    }
}
