package com.github.phonenumbermanager.configure;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.ConfigurationFieldTypeEnum;
import com.github.phonenumbermanager.constant.enums.MenuTypeEnum;
import com.github.phonenumbermanager.constant.enums.PhoneTypeEnum;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;
import com.github.phonenumbermanager.service.PhoneNumberService;
import com.github.phonenumbermanager.service.SystemPermissionService;
import com.github.phonenumbermanager.service.SystemUserService;

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
    private SystemPermissionService systemPermissionService;
    @Resource
    private SystemUserService systemUserService;
    @Resource
    private PhoneNumberService phoneNumberService;
    private Long administratorId;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run(String... args) {
        systemUerAndRoleDataInitialize();
        configurationDataInitialize();
        systemPermissionDataInitialize();
    }

    /**
     * 配置数据初始化
     */
    private void configurationDataInitialize() {
        if (configurationService.list().isEmpty()) {
            List<Configuration> configurations = new ArrayList<>();
            configurations.add((Configuration)new Configuration().setTitle("系统管理员用户编号")
                .setName("system_administrator_id").setFieldType(ConfigurationFieldTypeEnum.NUMBER)
                .setContent(String.valueOf(administratorId)).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_title_up")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("附件2").setTitle("电话库Excel表标题上文字")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_title")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("“评社区”活动电话库登记表").setTitle("电话库Excel表标题")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("read_resident_excel_start_row_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("读取电话库Excel表开始行号，从内容开始的行数加，包含开始行数").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_subdistrict_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("0").setTitle("电话库Excel表中“街道”所在列的位置序号，从0开始")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_community_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("1").setTitle("电话库Excel表中“社区”所在列的位置序号，从0开始")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("2")
                .setTitle("电话库Excel表中“户主姓名”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("3")
                .setTitle("电话库Excel表中“家庭住址”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone1_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("电话库Excel表中“联系方式一”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone2_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("5")
                .setTitle("电话库Excel表中“联系方式二”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone3_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("6")
                .setTitle("电话库Excel表中“联系方式三”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations
                .add((Configuration)new Configuration().setName("excel_resident_subcontractor_name_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("7")
                    .setTitle("电话库Excel表中“分包人”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_title")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("${subdistrictName}街道（园区）社区楼片长花名册")
                .setTitle("楼长Excel表标题").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_title_up")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("附件1").setTitle("楼长Excel表标题上文字")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("read_dormitory_excel_start_row_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("6")
                .setTitle("读取社区楼长Excel表开始行号，从内容开始的行数加，包含开始行数").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_community_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("1")
                .setTitle("社区楼长Excel表中“社区”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_id_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("2")
                .setTitle("社区楼长Excel表中“编号”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("3")
                .setTitle("社区楼长Excel表中“楼片长姓名”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_gender_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("社区楼长Excel表中“性别”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_birth_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("5")
                .setTitle("社区楼长Excel表中“出生年月”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations
                .add((Configuration)new Configuration().setName("excel_dormitory_political_status_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("6")
                    .setTitle("社区楼长Excel表中“政治面貌”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_work_status_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("7")
                .setTitle("社区楼长Excel表中“工作状况”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_education_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("8")
                .setTitle("社区楼长Excel表中“学历”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("9")
                .setTitle("社区楼长Excel表中“家庭住址”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_manager_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("10")
                .setTitle("社区楼长Excel表中“管理的地址”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_manager_count_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("11")
                .setTitle("社区楼长Excel表中“管理的户数”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_telephone_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("12")
                .setTitle("社区楼长Excel表中“手机号码”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_landline_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("13")
                .setTitle("社区楼长Excel表中“座机号码”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations
                .add((Configuration)new Configuration().setName("excel_dormitory_subcontractor_name_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("14")
                    .setTitle("社区楼长Excel表中“分包人姓名”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations
                .add((Configuration)new Configuration().setName("excel_dormitory_subcontractor_telephone_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("15")
                    .setTitle("社区楼长Excel表中“分包人手机号码”所在列的位置序号，从0开始").setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurations.add((Configuration)new Configuration().setName("system_is_active")
                .setFieldType(ConfigurationFieldTypeEnum.BOOLEAN).setContent("1").setTitle("系统是否开启")
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            configurationService.saveBatch(configurations);
        }
    }

    /**
     * 用户权限数据初始化
     */
    private void systemPermissionDataInitialize() {
        if (systemPermissionService.list().isEmpty()) {
            List<SystemPermission> systemPermissions = new ArrayList<>();
            long topId = 0L;
            long indexId = IdWorker.getId();
            long communityResidentManagerId = IdWorker.getId();
            long dormitoryManagerManagerId = IdWorker.getId();
            long companyManagerId = IdWorker.getId();
            long userManagerId = IdWorker.getId();
            long systemManagerId = IdWorker.getId();
            // -------------- 顶级权限
            systemPermissions.add((SystemPermission)new SystemPermission().setId(indexId).setName("首页相关")
                .setFunctionName("indexController").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // ----------------二级
            systemPermissions.add((SystemPermission)new SystemPermission().setName("欢迎").setFunctionName("./welcome")
                .setUri("/welcome").setParentId(indexId).setIsDisplay(true).setLevel(1)
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setMenuType(MenuTypeEnum.FRONTEND)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setId(communityResidentManagerId)
                .setName("社区居民管理").setFunctionName("communityResidentController").setParentId(topId).setIsDisplay(true)
                .setLevel(0).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setId(dormitoryManagerManagerId)
                .setName("社区楼长管理").setFunctionName("dormitoryManagerController").setParentId(topId).setIsDisplay(true)
                .setLevel(0).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setId(companyManagerId).setName("单位管理")
                .setFunctionName("companyController").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setId(userManagerId).setName("系统用户与系统权限管理")
                .setFunctionName("userAndPermissionController").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setId(systemManagerId).setName("系统管理")
                .setFunctionName("systemController").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // -------------------首页
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("获取首页菜单栏内容").setFunctionName("getMenu")
                    .setUri("/menu").setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(indexId).setLevel(1)
                    .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("获取图表数据").setFunctionName("getComputedCount")
                    .setUri("/computed").setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(indexId)
                    .setLevel(1).setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // -----------------社区居民
            systemPermissions.add((SystemPermission)new SystemPermission().setName("居民电话列表")
                .setFunctionName("communityResidentList").setUri("/resident")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("居民电话列表")
                .setFunctionName("./resident").setUri("/resident").setHttpMethods(new HttpMethod[] {HttpMethod.GET})
                .setIsDisplay(true).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.FRONTEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过编号查找社区居民")
                .setFunctionName("getCommunityResidentById").setUri("/resident/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("添加居民信息处理")
                .setFunctionName("communityResidentCreateHandle").setUri("/resident")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改居民信息处理")
                .setFunctionName("communityResidentModifyHandle").setUri("/resident/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除居民信息")
                .setFunctionName("removeCommunityResident").setUri("/resident/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(communityResidentManagerId)
                .setLevel(1).setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("导入居民信息进系统")
                .setFunctionName("communityResidentImportAsSystem").setUri("/resident/import/{streetId}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("导出居民信息到Excel")
                .setFunctionName("communityResidentSaveAsExcel").setUri("/resident/download")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(communityResidentManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // ----------------楼片长
            systemPermissions.add((SystemPermission)new SystemPermission().setName("楼长信息列表")
                .setFunctionName("dormitoryManagerList").setUri("/dormitory")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过编号查找社区楼长")
                .setFunctionName("getDormitoryManagerById").setUri("/dormitory/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("楼长信息列表")
                .setFunctionName("./dormitory").setUri("/dormitory").setHttpMethods(new HttpMethod[] {HttpMethod.GET})
                .setParentId(dormitoryManagerManagerId).setLevel(1).setIsDisplay(true)
                .setMenuType(MenuTypeEnum.FRONTEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("添加楼长信息处理")
                .setFunctionName("dormitoryManagerCreateHandle").setUri("/dormitory")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改楼长信息处理")
                .setFunctionName("dormitoryManagerModifyHandle").setUri("/dormitory/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除楼长信息")
                .setFunctionName("removeDormitoryManager").setUri("/dormitory/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("导入楼长信息进系统")
                .setFunctionName("dormitoryManagerImportAsSystem").setUri("/dormitory/import/{streetId}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("导出楼长信息到Excel")
                .setFunctionName("dormitoryManagerSaveAsExcel").setUri("/dormitory/download")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(dormitoryManagerManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // ----------------单位
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("单位列表").setFunctionName("companyList")
                    .setUri("/company").setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(companyManagerId)
                    .setLevel(1).setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过单位编号获取")
                .setFunctionName("getCompanyById").setUri("/company/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(companyManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add(
                (SystemPermission)new SystemPermission().setName("单位列表").setFunctionName("./company").setUri("/company")
                    .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(companyManagerId).setIsDisplay(true)
                    .setLevel(1).setMenuType(MenuTypeEnum.FRONTEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("添加单位信息处理").setFunctionName("companyCreateHandle")
                    .setUri("/company").setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(companyManagerId)
                    .setLevel(1).setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改单位信息处理")
                .setFunctionName("companyModifyHandle").setUri("/company/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(companyManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除单位信息")
                .setFunctionName("removeCompany").setUri("/company")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(companyManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // --------------------系统用户
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("系统用户列表").setFunctionName("systemUserList")
                    .setUri("/system/user").setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId)
                    .setLevel(1).setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过系统用户编号查找")
                .setFunctionName("getSystemUserById").setUri("/system/user/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("系统用户列表").setFunctionName("./user")
                .setUri("/system/user").setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId)
                .setLevel(1).setIsDisplay(true).setMenuType(MenuTypeEnum.FRONTEND)
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("单独字段修改系统用户")
                .setFunctionName("systemUserModifyHandlePatch").setUri("/system/user/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PATCH}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("添加处理系统用户")
                .setFunctionName("systemUserCreateHandle").setUri("/system/user")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改处理系统用户")
                .setFunctionName("systemUserModifyHandle").setUri("/system/user/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除系统用户")
                .setFunctionName("removeSystemUser").setUri("/system/user")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过单位编号加载系统用户")
                .setFunctionName("loadSystemUserByCompanyId").setUri("/user/company/{companyId}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // -----------------系统权限
            systemPermissions.add((SystemPermission)new SystemPermission().setName("系统权限列表")
                .setFunctionName("systemSystemPermissionList").setUri("/system/permission")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过编号获取系统权限")
                .setFunctionName("getSystemPermissionById").setUri("/permission/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("系统权限列表").setFunctionName("./permission")
                    .setUri("/system/permission").setHttpMethods(new HttpMethod[] {HttpMethod.GET})
                    .setParentId(userManagerId).setLevel(1).setIsDisplay(true).setMenuType(MenuTypeEnum.FRONTEND)
                    .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("添加处理系统权限")
                .setFunctionName("systemPermissionsCreateHandle").setUri("/system/permission")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改处理系统权限")
                .setFunctionName("systemPermissionsModifyHandle").setUri("/system/permission/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除系统权限")
                .setFunctionName("removeSystemPermission").setUri("/system/permission")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("获取系统用户单位拥有的权限")
                .setFunctionName("getPermissionsByCompanyId").setUri("/system/permission/company/{companyId}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(userManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            // ----------------系统配置
            systemPermissions.add((SystemPermission)new SystemPermission().setName("系统配置列表")
                .setFunctionName("systemConfigurationList").setUri("/system/configuration")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(systemManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("通过系统配置项编号查找")
                .setFunctionName("getConfigurationById").setUri("/system/configuration/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.GET}).setParentId(systemManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions
                .add((SystemPermission)new SystemPermission().setName("系统配置列表").setFunctionName("./configuration")
                    .setUri("/system/configuration").setHttpMethods(new HttpMethod[] {HttpMethod.GET})
                    .setParentId(systemManagerId).setLevel(1).setIsDisplay(true).setMenuType(MenuTypeEnum.FRONTEND)
                    .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                    .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("添加处理系统配置")
                .setFunctionName("systemConfigurationCreateHandle").setUri("/system/configuration")
                .setHttpMethods(new HttpMethod[] {HttpMethod.POST}).setParentId(systemManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("修改处理系统配置")
                .setFunctionName("systemConfigurationModifyHandle").setUri("/system/configuration/{id}")
                .setHttpMethods(new HttpMethod[] {HttpMethod.PUT}).setParentId(systemManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissions.add((SystemPermission)new SystemPermission().setName("删除系统配置")
                .setFunctionName("removeConfigurationById").setUri("/system/configuration")
                .setHttpMethods(new HttpMethod[] {HttpMethod.DELETE}).setParentId(systemManagerId).setLevel(1)
                .setMenuType(MenuTypeEnum.BACKEND).setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME));
            systemPermissionService.saveBatch(systemPermissions);
        }
    }

    /**
     * 系统用户与角色数据初始化
     */
    private void systemUerAndRoleDataInitialize() {
        if (systemUserService.list().isEmpty()) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber("13012345678").setPhoneType(PhoneTypeEnum.MOBILE);
            phoneNumberService.save(phoneNumber);
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername("admin").setPassword("admin888")
                .setAccountExpireTime(SystemConstant.DATABASE_MAX_DATETIME).setPhoneNumberId(phoneNumber.getId())
                .setCreateTime(SystemConstant.DATABASE_MIX_DATETIME)
                .setUpdateTime(SystemConstant.DATABASE_MIX_DATETIME);
            systemUserService.save(systemUser);
            administratorId = systemUser.getId();
        }
    }
}
