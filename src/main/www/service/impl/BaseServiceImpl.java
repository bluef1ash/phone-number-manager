package www.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import www.dao.*;
import www.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础Service实现
 *
 * @param <T> 模型泛型
 */
public class BaseServiceImpl<T> implements BaseService<T> {
    protected BaseDao<T> baseDao;
    @Autowired
    protected SystemUsersDao systemUsersDao;
    @Autowired
    protected UserRolesDao userRolesDao;
    @Autowired
    protected UserPrivilegesDao userPrivilegesDao;
    @Autowired
    protected UserRolePrivilegesDao userRolePrivilegesDao;
    @Autowired
    protected SubdistrictsDao subdistrictsDao;
    @Autowired
    protected CommunitiesDao communitiesDao;
    @Autowired
    protected CommunityResidentsDao communityResidentsDao;

    /**
     * 最先运行的方法，自动加载对应类型的DAO
     *
     * @throws Exception
     */
    @PostConstruct
    private void initBaseDao() throws Exception {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        String className = clazz.getSimpleName();
        StringBuilder localField = new StringBuilder();
        localField.append(className.substring(0, 1).toLowerCase());
        if ("y".equals(className.substring(className.length() - 1))) {
            localField.append(className.substring(1, className.length() - 1)).append("ies");
        } else {
            localField.append(className.substring(1)).append("s");
        }
        localField.append("Dao");
        Field field = this.getClass().getSuperclass().getDeclaredField(localField.toString());
        Field baseField = this.getClass().getSuperclass().getDeclaredField("baseDao");
        baseField.set(this, field.get(this));
    }

    @Override
    public int createObject(T obj) throws Exception {
        return baseDao.insertObject(obj);
    }

    @Override
    public int deleteObjectById(Integer id) throws Exception {
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
    public Map<String, Object> findObjects(Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<T> data = baseDao.selectObjectsAll();
        return findObjectsMethod(data, pageNum, pageSize);
    }

    @Override
    public T findObject(Integer id) throws Exception {
        return baseDao.selectObjectById(id);
    }

    @Override
    public Map<String, Object> findObjects(String name, Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<T> data = baseDao.selectObjectsByName(name);
        return findObjectsMethod(data, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> findObjects(T object, Integer pageNum, Integer pageSize) throws Exception {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);
        List<T> data = baseDao.selectObjectsByObject(object);
        return findObjectsMethod(data, pageNum, pageSize);
    }

    @Override
    public List<T> findObjectsForIdAndName() throws Exception {
        return baseDao.selectObjectsForIdAndName();
    }

    /**
     * 查找对象的方法
     *
     * @param data
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    protected Map<String, Object> findObjectsMethod(List<T> data, Integer pageNum, Integer pageSize) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", data);
        map.put("pageInfo", new PageInfo<T>(data));
        return map;
    }
}
