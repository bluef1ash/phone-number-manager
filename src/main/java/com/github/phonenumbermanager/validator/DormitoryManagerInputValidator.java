package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.entity.DormitoryManager;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.DormitoryManagerService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 社区楼长添加/更新验证
 *
 * @author 廿二月的天
 */
public class DormitoryManagerInputValidator extends BaseInputValidator<DormitoryManager> implements Validator {
    private DormitoryManagerService dormitoryManagerService;

    public DormitoryManagerInputValidator(DormitoryManagerService dormitoryManagerService, HttpServletRequest request) {
        this.request = request;
        this.dormitoryManagerService = dormitoryManagerService;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "id", "dormitoryManager.id.required", "社区楼长编号不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "name", "dormitoryManager.name.required", "社区楼长姓名不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "sex", "dormitoryManager.sex.required", "请选择社区楼长的性别！");
        ValidationUtils.rejectIfEmpty(errors, "birth", "dormitoryManager.birth.required", "请选择社区楼长的出生年月！");
        ValidationUtils.rejectIfEmpty(errors, "politicalStatus", "dormitoryManager.politicalStatus.required", "请选择社区楼长的政治面貌！");
        ValidationUtils.rejectIfEmpty(errors, "workStatus", "dormitoryManager.workStatus.required", "请选择社区楼长的工作状况！");
        ValidationUtils.rejectIfEmpty(errors, "education", "dormitoryManager.education.required", "请选择社区楼长的文化程度！");
        ValidationUtils.rejectIfEmpty(errors, "address", "dormitoryManager.address.required", "社区楼长的家庭地址不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "managerAddress", "dormitoryManager.managerAddress.required", "社区楼长的分包楼栋不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "managerCount", "dormitoryManager.managerCount.required", "社区楼长的联系户数不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "communityId", "dormitoryManager.communityId.required", "请选择所属社区！");
        ValidationUtils.rejectIfEmpty(errors, "subcontractorId", "dormitoryManager.subcontractorId.required", "请选择社区分包人！");
        DormitoryManager dormitoryManager = (DormitoryManager) target;
        if (dormitoryManager.getName().length() > 10) {
            field = "name";
            message = "社区楼长姓名不允许超过10个字符！";
            return false;
        }
        field = "telephone";
        if (StringUtils.isEmpty(dormitoryManager.getMobile()) && StringUtils.isEmpty(dormitoryManager.getLandline())) {
            message = "社区楼长的联系方式必须填写一项！";
            return false;
        }
        if (StringUtils.isNotEmpty(dormitoryManager.getMobile()) && StringCheckedRegexUtils.checkPhone(dormitoryManager.getMobile()) != PhoneCheckedTypes.MOBILE) {
            message = "社区楼长的移动联系方式不合法！";
            return false;
        }
        if (StringUtils.isNotEmpty(dormitoryManager.getLandline()) && StringCheckedRegexUtils.checkPhone(dormitoryManager.getLandline()) != PhoneCheckedTypes.LANDLINE) {
            field = "landline";
            message = "社区楼长的固定联系方式不合法！";
            return false;
        }
        // 验证姓名+地址重复
        String nameAddress = dormitoryManager.getName() + dormitoryManager.getAddress();
        List<String> phones = new ArrayList<>();
        if (StringUtils.isNotEmpty(dormitoryManager.getMobile())) {
            phones.add(CommonUtils.qj2bj(CommonUtils.replaceBlank(dormitoryManager.getMobile())));
        }
        if (StringUtils.isNotEmpty(dormitoryManager.getLandline())) {
            phones.add(CommonUtils.qj2bj(CommonUtils.replaceBlank(dormitoryManager.getLandline()).replaceAll("—", "-")));
        }
        field = "phones";
        Long subdistrictId = Long.valueOf(request.getParameter("subdistrictId"));
        try {
            List<DormitoryManager> isNameAndAddressRepeat = dormitoryManagerService.find(nameAddress, dormitoryManager.getId(), subdistrictId);
            if (!checkedListData(isNameAndAddressRepeat, false, null)) {
                return false;
            }
            // 联系方式重复
            List<DormitoryManager> isPhonesRepeat = dormitoryManagerService.find(phones, dormitoryManager.getId(), subdistrictId);
            return checkedListData(isPhonesRepeat, true, dormitoryManager);
        } catch (Exception e) {
            throw new BusinessException("社区楼长验证失败！", e);
        }
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param isNameAndAddressRepeat 需要验证的楼长对象的集合
     * @param isCheckedPhone         是否合法的社区楼长联系方式
     * @param dormitoryManager       前台传过来的社区楼长对象
     * @return 是否验证成功
     */
    private boolean checkedListData(List<DormitoryManager> isNameAndAddressRepeat, boolean isCheckedPhone, DormitoryManager dormitoryManager) {
        boolean isChecked = true;
        StringBuilder communityNames = new StringBuilder();
        if (isNameAndAddressRepeat.size() > 0) {
            String checkType = null;
            if (isCheckedPhone) {
                for (int i = 0; i < isNameAndAddressRepeat.size(); i++) {
                    if ("PUT".equals(request.getMethod()) && dormitoryManager.getId() != null) {
                        boolean isPhone1 = StringUtils.isNotEmpty(dormitoryManager.getMobile()) && isNameAndAddressRepeat.toString().contains(dormitoryManager.getMobile());
                        boolean isPhone2 = StringUtils.isNotEmpty(dormitoryManager.getLandline()) && isNameAndAddressRepeat.toString().contains(dormitoryManager.getLandline());
                        if (isPhone1 || isPhone2) {
                            continue;
                        }
                    }
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getName()).append("的").append(isNameAndAddressRepeat.get(i).getName()).append("楼长");
                    isChecked = false;
                    checkType = "联系方式";
                    field = "telephones";
                }
            } else {
                for (int i = 0; i < isNameAndAddressRepeat.size(); i++) {
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getName());
                }
                field = "name";
                checkType = "社区楼长";
                isChecked = false;
            }
            if (!isChecked) {
                message = "该" + checkType + "已经存在，现存在于" + communityNames.toString();
            }
        }
        return isChecked;
    }
}
