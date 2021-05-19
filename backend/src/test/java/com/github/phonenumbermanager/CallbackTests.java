package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 回调测试类
 */
@SpringBootTest
public class CallbackTests {
    private static TestInterface testInterface;

    public static void set(TestInterface testInterface1) {
        testInterface = testInterface1;
    }

    @Test
    public static void test() {
        Integer index = 1;
        testInterface.call(index);
        System.out.println(index);
    }

    interface TestInterface {
        void call(Integer index);
    }
}
