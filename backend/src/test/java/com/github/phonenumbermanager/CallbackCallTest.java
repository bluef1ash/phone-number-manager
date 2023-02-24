package com.github.phonenumbermanager;

import org.junit.jupiter.api.Test;

/**
 * 回调调用测试类
 *
 * @author 廿二月的天
 */
public class CallbackCallTest {

    @Test
    public void call() {
        CallbackTest.set(index -> index = 112);
        CallbackTest.test();
    }
}
