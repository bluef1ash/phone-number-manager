package com.github.phonenumbermanager;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 回调测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CallbackTests {
    private static TestInterface testInterface;

    public static void set(TestInterface testInterface1) {
        testInterface = testInterface1;
    }

    public static void test() {
        Integer index = 1;
        testInterface.call(index);
        System.out.println(index);
    }

    interface TestInterface {
        void call(Integer index);
    }
}
