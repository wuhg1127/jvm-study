package com.cxy.goodgoodstudy.jvm;


/**
 * 测试一下我们的线程大小参数配置   ‐Xss512K   默认值：-Xss1M
 * Xss的设置的越大，说明单个线程占用的越多，则申请的栈帧内存大，支持的线程数就少
 * 但是Xss设置的越小，说明单个线程占用的栈内存越少，由于栈（线程）设置的小，这样对于线程数就会越多（同一台物理机器的情况下）
 */
public class StackOverflowTest {
    // 定义一个变量来进行计数
    static int count = 0;

    // 定义一个方法来进行执行，也就是程序启动后的栈帧
    public static void redo() {
        count++;
        // 递归调用
        redo();
    }

    public static void main(String[] args) {
        try {
            redo();
            // 执行结果：默认Xss1M的情况下，执行了23167次redo
            // 调整Xss为512K的时候，执行了10539次redo
        } catch (Throwable t) {
            t.printStackTrace();
            // 打印一下执行了多少次方法，相当于启动了多少栈帧
            System.out.println(count);
        }
    }

}
