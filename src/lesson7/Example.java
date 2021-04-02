package lesson7;

import lesson7.annotations.AfterSuite;
import lesson7.annotations.BeforeSuite;
import lesson7.annotations.Test;

public class Example {
    @BeforeSuite
    public void someMethodBefore() {
        System.out.println("Some method \"before\" executed");
    }

    @Test(1)
    public void someMethodWithLowPriority() {
        System.out.println("Some method with priority 1 executed");
    }

    @Test(5)
    public void someMethodWithMiddlePriority() {
        System.out.println("Some method with priority 5 executed ");
    }

    @Test(10)
    public void someMethodWithHighPriority() {
        System.out.println("Some method with priority 10 executed");
    }

    @Test()
    public void someMethodWithDefaultPriority() {
        System.out.println("Some method with priority 1 executed");
    }

    @AfterSuite
    public void someMethodAfter() {
        System.out.println("Some method \"after\" executed");
    }
}
