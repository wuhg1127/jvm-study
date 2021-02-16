package com.cxy.goodgoodstudy.jvm;

import org.openjdk.jol.info.ClassLayout;

/**
 * 主要是通过两个参数，来对指针压缩进一步的了解
 * ‐XX:+UseCompressedOops 压缩所有指针（默认开启）
 * ‐XX:+UseCompressedClassPointers 压缩对象头里的类型指针Klass Pointer （默认开启）
 */
public class JOLSample {

    public static void main(String[] args) {
        ClassLayout classLayout = ClassLayout.parseInstance(new Object());
        System.out.println(classLayout.toPrintable());

        ClassLayout classLayout1 = ClassLayout.parseInstance(new int[]{});
        System.out.println(classLayout1.toPrintable());

        ClassLayout classLayout2 = ClassLayout.parseInstance(new A());
        System.out.println(classLayout2.toPrintable());
    }

    // ‐XX:+UseCompressedOops 压缩所有指针（默认开启）
    // ‐XX:+UseCompressedClassPointers 压缩对象头里的类型指针Klass Pointer （默认开启）
    public static class A {
        //8B mark word
        //4B Klass Pointer 如果关闭压缩‐XX:‐UseCompressedClassPointers或‐XX:‐UseCompressedOops，则占用8B

        int id; // 4B
        String name; // 4B 如果关闭压缩指针‐XX:‐UseCompressedOops，则占用8个字节
        byte b; //1B
        Object o;  // 4B 如果关闭压缩指针‐XX:‐UseCompressedOops，则占用8个字节
    }
}
/*
 *  默认的jvm参数，默认开启了所有指针压缩和klass pointer指针压缩
 *
 * java.lang.Object object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)     // 这个是mark word (总共占用8个字节)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)     // 这个是mark word (总共占用8个字节)
 *       8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243) // klass Pointer 占用8个字节，此处指针压缩
 *      12     4        (loss due to the next object alignment)     // 对齐填充，按照8个字节的倍数，将总字节数填充为8的倍数，执行效率最高
 * Instance size: 16 bytes
 * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
 *
 * [I object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4        (object header)                           6d 01 00 f8 (01101101 00000001 00000000 11111000) (-134217363)
 *      12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *      16     0    int [I.<elements>                             N/A
 * Instance size: 16 bytes
 * Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
 *
 * com.cxy.goodgoodstudy.jvm.JOLSample$A object internals:
 *  OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
 *       0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4                    (object header)                           62 e0 00 f8 (01100010 11100000 00000000 11111000) (-134160286)
 *      12     4                int A.id                                      0
 *      16     1               byte A.b                                       0
 *      17     3                    (alignment/padding gap)                         // 对齐填充，按照8个字节的倍数，将总字节数填充为8的倍数，执行效率最高
 *      20     4   java.lang.String A.name                                    null  // 指针压缩，压缩成4个字节
 *      24     4   java.lang.Object A.o                                       null  // 指针压缩，压缩成4个字节
 *      28     4                    (loss due to the next object alignment)
 * Instance size: 32 bytes
 * Space losses: 3 bytes internal + 4 bytes external = 7 bytes total
 *
 */


/*
 * ‐XX:-UseCompressedOops  关闭了所有指针压缩的对象信息
 *
 * java.lang.Object object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4        (object header)                           00 1c 5e 1c (00000000 00011100 01011110 00011100) (475929600)
 *      12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 * Instance size: 16 bytes
 * Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
 *
 * [I object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4        (object header)                           68 0b 5e 1c (01101000 00001011 01011110 00011100) (475925352)
 *      12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *      16     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *      20     4        (alignment/padding gap)
 *      24     0    int [I.<elements>                             N/A
 * Instance size: 24 bytes
 * Space losses: 4 bytes internal + 0 bytes external = 4 bytes total
 *
 * com.cxy.goodgoodstudy.jvm.JOLSample$A object internals:
 *  OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
 *       0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4                    (object header)                           b8 d9 c4 1c (10111000 11011001 11000100 00011100) (482662840) // klass pointer 原来只有这一个只占用了4B
 *      12     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)  // klass pointer 现在关闭了指针压缩之后，klass pointer增加了4B
 *      16     4                int A.id                                      0
 *      20     1               byte A.b                                       0
 *      21     3                    (alignment/padding gap)
 *      24     8   java.lang.String A.name                                    null  // 关闭了指针压缩之后，字节从原来的4B变成了8B
 *      32     8   java.lang.Object A.o                                       null  // 关闭了指针压缩之后，字节从原来的4B变成了8B
 * Instance size: 40 bytes
 * Space losses: 3 bytes internal + 0 bytes external = 3 bytes total
 */



/*
 *
 * -XX:-UseCompressedClassPointers    关闭了klass pointer指针压缩的对象信息
 *
 * java.lang.Object object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4        (object header)                           00 1c 15 1c (00000000 00011100 00010101 00011100) (471145472)
 *      12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 * Instance size: 16 bytes
 * Space losses: 0 bytes internal + 0 bytes external = 0 bytes total
 *
 * [I object internals:
 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
 *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4        (object header)                           68 0b 15 1c (01101000 00001011 00010101 00011100) (471141224)
 *      12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *      16     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *      20     4        (alignment/padding gap)
 *      24     0    int [I.<elements>                             N/A
 * Instance size: 24 bytes
 * Space losses: 4 bytes internal + 0 bytes external = 4 bytes total
 *
 * com.cxy.goodgoodstudy.jvm.JOLSample$A object internals:
 *  OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
 *       0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
 *       4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
 *       8     4                    (object header)                           b8 d9 7b 1c (10111000 11011001 01111011 00011100) (477878712) // klass pointer
 *      12     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) // klass pointer 可以清晰的看到我们设置了-XX:-UseCompressedClassPointers指针压缩之后，就不会被压缩
 *      16     4                int A.id                                      0
 *      20     1               byte A.b                                       0
 *      21     3                    (alignment/padding gap)
 *      24     4   java.lang.String A.name                                    null // 可以看得出来，我们的对象还是占有4B，指针压缩了，因为我们设置的jvm参数是不压缩klass pointer
 *      28     4   java.lang.Object A.o                                       null
 * Instance size: 32 bytes
 * Space losses: 3 bytes internal + 0 bytes external = 3 bytes total
 *
 *
 * Process finished with exit code 0
 *
 */