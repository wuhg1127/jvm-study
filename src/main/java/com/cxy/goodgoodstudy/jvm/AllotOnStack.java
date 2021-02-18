package com.cxy.goodgoodstudy.jvm;


import java.util.ArrayList;

/**
 * 栈上分配依赖于标量替换、逃逸分析
 * 代码调用了1亿次method，如果是分配到堆上，大概需要1GB以上堆空间，如果堆空间小于该值，必然会触发GC
 *
 * 使用如下参数不会发生GC（开启了逃逸分析，并且开启了标量替换，jdk7以后就默认都开启的）
 * ‐Xmx15m ‐Xms15m ‐XX:+DoEscapeAnalysis ‐XX:+PrintGC ‐XX:+EliminateAllocations
 *
 * 使用如下参数都会发生大量GC
 * ‐Xmx15m ‐Xms15m ‐XX:‐DoEscapeAnalysis ‐XX:+PrintGC ‐XX:+EliminateAllocations  关闭逃逸分析，开启标量替换，则不会分配到栈内存
 * ‐Xmx15m ‐Xms15m ‐XX:+DoEscapeAnalysis ‐XX:+PrintGC ‐XX:‐EliminateAllocations  开启逃逸分析，关闭标量替换，也不会分配到栈内存
 *
 * 名词解释：
 * 逃逸分析：判断该对象的作用域不会超过该method，或者超过之后，没有被其他对象等引用或者使用，则属于非逃逸对象，会优先分配在栈帧内存，反之，则属于逃逸对象，会分配在堆内存中
 * 标量替换：标量替换，指的是会将对象分解成若干个属性（有正在被使用的属性），会在栈帧内存中，通过各个碎片内存来存储这些属性（标量），随着栈帧释放，对象被销毁，不会放到堆中，也不会产生GC
 * 标量和聚合量：标量指的是不会再被分解的对象，通常是int、long等基本类型，聚合量则是可以分解成若干个标量，再Java中，对象就是聚合量
 *
 */
public class AllotOnStack {

    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 100000000; i++) {

            // users.add(doEscapeAnalysis()); // 该对象属于逃逸对象，不管开不开器逃逸分析和标量替换，都会在堆上进行分配，所以必然会产生大量gc

            doEscapeAnalysis(); // 有返回值，但是没有引用返回值，随着栈帧释放，对象被释放，属于非逃逸对象，默认开启逃逸分析和标量替换的情况下不太可能会产生fullGC
            /*
            执行结果：
            第一种：执行unDoEscapeAnalysis方法时，由于开启了逃逸分析和标量替换，没有产生大量GC，只产生了一次
            [GC (Allocation Failure)  4096K->805K(15872K), 0.0008208 secs]

            第二种：执行unDoEscapeAnalysis方法时，没有开启逃逸分析，但是开启了标量替换，产生了大量GC
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0002087 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0001760 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0001646 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0002223 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0002145 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0001553 secs]
            [GC (Allocation Failure)  4988K->892K(15872K), 0.0001881 secs]……

            第三种：执行unDoEscapeAnalysis方法时，开启了逃逸分析，但是没有开启了标量替换，产生了大量GC
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0001996 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0001522 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0002036 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0002138 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0001661 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0001902 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0002174 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0001888 secs]
            [GC (Allocation Failure)  5053K->957K(15872K), 0.0002096 secs]……
             */
        }
    }


    /**
     * 由于对象引用被返回，可能导致无法确定该对象是否被其他引用，属于逃逸对象
     * @return 实例对象
     */
    public static User doEscapeAnalysis() {
        User user = new User();
        user.setId(1);
        user.setName("逃逸对象");
        return user;
    }

    /**
     * 该user对象只在method内部使用，可以通过逃逸分析得知该对象的作用于不会超过该方法
     * 所以属于非逃逸对象，这样就会在栈上分配该对象内存，前提必须开启逃逸分析和标量替换
     */
    public static void unDoEscapeAnalysis() {
        User user = new User();
        user.setId(2);
        user.setName("非逃逸对象");
    }
}
