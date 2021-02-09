package com.cxy.goodgoodstudy.jvm;

public class Math {

    // 静态常量在准备过程中，就会赋值8作为常量
    public static final int initStaticConstantData = 8;
    // 静态变量在准备过程中会先赋默认值，按照jdk自带的默认值进行赋值，int基本类型会赋值为0。在初始化过程中，才会赋值为6。
    public static final int initStaticVariableData = 6;
    // 静态变量会在准备过程中赋值为null，在初始化过程中会加载User类，进行赋值。懒加载
    public static User user = new User();

    /**
     * 每一个方法对应一块栈帧内存区域
     * @return 返回计算结果
     */
    public int compute() {
        int a = 1;
        int b = 2;
        int c = (a + b) * 10;
        return c;
    }

    public static void main(String[] args) {
        Math math = new Math();
        math.compute();
    }
}