package test;

import org.junit.Test;

public class ExcelTest {

    private interface Person {
        public void eat();
    }
    private class Student implements Person {
        @Override
        public void eat() {
            System.out.println("111111");
        }
        public void sleep() {
            System.out.println("22222222222");
        }
    }
    private void test(Person person) {
        Student student = null;
        if (person instanceof Student) {
            System.out.println("hhhhhhhhhhhhh");
            student = (Student) person;
        }
        student.sleep();
    }
    @Test
    public void run() {
        Student student = new Student();
        test(student);
    }
}
