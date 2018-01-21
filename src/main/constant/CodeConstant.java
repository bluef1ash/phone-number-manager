package constant;

import www.entity.ResultCode;

/**
 * 状态码常量
 *
 * @author 廿二月的天
 */
public final class CodeConstant {
    /**
     * CSRF错误状态码
     */
    public final static ResultCode CSRF_ERROR = new ResultCode("101", "CSRF ERROR:无效的token，或者token过期");
}
