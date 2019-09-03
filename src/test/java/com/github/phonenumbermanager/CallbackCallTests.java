package com.github.phonenumbermanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 回调调用测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CallbackCallTests {

    @Test
    public void call() {
        CallbackTests.set(index -> index = 112);
        CallbackTests.test();
    }
}
