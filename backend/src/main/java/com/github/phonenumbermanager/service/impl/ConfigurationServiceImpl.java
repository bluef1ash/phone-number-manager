package com.github.phonenumbermanager.service.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.entity.Configuration;
import com.github.phonenumbermanager.mapper.ConfigurationMapper;
import com.github.phonenumbermanager.service.ConfigurationService;

/**
 * 系统配置业务实现
 *
 * @author 廿二月的天
 */
@Service
public class ConfigurationServiceImpl extends BaseServiceImpl<ConfigurationMapper, Configuration>
    implements ConfigurationService {

    @CacheEvict(cacheNames = SystemConstant.CONFIGURATIONS_MAP_KEY)
    @Override
    public boolean save(Configuration entity) {
        return super.save(entity);
    }

    @CacheEvict(cacheNames = SystemConstant.CONFIGURATIONS_MAP_KEY)
    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @CacheEvict(cacheNames = SystemConstant.CONFIGURATIONS_MAP_KEY)
    @Override
    public boolean updateById(Configuration entity) {
        return super.updateById(entity);
    }

    @Cacheable(cacheNames = SystemConstant.CONFIGURATIONS_MAP_KEY + "${{ttl: -1}}")
    @Override
    public Map<String, Configuration> mapAll() {
        QueryWrapper<Configuration> wrapper = new QueryWrapper<>();
        wrapper.select("id", "title", "description", "name", "content", "field_type", "field_value");
        return baseMapper.selectList(wrapper).stream()
            .collect(Collectors.toMap(Configuration::getName, Function.identity()));
    }
}
