package com.cxy.goodgoodstudy.jvm;

/**
 * 懒加载
 */
public class LazyLoadingClass {

    static {
        System.out.println("====================loading LazyLoadingClass.class ====================");
    }

    public static void main(String[] args) {
        // 实例化之后才会触发类加载过程
        TestA testA = new TestA();
        // 只有引用，没有使用到TestB这个对象，所以不会被加载
        TestB testB = null;
    }
}

class TestA {
    static {
        System.out.println("====================loading TestA.class ====================");
    }

    public TestA() {
        System.out.println("====================init TestA.class ====================");
    }
}

class TestB {
    static {
        System.out.println("====================loading TestB.class ====================");
    }

    public TestB() {
        System.out.println("====================init TestB.class ====================");
    }
}