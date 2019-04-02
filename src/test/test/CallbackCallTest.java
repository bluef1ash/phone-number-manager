package test;

import org.junit.Test;

/**
 * 回调调用
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
