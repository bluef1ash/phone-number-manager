package com.github.phonenumbermanager.configure;

import java.util.Objects;
import java.util.WeakHashMap;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 字符串转枚举工厂类
 *
 * @author 廿二月的天
 */
@Component
public class EnumConvertFactory implements ConverterFactory<Object, IEnum> {
    private static final WeakHashMap<String, Converter> CACHE = new WeakHashMap<>();

    @Override
    public <T extends IEnum> Converter<Object, T> getConverter(Class<T> targetType) {
        String simpleName = targetType.getSimpleName();
        Converter converter = CACHE.get(targetType);
        if (converter == null) {
            converter = new String2EnumConverter<>(targetType);
            CACHE.put(simpleName, converter);
        }
        return converter;
    }

    private static class String2EnumConverter<T extends IEnum> implements Converter<String, T> {
        private final Class<T> aClass;

        public String2EnumConverter(Class<T> enumType) {
            this.aClass = enumType;
        }

        @Override
        public T convert(String s) {
            for (T enumType : aClass.getEnumConstants()) {
                if (Objects.equals(enumType.getValue(), Integer.parseInt(s))) {
                    return enumType;
                }
            }
            return null;
        }
    }
}
