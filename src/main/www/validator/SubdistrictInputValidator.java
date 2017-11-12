package www.validator;

import exception.BusinessException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import www.entity.Community;
import www.entity.Subdistrict;
import www.service.CommunityService;
import www.service.SubdistrictService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 街道添加/更新验证
 */
public class SubdistrictInputValidator implements Validator {
    private String message;
    private String field;
    private SubdistrictService subdistrictService;
    private HttpServletRequest request;
    private String errorCode;

    public SubdistrictInputValidator() {
    }

    public SubdistrictInputValidator(SubdistrictService subdistrictService, HttpServletRequest request) {
        this.subdistrictService = subdistrictService;
        this.request = request;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Subdistrict.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            ValidationUtils.rejectIfEmpty(errors, "subdistrictName", "subdistrict.subdistrictName.required", "街道名称不能为空！");
            ValidationUtils.rejectIfEmpty(errors, "subdistrictTelephone", "subdistrict.subdistrictTelephone.required", "街道联系方式不能为空！");
            Subdistrict subdistrict = (Subdistrict) target;
            if (!checkInput(subdistrict)) {
                errors.rejectValue(field, errorCode, message);
            }
        } catch (Exception e) {
            throw new BusinessException("街道验证失败！");
        }
    }

    /**
     * 验证输入数据
     *
     * @param subdistrict
     * @return
     */
    private Boolean checkInput(Subdistrict subdistrict) throws Exception {
        // 联系方式合法
        if (!checkedPhone(subdistrict.getSubdistrictTelephone())) {
            return false;
        }
        // 联系方式重复
        List<Subdistrict> isPhonesRepeat = subdistrictService.findSubdistrictBySubdistrict(subdistrict);
        return checkedListData(isPhonesRepeat);
    }

    /**
     * 验证联系方式是否合法
     *
     * @param phone
     * @return
     */
    private boolean checkedPhone(String phone) {
        Pattern regex = Pattern.compile("(?iUs)^(?:(?:[\\(（]?(?:[0-9]{3,4})?\\s*[\\)）-])?\\d{7,9}(?:[-转]\\d{2,6})?)|(?:[\\(\\+]?(?:86)?[\\)]?0?1[34578]{1}\\d{9})$");
        Matcher matcher = regex.matcher(phone);
        if (!matcher.matches()) {
            field = "subdistrictTelephone";
            errorCode = "subdistrict.subdistrictTelephone.errorCode";
            message = "输入的联系方式不合法，请检查后重试！";
            return false;
        }
        return true;
    }

    /**
     * 验证数据库返回数据是否为空
     *
     * @param subdistricts
     * @return
     */
    private boolean checkedListData(List<Subdistrict> subdistricts) {
        if (subdistricts != null && subdistricts.size() > 0) {
            field = "subdistrictId";
            errorCode = "subdistrict.subdistrictId.errorCode";
            message = "所填内容已存在！";
            return false;
        }
        return true;
    }
}
