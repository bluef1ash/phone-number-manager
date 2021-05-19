package com.github.phonenumbermanager;

import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.NoArgsConstructor;

public class JavaTest {
    @Test
    public void onLoad() {
        A a = new A();
        A a1 = new A();
        a1.setId(1L);
        a.getAs().add(a1);
        System.out.println(a);
    }

    @Data
    @NoArgsConstructor
    class A {
        private Long id;
        private List<A> as;
    }
}
