package com.cxy.goodgoodstudy.jvm;

import com.sun.crypto.provider.DESKeyFactory;
import sun.misc.Launcher;

import java.net.URL;

/**
 * 测试jdk的classLoader
 */
public class JDKClassLoader {
    public static void main(String[] args) {

        System.out.println("========== classLoader的结构 ==========");
        // 应用classLoader
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        // 拓展classLoader
        ClassLoader extClassLoader = appClassLoader.getParent();
        // 引导classLoader
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println("appClassLoader ==========>> " + appClassLoader);
        System.out.println("extClassLoader ==========>> " + extClassLoader);
        System.out.println("bootstrapClassLoader ==========>> " + bootstrapClassLoader);
        System.out.println();


        System.out.println("========== 查看加载以下class的classLoader ==========");
        // String类是存在于jre的lib包底下的，是属于最核心的代码，由jdk启动的时候，由c++来加载的
        System.out.println("String.class ==========>> " + String.class.getClassLoader());
        // 是属于ext包底下的类库，所以是由extClassLoader进行加载类
        System.out.println("DESKeyFactory.class ==========>> " + DESKeyFactory.class.getClassLoader());
        // 我们自己编写的java文件，所以应该由appClassLoader来完成加载
        System.out.println("Math.class ==========>> " + Math.class.getClassLoader());
        System.out.println();

        System.out.println("========== 各个classLoader加载的目录 ==========");
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        System.out.println("bootstrapLoader加载以下文件：");
        for (URL url : urls) {
            System.out.println(url);
        }
        System.out.println();

        System.out.println("extClassloader加载以下文件：");
        String extDirs = System.getProperty("java.ext.dirs");
        for (String extDir : extDirs.split(":")) {
            System.out.println(extDir);
        }
        System.out.println();

        System.out.println("appClassLoader加载以下文件：");
        String classPathDirs = System.getProperty("java.class.path");
        for (String classPathDir : classPathDirs.split(":")) {
            System.out.println(classPathDir);
        }
    }
}
