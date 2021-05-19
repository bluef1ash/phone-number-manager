package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 回调调用测试类
 */
@SpringBootTest
public class CallbackCallTests {

    @Test
    public void call() {
        CallbackTests.set(index -> index = 112);
        CallbackTests.test();
    }
}
