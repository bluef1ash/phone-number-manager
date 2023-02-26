package com.github.phonenumbermanager;

import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 普通测试类
 *
 * @author 廿二月的天
 */
@Slf4j
public class JavaTest {
    @Test
    public void onLoad() {
        A a = new A();
        A a1 = new A();
        a1.setId(1L);
        a.getAs().add(a1);
        log.debug(a.toString());
    }

    @Data
    @NoArgsConstructor
    class A {
        private Long id;
        private List<A> as;
    }
}
