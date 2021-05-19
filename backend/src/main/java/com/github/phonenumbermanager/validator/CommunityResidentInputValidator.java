package com.github.phonenumbermanager.validator;

import com.github.phonenumbermanager.constant.PhoneNumberSourceTypeEnum;
import com.github.phonenumbermanager.entity.CommunityResident;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.service.CommunityResidentService;
import com.github.phonenumbermanager.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 社区居民添加/更新验证
 *
 * @author 廿二月的天
 */
public class CommunityResidentInputValidator extends BaseInputValidator<CommunityResident> {
    private final CommunityResidentService communityResidentService;

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
        String name = CommonUtil.replaceBlank(communityResident.getName()).replaceAll("—", "-");
        if (name.length() > 10) {
            message = "社区居民姓名不能超过10个字符！";
            return false;
        }
        String address = CommonUtil.replaceBlank(communityResident.getAddress()).replaceAll("—", "-");
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
        for (PhoneNumber phoneNumber : communityResident.getPhoneNumbers()) {
            if (StringUtils.isNotEmpty(phoneNumber.getPhoneNumber())) {
                phoneNumber.setPhoneNumber(CommonUtil.qj2bj(CommonUtil.replaceBlank(phoneNumber.getPhoneNumber()).replaceAll("—", "-")));
            }
        }
        field = "phoneNumbers";
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        for (PhoneNumber phoneNumber : communityResident.getPhoneNumbers()) {
            if (StringUtils.isNotEmpty(phoneNumber.getPhoneNumber())) {
                phoneNumbers.add(phoneNumber);
            }
        }
        communityResident.setPhoneNumbers(phoneNumbers);
        // 联系方式合法
        if (!checkedPhones(communityResident.getPhoneNumbers())) {
            return false;
        }
        Long subdistrictId = Long.valueOf(request.getParameter("subdistrictId"));
        List<CommunityResident> isNameAndAddressRepeat = communityResidentService.get(nameAddress, id, subdistrictId);
        if (!checkedListData(isNameAndAddressRepeat, false, null)) {
            return false;
        }
        // 联系方式重复
        List<CommunityResident> isPhonesRepeat = communityResidentService.get(communityResident.getPhoneNumbers(), id, subdistrictId, PhoneNumberSourceTypeEnum.COMMUNITY_RESIDENT);
        return checkedListData(isPhonesRepeat, true, communityResident);
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
                loopRepeat:
                for (CommunityResident resident : isNameAndAddressRepeat) {
                    if (RequestMethod.PUT.toString().equals(request.getMethod()) && communityResident.getId() != null) {
                        for (PhoneNumber phoneNumber : resident.getPhoneNumbers()) {
                            for (PhoneNumber pm : resident.getPhoneNumbers()) {
                                if (StringUtils.isNotEmpty(phoneNumber.getPhoneNumber()) && pm.getPhoneNumber().equals(phoneNumber.getPhoneNumber()) && !resident.getId().equals(communityResident.getId())) {
                                    continue loopRepeat;
                                }
                            }
                        }
                    }
                    if (communityNames.length() > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(resident.getCommunity().getName()).append("的").append(resident.getName()).append("居民");
                    isChecked = false;
                    checkType = "联系方式";
                    field = "phones";
                }
            } else {
                for (CommunityResident resident : isNameAndAddressRepeat) {
                    if (communityNames.length() > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(resident.getCommunity().getName());
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
