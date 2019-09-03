package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneCheckedTypes;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.exception.BusinessException;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.utils.CommonUtils;
import com.github.phonenumbermanager.utils.StringCheckedRegexUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 社区居民添加/更新验证
 *
 * @author 廿二月的天
 */
public class CommunityResidentInputValidator extends BaseInputValidator<CommunityResident> implements Validator {
    private CommunityResidentService communityResidentService;

    public CommunityResidentInputValidator(CommunityResidentService communityResidentService, HttpServletRequest request) {
        this.communityResidentService = communityResidentService;
        this.request = request;
    }

    @Override
    protected boolean checkInput(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "communityResident.name.required", "社区居民姓名不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "address", "communityResident.address.required", "社区居民地址不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "subcontractorId", "communityResident.subcontractorId.required", "社区分包人不能为空！");
        CommunityResident communityResident = (CommunityResident) target;
        String name = CommonUtils.replaceBlank(communityResident.getName()).replaceAll("—", "-");
        if (name.length() > 10) {
            message = "社区居民姓名不能超过10个字符！";
            return false;
        }
        String address = CommonUtils.replaceBlank(communityResident.getAddress()).replaceAll("—", "-");
        if (address.length() > 255) {
            message = "社区居民地址不能超过255个字符！";
            return false;
        }
        if (communityResident.getSubcontractorId() == null || communityResident.getSubcontractorId() == 0) {
            message = "未选择社区分包人！";
            return false;
        }
        Long id = communityResident.getId();
        // 验证姓名+地址重复
        String nameAddress = name + address;
        List<String> phones = new ArrayList<>();
        if (StringUtils.isNotEmpty(communityResident.getPhone1())) {
            phones.add(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone1()).replaceAll("—", "-")));
        }
        if (StringUtils.isNotEmpty(communityResident.getPhone2())) {
            phones.add(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone2()).replaceAll("—", "-")));
        }
        if (StringUtils.isNotEmpty(communityResident.getPhone3())) {
            phones.add(CommonUtils.qj2bj(CommonUtils.replaceBlank(communityResident.getPhone3()).replaceAll("—", "-")));
        }
        field = "phones";
        // 联系方式合法
        if (!checkedPhone(phones)) {
            return false;
        }
        Long subdistrictId = Long.valueOf(request.getParameter("subdistrictId"));
        try {
            List<CommunityResident> isNameAndAddressRepeat = communityResidentService.find(nameAddress, id, subdistrictId);
            if (!checkedListData(isNameAndAddressRepeat, false, null)) {
                return false;
            }
            // 联系方式重复
            List<CommunityResident> isPhonesRepeat = communityResidentService.find(phones, id, subdistrictId);
            return checkedListData(isPhonesRepeat, true, communityResident);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("社区居民验证失败！", e);
        }
    }

    /**
     * 验证联系方式是否合法
     *
     * @param residentPhones 社区居民联系方式的集合
     * @return 是否验证成功
     */
    private boolean checkedPhone(List<String> residentPhones) {
        String tmpPhone = null;
        for (String residentPhone : residentPhones) {
            if (StringUtils.isNotEmpty(tmpPhone) && tmpPhone.equals(residentPhone)) {
                message = "不允许重复输入相同的输入方式，请检查后重试！";
                return false;
            }
            // 验证固定电话与手机
            if (StringCheckedRegexUtils.checkPhone(residentPhone) == PhoneCheckedTypes.FAILED) {
                message = "输入的联系方式不合法，请检查后重试！";
                return false;
            }
            tmpPhone = residentPhone;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param isNameAndAddressRepeat 需要验证的社区居民对象的集合
     * @param isCheckedPhone         是否合法的社区居民联系方式
     * @param communityResident      前台传过来的社区居民对象
     * @return 是否验证成功
     */
    private boolean checkedListData(List<CommunityResident> isNameAndAddressRepeat, boolean isCheckedPhone, CommunityResident communityResident) {
        boolean isChecked = true;
        StringBuilder communityNames = new StringBuilder();
        if (isNameAndAddressRepeat.size() > 0) {
            String checkType = null;
            if (isCheckedPhone) {
                for (int i = 0; i < isNameAndAddressRepeat.size(); i++) {
                    if (RequestMethod.PUT.toString().equals(request.getMethod()) && communityResident.getId() != null) {
                        boolean isPhone1 = StringUtils.isNotEmpty(communityResident.getPhone1()) && isNameAndAddressRepeat.toString().contains(communityResident.getPhone1());
                        boolean isPhone2 = StringUtils.isNotEmpty(communityResident.getPhone2()) && isNameAndAddressRepeat.toString().contains(communityResident.getPhone2());
                        boolean isPhone3 = StringUtils.isNotEmpty(communityResident.getPhone3()) && isNameAndAddressRepeat.toString().contains(communityResident.getPhone3());
                        if (isPhone1 || isPhone2 || isPhone3) {
                            continue;
                        }
                    }
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getName()).append("的").append(communityResident.getName()).append("居民");
                    isChecked = false;
                    checkType = "联系方式";
                    field = "phones";
                }
            } else {
                for (int i = 0; i < isNameAndAddressRepeat.size(); i++) {
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getName());
                }
                field = "name";
                checkType = "社区居民";
                isChecked = false;
            }
            if (!isChecked) {
                message = "该" + checkType + "已经存在，现存在于" + communityNames.toString();
            }
        }
        return isChecked;
    }
}
