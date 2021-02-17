package com.cxy.goodgoodstudy.jvm;

/**
 * 测试对象分配  添加jvm参数 ‐XX:+PrintGCDetail
 * -XX:PretenureSizeThreshold  设置默认的“大对象”大小，如果达到这个大小，就会直接进入老年代，
 * 如果没有设置的话，是默认在s区域分配不下，就会进入老年代，这个参数只在Serial和ParNew两个收集器下有效。
 * 比如设置JVM参数：-XX:PretenureSizeThreshold=1000000(单位是字节)-XX:+UseSerialGC，再执行下allocation1会发现大对象直接进了老年代
 *
 * 一共有以下几种情况：
 * 1.优先栈上分配（非逃逸对象 + 标量替换）
 * 2.在eden区进行分配（以下代码就是验证默认在eden区分配的事例）
 * 3.大对象直接进入老年代（eden区域无法分配，那么正常年轻代比例（8:1:1），在from区和to区肯定也分配不下，就会直接进入老年代）
 * 4.长期存活的对象会进入老年代（默认经过15次的对象头年龄，也就是minorGC的次数，就会进入老年代）
 * 5.对象动态年龄判断（如果在minorGC之后，在survivor区域，一大批对象，年龄1……年龄N的内存总大小超过当前存放的s区内的某一块内存的50%，那么就将大于等于年龄N的对象提前进入老年代）
 * 6.老年代空间分配担保机制（每一次minorGC之前都会判断老年代剩余的内存空间，如果大于年轻代的所有对象（包括垃圾对象），就会直接minorGC，如果小于，那么就会看是否有配置-XX:-HandlePromotionFailure的jvm参数，
 * 如果没有配置，那么就会直接fullGC，紧接着minorGC，如果配置了，那么就会再判断一下，老年代剩余的内存空间，是否小于之前历史所有的minorGC所移入到老年代的平均对象内存大小（历史所有minorGC移动到老年代的对象/历史所有minorGC的次数），如果
 * 小于就直接fullGC，之后再进行minorGC，如果大于就直接进行minorGC）
 *
 * 注意：就算是没有对象分配，默认jvm也会在eden有一定的空间被使用，jvm自己的产生的对象
 */
public class MemoryAllocation {

    public static void main(String[] args) {

        byte[] allocation1, allocation2, allocation3,allocation4,allocation5,allocation6;

        allocation1 = new byte[60000 * 1024];
        /*
        只创建了大概60M的对象的堆信息，eden区域已经被使用了100%
        Heap
         PSYoungGen      total 76288K, used 65536K [0x000000076af00000, 0x0000000770400000, 0x00000007c0000000)
          eden space 65536K, 100% used [0x000000076af00000,0x000000076ef00000,0x000000076ef00000)
          from space 10752K, 0% used [0x000000076f980000,0x000000076f980000,0x0000000770400000)
          to   space 10752K, 0% used [0x000000076ef00000,0x000000076ef00000,0x000000076f980000)
         ParOldGen       total 175104K, used 0K [0x00000006c0c00000, 0x00000006cb700000, 0x000000076af00000)
          object space 175104K, 0% used [0x00000006c0c00000,0x00000006c0c00000,0x00000006cb700000)
         Metaspace       used 3497K, capacity 4498K, committed 4864K, reserved 1056768K
          class space    used 387K, capacity 390K, committed 512K, reserved 1048576K
         */

        allocation2 = new byte[8000 * 1024];
        /*
        可以看到，我们又创建了8M的对象，这样eden区其实是我们的8M对象，因为60M的对象由于产生了youngGC，但是在s1的from区域又无法存放，
        那么就会直接提前进入到老年代中。
        [GC (Allocation Failure) [PSYoungGen: 65250K->1016K(76288K)] 65250K->61024K(251392K), 0.0284234 secs] [Times: user=0.22 sys=0.05, real=0.03 secs]
        Heap
         PSYoungGen      total 76288K, used 9671K [0x000000076af00000, 0x0000000774400000, 0x00000007c0000000)
          eden space 65536K, 13% used [0x000000076af00000,0x000000076b773ef8,0x000000076ef00000)
          from space 10752K, 9% used [0x000000076ef00000,0x000000076effe030,0x000000076f980000)
          to   space 10752K, 0% used [0x0000000773980000,0x0000000773980000,0x0000000774400000)
         ParOldGen       total 175104K, used 60008K [0x00000006c0c00000, 0x00000006cb700000, 0x000000076af00000)
          object space 175104K, 34% used [0x00000006c0c00000,0x00000006c469a010,0x00000006cb700000)
         Metaspace       used 3498K, capacity 4498K, committed 4864K, reserved 1056768K
          class space    used 387K, capacity 390K, committed 512K, reserved 1048576K
         */

        allocation3 = new byte[1000 * 1024];
        allocation4 = new byte[1000 * 1024];
        allocation5 = new byte[1000 * 1024];
        allocation6 = new byte[1000 * 1024];
        /*
        我们可以看到我们后面接连创建了4次1M的对象，但是都是分配在eden区域的，因为eden区域的内存空间目前足够大，是可以完成对象内存的分配。
        Heap
         PSYoungGen      total 76288K, used 13926K [0x000000076af00000, 0x0000000774400000, 0x00000007c0000000)
          eden space 65536K, 19% used [0x000000076af00000,0x000000076bb9fbb8,0x000000076ef00000)
          from space 10752K, 9% used [0x000000076ef00000,0x000000076effa020,0x000000076f980000)
          to   space 10752K, 0% used [0x0000000773980000,0x0000000773980000,0x0000000774400000)
         ParOldGen       total 175104K, used 60008K [0x00000006c0c00000, 0x00000006cb700000, 0x000000076af00000)
          object space 175104K, 34% used [0x00000006c0c00000,0x00000006c469a010,0x00000006cb700000)
         Metaspace       used 3497K, capacity 4498K, committed 4864K, reserved 1056768K
          class space    used 387K, capacity 390K, committed 512K, reserved 1048576K
         */
    }
}
