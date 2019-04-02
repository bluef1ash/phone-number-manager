package test;

/**
 * 回调测试类
 *
 * @author 廿二月的天
 */
public class CallbackTest {
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
