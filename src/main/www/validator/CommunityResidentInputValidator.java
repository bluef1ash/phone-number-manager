package www.validator;

import constant.PhoneCheckedTypes;
import exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import utils.CommonUtil;
import utils.StringCheckedRegexUtil;
import www.entity.CommunityResident;
import www.service.CommunityResidentService;

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
        ValidationUtils.rejectIfEmpty(errors, "communityResidentName", "communityResident.communityResidentName.required", "社区居民姓名不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "communityResidentAddress", "communityResident.communityResidentAddress.required", "社区居民地址不能为空！");
        ValidationUtils.rejectIfEmpty(errors, "subcontractorId", "communityResident.subcontractorId.required", "社区分包人不能为空！");
        CommunityResident communityResident = (CommunityResident) target;
        String communityResidentName = CommonUtil.replaceBlank(communityResident.getCommunityResidentName()).replaceAll("—", "-");
        if (communityResidentName.length() > 10) {
            message = "社区居民姓名不能超过10个字符！";
            return false;
        }
        String communityResidentAddress = CommonUtil.replaceBlank(communityResident.getCommunityResidentAddress()).replaceAll("—", "-");
        if (communityResidentAddress.length() > 255) {
            message = "社区居民地址不能超过255个字符！";
            return false;
        }
        if (communityResident.getSubcontractorId() == null || communityResident.getSubcontractorId() == 0) {
            message = "未选择社区分包人！";
            return false;
        }
        Long communityResidentId = communityResident.getCommunityResidentId();
        // 验证姓名+地址重复
        String nameAddress = communityResidentName + communityResidentAddress;
        List<String> phones = new ArrayList<>();
        if (StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone1())) {
            phones.add(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone1()).replaceAll("—", "-")));
        }
        if (StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone2())) {
            phones.add(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone2()).replaceAll("—", "-")));
        }
        if (StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone3())) {
            phones.add(CommonUtil.qj2bj(CommonUtil.replaceBlank(communityResident.getCommunityResidentPhone3()).replaceAll("—", "-")));
        }
        field = "communityResidentPhones";
        // 联系方式合法
        if (!checkedPhone(phones)) {
            return false;
        }
        Long subdistrictId = Long.valueOf(request.getParameter("subdistrictId"));
        try {
            List<CommunityResident> isNameAndAddressRepeat = communityResidentService.findByNameAndAddress(nameAddress, communityResidentId, subdistrictId);
            if (!checkedListData(isNameAndAddressRepeat, false, null)) {
                return false;
            }
            // 联系方式重复
            List<CommunityResident> isPhonesRepeat = communityResidentService.findByPhones(phones, communityResidentId, subdistrictId);
            return checkedListData(isPhonesRepeat, true, communityResident);
        } catch (Exception e) {
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
            if (StringCheckedRegexUtil.checkPhone(residentPhone) == PhoneCheckedTypes.FAILED) {
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
                    if ("PUT".equals(request.getMethod()) && communityResident.getCommunityResidentId() != null) {
                        boolean isPhone1 = StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone1()) && isNameAndAddressRepeat.toString().contains(communityResident.getCommunityResidentPhone1());
                        boolean isPhone2 = StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone2()) && isNameAndAddressRepeat.toString().contains(communityResident.getCommunityResidentPhone2());
                        boolean isPhone3 = StringUtils.isNotEmpty(communityResident.getCommunityResidentPhone3()) && isNameAndAddressRepeat.toString().contains(communityResident.getCommunityResidentPhone3());
                        if (isPhone1 || isPhone2 || isPhone3) {
                            continue;
                        }
                    }
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getCommunityName()).append("的").append(isNameAndAddressRepeat.get(i).getCommunityResidentName()).append("居民");
                    isChecked = false;
                    checkType = "联系方式";
                    field = "communityResidentPhones";
                }
            } else {
                for (int i = 0; i < isNameAndAddressRepeat.size(); i++) {
                    if (i > 0) {
                        communityNames.append("，");
                    }
                    communityNames.append(isNameAndAddressRepeat.get(i).getCommunity().getCommunityName());
                }
                field = "communityResidentName";
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
