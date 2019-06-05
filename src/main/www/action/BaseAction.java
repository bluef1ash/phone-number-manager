package www.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import exception.JsonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import utils.CommonUtil;
import utils.ExcelUtil;
import www.entity.Community;
import www.entity.SystemUser;
import www.service.CommunityService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.*;

/**
 * 控制器父类
 *
 * @author 廿二月的天
 */
abstract class BaseAction {
    Long systemRoleId;
    Long communityRoleId;
    Long subdistrictRoleId;
    SystemUser systemUser;
    Map<String, Object> configurationsMap;
    Long systemAdministratorId;

    /**
     * 获取Session内的角色编号
     *
     * @param session Session对象
     */
    void getSessionRoleId(HttpSession session) {
        systemUser = (SystemUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        configurationsMap = (Map<String, Object>) session.getAttribute("configurationsMap");
        systemRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_role_id"));
        communityRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("community_role_id"));
        subdistrictRoleId = CommonUtil.convertConfigurationLong(configurationsMap.get("subdistrict_role_id"));
        systemAdministratorId = CommonUtil.convertConfigurationLong(configurationsMap.get("system_administrator_id"));
    }

    /**
     * 设置导入页面Cookie
     *
     * @param session Session对象
     * @param model   前台模型
     */
    void setPersonVariable(HttpSession session, Model model) {
        getSessionRoleId(session);
        Map<String, Long> roleIdMap = new HashMap<>(4);
        roleIdMap.put("systemRoleId", systemRoleId);
        roleIdMap.put("communityRoleId", communityRoleId);
        roleIdMap.put("subdistrictRoleId", subdistrictRoleId);
        model.addAttribute("roleIds", roleIdMap);
        model.addAttribute("roleId", systemUser.getRoleId());
    }

    /**
     * 居民与楼长保存或更新时出现错误
     *
     * @param communityService 社区Service对象
     * @param model            前台模型
     * @param bindingResult    错误信息对象
     * @throws Exception SERVICE层异常
     */
    void throwsError(CommunityService communityService, Model model, BindingResult bindingResult) throws Exception {
        List<Community> communities = communityService.findCommunitiesBySystemUser(systemUser, communityRoleId, subdistrictRoleId);
        model.addAttribute("communities", communities);
        // 输出错误信息
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        model.addAttribute("messageErrors", allErrors);
    }

    /**
     * 上传Excel
     *
     * @param request    HTTP请求对象
     * @param session    Session对象
     * @param excelTitle Excel文件标题字段
     * @return Excel工作簿对象
     * @throws Exception SERVICE层异常
     */
    Workbook uploadExcel(HttpServletRequest request, HttpSession session, String excelTitle) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if (file == null || file.isEmpty()) {
            throw new JsonException("文件不存在！");
        }
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            getSessionRoleId(session);
            filename = CommonUtil.convertConfigurationString(configurationsMap.get(excelTitle));
        }
        InputStream inputStream = file.getInputStream();
        Workbook workbook = ExcelUtil.getWorkbook(inputStream, filename);
        if (workbook == null) {
            throw new Exception("创建Excel工作薄为空！");
        }
        return workbook;
    }

    /**
     * 获取下载Excel用户数据
     *
     * @param session Session对象
     * @param data    传递的用户数据
     * @return 解码后用户数据
     */
    List<Map<String, Object>> getUserData(HttpSession session, String data) {
        getSessionRoleId(session);
        byte[] jsonDecode = Base64.getDecoder().decode(data);
        List<Map<String, Object>> userData = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) JSON.parse(jsonDecode);
        for (Object obj : jsonArray) {
            userData.add((Map<String, Object>) obj);
        }
        return userData;
    }

    /**
     * 设置返回JSON数据
     *
     * @param dataMap 数据对象集合
     * @return JSON数据
     */
    Map<String, Object> setJsonMap(Map<String, Object> dataMap) {
        Map<String, Object> jsonMap = new HashMap<>(4);
        jsonMap.put("state", 1);
        jsonMap.put("data", dataMap.get("data"));
        jsonMap.put("pageInfo", dataMap.get("pageInfo"));
        return jsonMap;
    }

    /**
     * 设置错误Cookie
     *
     * @param request  HTTP请求对象
     * @param response HTTP相应对象
     */
    void setCookieError(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("exportExcel".equals(cookie.getName())) {
                cookie.setValue("error");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }
}
