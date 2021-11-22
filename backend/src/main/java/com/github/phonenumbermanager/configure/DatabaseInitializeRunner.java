package com.github.phonenumbermanager.configure;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.phonenumbermanager.constant.enums.ConfigurationFieldTypeEnum;
import com.github.phonenumbermanager.constant.enums.HttpMethodEnum;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.entity.SystemPermission;
import com.github.phonenumbermanager.entity.SystemUser;
import com.github.phonenumbermanager.service.ConfigurationService;
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
    private Long administratorId;

    @Override
    public void run(String... args) {
        configurationDataInitialize();
        privilegeDataInitialize();
        systemUerAndRoleDataInitialize();
    }

    /**
     * 配置数据初始化
     */
    private void configurationDataInitialize() {
        if (configurationService.list().isEmpty()) {
            List<Configuration> configurations = new ArrayList<>();
            configurations
                .add((Configuration)new Configuration().setTitle("系统管理员用户编号").setName("system_administrator_id")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent(String.valueOf(administratorId))
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_title_up")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("附件2").setTitle("电话库Excel表标题上文字")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_title")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("“评社区”活动电话库登记表").setTitle("电话库Excel表标题")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("read_resident_excel_start_row_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("读取电话库Excel表开始行号，从内容开始的行数加，包含开始行数").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_subdistrict_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("0").setTitle("电话库Excel表中“街道”所在列的位置序号，从0开始")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_community_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("1").setTitle("电话库Excel表中“社区”所在列的位置序号，从0开始")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("2")
                .setTitle("电话库Excel表中“户主姓名”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("3")
                .setTitle("电话库Excel表中“家庭住址”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone1_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("电话库Excel表中“联系方式一”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone2_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("5")
                .setTitle("电话库Excel表中“联系方式二”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_resident_phone3_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("6")
                .setTitle("电话库Excel表中“联系方式三”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations
                .add((Configuration)new Configuration().setName("excel_resident_subcontractor_name_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("7")
                    .setTitle("电话库Excel表中“分包人”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_title")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("${subdistrictName}街道（园区）社区楼片长花名册")
                .setTitle("楼长Excel表标题").setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_title_up")
                .setFieldType(ConfigurationFieldTypeEnum.STRING).setContent("附件1").setTitle("楼长Excel表标题上文字")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("read_dormitory_excel_start_row_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("6")
                .setTitle("读取社区楼长Excel表开始行号，从内容开始的行数加，包含开始行数").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_community_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("1")
                .setTitle("社区楼长Excel表中“社区”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_id_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("2")
                .setTitle("社区楼长Excel表中“编号”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_name_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("3")
                .setTitle("社区楼长Excel表中“楼片长姓名”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_gender_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("4")
                .setTitle("社区楼长Excel表中“性别”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_birth_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("5")
                .setTitle("社区楼长Excel表中“出生年月”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration()
                .setName("excel_dormitory_political_status_cell_number").setFieldType(ConfigurationFieldTypeEnum.NUMBER)
                .setContent("6").setTitle("社区楼长Excel表中“政治面貌”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_work_status_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("7")
                .setTitle("社区楼长Excel表中“工作状况”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_education_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("8")
                .setTitle("社区楼长Excel表中“学历”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("9")
                .setTitle("社区楼长Excel表中“家庭住址”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_manager_address_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("10")
                .setTitle("社区楼长Excel表中“管理的地址”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_manager_count_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("11")
                .setTitle("社区楼长Excel表中“管理的户数”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_telephone_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("12")
                .setTitle("社区楼长Excel表中“手机号码”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("excel_dormitory_landline_cell_number")
                .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("13")
                .setTitle("社区楼长Excel表中“座机号码”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            configurations
                .add((Configuration)new Configuration().setName("excel_dormitory_subcontractor_name_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("14")
                    .setTitle("社区楼长Excel表中“分包人姓名”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            configurations
                .add((Configuration)new Configuration().setName("excel_dormitory_subcontractor_telephone_cell_number")
                    .setFieldType(ConfigurationFieldTypeEnum.NUMBER).setContent("15")
                    .setTitle("社区楼长Excel表中“分包人手机号码”所在列的位置序号，从0开始").setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            configurations.add((Configuration)new Configuration().setName("system_is_active")
                .setFieldType(ConfigurationFieldTypeEnum.BOOLEAN).setContent("1").setTitle("系统是否开启")
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            configurationService.saveBatch(configurations);
        }
    }

    /**
     * 用户权限数据初始化
     */
    private void privilegeDataInitialize() {
        if (systemPermissionService.list().isEmpty()) {
            List<SystemPermission> systemPermission = new ArrayList<>();
            long topId = 0L;
            long indexId = IdWorker.getId();
            long communityResidentTopId = IdWorker.getId();
            long companyTopId = IdWorker.getId();
            long systemTopId = IdWorker.getId();
            long communityResidentManagerId = IdWorker.getId();
            long dormitoryManagerManagerId = IdWorker.getId();
            long companyManagerId = IdWorker.getId();
            long userManagerId = IdWorker.getId();
            long systemManagerId = IdWorker.getId();
            long accountManagerId = IdWorker.getId();
            // -------------- 顶级权限
            systemPermission.add((SystemPermission)new SystemPermission().setId(indexId).setName("首页相关")
                .setFunctionName("indexController").setParentId(topId).setLevel(0).setIsDisplay(true)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(communityResidentTopId)
                .setName("社区居民相关").setFunctionName("communityResidentTitle").setParentId(topId).setIsDisplay(true)
                .setLevel(0).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(companyTopId).setName("单位相关")
                .setFunctionName("communityTitle").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(systemTopId).setName("系统相关")
                .setFunctionName("systemTitle").setParentId(topId).setIsDisplay(true).setLevel(0)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // ----------------二级
            systemPermission.add((SystemPermission)new SystemPermission().setId(communityResidentManagerId)
                .setName("社区居民管理").setFunctionName("communityResidentController").setParentId(communityResidentTopId)
                .setIconName("fa fa-phone").setIsDisplay(true).setLevel(1).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(dormitoryManagerManagerId)
                .setName("社区楼长管理").setFunctionName("dormitoryManagerController").setParentId(communityResidentTopId)
                .setIconName("fa fa-university").setIsDisplay(true).setLevel(1).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(companyManagerId).setName("单位管理")
                .setFunctionName("companyController").setParentId(companyTopId).setIconName("fa fa-building")
                .setIsDisplay(true).setLevel(1).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(userManagerId).setName("系统用户与系统权限管理")
                .setFunctionName("userAndPermissionController").setParentId(systemTopId).setIconName("fa fa-user")
                .setIsDisplay(true).setLevel(1).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setId(systemManagerId).setName("系统管理")
                .setFunctionName("systemController").setParentId(systemTopId).setIconName("fa fa-cog")
                .setIsDisplay(true).setLevel(1).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // -------------------三级
            // -------------------首页
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("获取首页菜单栏内容").setFunctionName("getMenu")
                    .setUri("/get-menu").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(indexId).setIconName("fa fa-list").setLevel(1).setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("获取图表数据").setFunctionName("getComputedCount")
                    .setUri("/get-computed").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(indexId).setIconName("fa fa-list").setLevel(1).setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            // -----------------社区居民
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("居民电话列表").setFunctionName("communityResidentList")
                    .setUri("/resident").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(communityResidentManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("通过编号查找社区居民")
                .setFunctionName("getCommunityResidentById").setUri("/resident/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(communityResidentManagerId)
                .setIconName("fa fa-list").setLevel(2).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加居民信息处理")
                .setFunctionName("communityResidentCreateHandle").setUri("/resident")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(communityResidentManagerId)
                .setIsDisplay(true).setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("修改居民信息处理")
                .setFunctionName("communityResidentModifyHandle").setUri("/resident")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(communityResidentManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除居民信息")
                .setFunctionName("removeCommunityResident").setUri("/resident/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(communityResidentManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("导入居民信息进系统")
                .setFunctionName("communityResidentImportAsSystem").setUri("/resident/import/{streetId}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(communityResidentManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("导出居民信息到Excel")
                .setFunctionName("communityResidentSaveAsExcel").setUri("/resident/download")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(communityResidentManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // ----------------楼片长
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("楼长信息列表").setFunctionName("dormitoryManagerList")
                    .setUri("/dormitory").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(dormitoryManagerManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("通过编号查找社区楼长")
                .setFunctionName("getDormitoryManagerById").setUri("/dormitory/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(dormitoryManagerManagerId)
                .setIconName("fa fa-list").setIsDisplay(false).setLevel(2).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加楼长信息处理")
                .setFunctionName("dormitoryManagerCreateHandle").setUri("/dormitory")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(dormitoryManagerManagerId)
                .setIsDisplay(true).setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("修改楼长信息处理")
                .setFunctionName("dormitoryManagerModifyHandle").setUri("/dormitory")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(dormitoryManagerManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除楼长信息")
                .setFunctionName("removeDormitoryManager").setUri("/dormitory/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(dormitoryManagerManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("导入楼长信息进系统")
                .setFunctionName("dormitoryManagerImportAsSystem").setUri("/dormitory/import/{streetId}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(dormitoryManagerManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("导出楼长信息到Excel")
                .setFunctionName("dormitoryManagerSaveAsExcel").setUri("/dormitory/download")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(dormitoryManagerManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // ----------------单位
            systemPermission.add((SystemPermission)new SystemPermission().setName("单位列表").setFunctionName("companyList")
                .setUri("/company").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                .setParentId(companyManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("通过单位编号获取").setFunctionName("getCompanyById")
                    .setUri("/company/{id}").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(companyManagerId).setIconName("fa fa-list").setIsDisplay(false).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加单位信息处理")
                .setFunctionName("communityCreateHandle").setUri("/company")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(companyManagerId)
                .setIsDisplay(true).setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("修改单位信息处理")
                .setFunctionName("communityModifyHandle").setUri("/company")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(companyManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除单位信息")
                .setFunctionName("removeCompany").setUri("/company")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(companyManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // --------------------系统用户
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("系统用户列表").setFunctionName("systemUserList")
                    .setUri("/system/user").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(userManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("系统用户锁定与解锁")
                .setFunctionName("systemUserLocked").setUri("/system/user/lock/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission
                .add((SystemPermission)new SystemPermission().setName("通过系统用户编号查找").setFunctionName("getSystemUserById")
                    .setUri("/system/user/{id}").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(userManagerId).setIconName("fa fa-list").setLevel(2).setCreateTime(LocalDateTime.MIN)
                    .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加处理系统用户")
                .setFunctionName("systemUserCreateHandle").setUri("/system/user")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("修改处理系统用户")
                .setFunctionName("systemUserModifyHandle").setUri("/system/user")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除系统用户")
                .setFunctionName("removeSystemUser").setUri("/system/user")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("通过单位编号加载系统用户")
                .setFunctionName("loadSystemUserByCompanyId").setUri("/user/company/{companyId}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(userManagerId)
                .setIconName("fa fa-list").setLevel(2).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            // -----------------系统权限
            systemPermission.add(
                (SystemPermission)new SystemPermission().setName("系统权限列表").setFunctionName("systemSystemPermissionList")
                    .setUri("/system/permission").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(userManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("通过编号获取系统权限")
                .setFunctionName("getSystemUserPrivilegeById").setUri("/permission/{id}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(userManagerId)
                .setIconName("fa fa-list").setIsDisplay(true).setLevel(2).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加处理系统权限")
                .setFunctionName("systemPermissionCreateHandle").setUri("/system/permission")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(userManagerId)
                .setIsDisplay(true).setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("修改处理系统权限")
                .setFunctionName("systemPermissionModifyHandle").setUri("/system/permission")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除系统权限")
                .setFunctionName("removeSystemPermission").setUri("/system/permission")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(userManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("获取系统用户单位拥有的权限")
                .setFunctionName("getPermissionsByCompanyId").setUri("/system/permission/company/{companyId}")
                .setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue())).setParentId(userManagerId)
                .setIconName("fa fa-list").setLevel(2).setCreateTime(LocalDateTime.MIN)
                .setUpdateTime(LocalDateTime.MIN));
            // ----------------系统配置
            systemPermission.add(
                (SystemPermission)new SystemPermission().setName("系统配置列表").setFunctionName("systemConfigurationList")
                    .setUri("/system/configuration").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(systemManagerId).setIconName("fa fa-list").setIsDisplay(true).setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add(
                (SystemPermission)new SystemPermission().setName("通过系统配置项编号查找").setFunctionName("getConfigurationById")
                    .setUri("/system/configuration/{id}").setHttpMethod(String.valueOf(HttpMethodEnum.GET.getValue()))
                    .setParentId(systemManagerId).setIconName("fa fa-pencil-square-o").setLevel(2)
                    .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加处理系统配置")
                .setFunctionName("systemConfigurationCreateHandle").setUri("/system/configuration")
                .setHttpMethod(String.valueOf(HttpMethodEnum.POST.getValue())).setParentId(systemManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("添加与修改处理系统配置")
                .setFunctionName("systemConfigurationModifyHandle").setUri("/system/configuration")
                .setHttpMethod(String.valueOf(HttpMethodEnum.PUT.getValue())).setParentId(systemManagerId).setLevel(2)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermission.add((SystemPermission)new SystemPermission().setName("删除系统配置")
                .setFunctionName("removeConfigurationById").setUri("/system/configuration")
                .setHttpMethod(String.valueOf(HttpMethodEnum.DELETE.getValue())).setParentId(systemManagerId)
                .setLevel(2).setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            // -------------账号管理
            systemPermission.add((SystemPermission)new SystemPermission().setId(accountManagerId).setName("系统账号管理")
                .setFunctionName("accountController").setUri("/account").setParentId(systemManagerId).setLevel(1)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN));
            systemPermissionService.saveBatch(systemPermission);
        }
    }

    /**
     * 系统用户与角色数据初始化
     */
    private void systemUerAndRoleDataInitialize() {
        if (systemUserService.list().isEmpty()) {
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername("admin").setPassword(new BCryptPasswordEncoder().encode("admin888"))
                .setAccountExpireTime(LocalDateTime.MAX).setCredentialExpireTime(LocalDateTime.MIN)
                .setCreateTime(LocalDateTime.MIN).setUpdateTime(LocalDateTime.MIN);
            systemUserService.save(systemUser);
            administratorId = systemUser.getId();
        }
    }
}
