package www.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import www.dao.*;
import www.service.BaseService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础Service实现
 *
 * @param <T> SERVICE泛型
 * @author 廿二月的天
 */
public class BaseServiceImpl<T> implements BaseService<T> {
    BaseDao<T> baseDao;
    @Resource
    protected SystemUsersDao systemUsersDao;
    @Resource
    protected UserRolesDao userRolesDao;
    @Resource
    protected UserPrivilegesDao userPrivilegesDao;
    @Resource
    protected UserRolePrivilegesDao userRolePrivilegesDao;
    @Resource
    protected SubdistrictsDao subdistrictsDao;
    @Resource
    protected CommunitiesDao communitiesDao;
    @Resource
    protected CommunityResidentsDao communityResidentsDao;
    @Resource
    protected ConfigurationsDao configurationsDao;
    @Resource
    protected SubcontractorsDao subcontractorsDao;

    /**
     * 最先运行的方法，自动加载对应类型的DAO
     *
     * @throws Exception SERVICE层异常
     */
    @PostConstruct
    private void initBaseDao() throws Exception {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        String className = clazz.getSimpleName();
        StringBuilder localField = new StringBuilder();
        localField.append(className.substring(0, 1).toLowerCase());
        if ("y".equals(className.substring(className.length() - 1))) {
            localField.append(className, 1, className.length() - 1).append("ies");
        } else {
            localField.append(className.substring(1)).append("s");
        }
        localField.append("Dao");
        Field field = this.getClass().getSuperclass().getDeclaredField(localField.toString());
        Field baseField = this.getClass().getSuperclass().getDeclaredField("baseDao");
        baseField.set(this, field.get(this));
    }

    @Override
    public long createObject(T obj) throws Exception {
        return baseDao.insertObject(obj);
    }

    @Override
    public int deleteObjectById(Long id) throws Exception {
        return baseDao.deleteObjectById(id);
    }

    @Override
    public int deleteObjectsByName(String name) throws Exception {
        return baseDao.deleteObjectsByName(name);
    }

    @Override
    public int updateObject(T obj) throws Exception {
        return baseDao.updateObject(obj);
    }

    @Override
    public List<T> findObjects() throws Exception {
        return baseDao.selectObjectsAll();
    }

    @Override
    public Map<String, Object> findObjects(Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsAll();
        return findObjectsMethod(data);
    }

    @Override
    public T findObject(Long id) throws Exception {
        return baseDao.selectObjectById(id);
    }

    @Override
    public Map<String, Object> findObjects(String name, Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsByName(name);
        return findObjectsMethod(data);
    }

    @Override
    public Map<String, Object> findObjects(T object, Integer pageNumber, Integer pageDataSize) throws Exception {
        setPageHelper(pageNumber, pageDataSize);
        List<T> data = baseDao.selectObjectsByObject(object);
        return findObjectsMethod(data);
    }

    @Override
    public List<T> findObjectsForIdAndName() throws Exception {
        return baseDao.selectObjectsForIdAndName();
    }

    @Override
    public List<T> findObjects(T object) throws Exception {
        return baseDao.selectObjectsByObject(object);
    }

    /**
     * 配置PageHelper
     *
     * @param pageNumber  分页页码
     * @param pageDataSize 每页显示数量
     */
    protected void setPageHelper(Integer pageNumber, Integer pageDataSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageDataSize = pageDataSize == null ? 10 : pageDataSize;
        PageHelper.startPage(pageNumber, pageDataSize);
    }

    /**
     * 查找对象的方法
     *
     * @param data 对象集合
     * @return 查找到的对象集合与分页对象
     */
    protected Map<String, Object> findObjectsMethod(List<T> data) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("data", data);
        map.put("pageInfo", new PageInfo<T>(data));
        map.put("count", data.size());
        return map;
    }
}
