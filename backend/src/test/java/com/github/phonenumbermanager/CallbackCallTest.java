package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 回调调用测试类
 */
@SpringBootTest
public class CallbackCallTest {

    @Test
    public void call() {
        CallbackTest.set(index -> index = 112);
        CallbackTest.test();
    }
}
