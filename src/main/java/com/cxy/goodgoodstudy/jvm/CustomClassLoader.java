package com.cxy.goodgoodstudy.jvm;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomClassLoader {

    static class MyClassLoader extends ClassLoader {
        private final String classPath;

        public MyClassLoader(String classPath) {
            this.classPath = classPath;
        }

        /**
         * 因为加载的时候是使用的loadClass，所以里面核心调用了findClass，我们只需要重写这个方法就能实现自定义类加载器
         * @param name 包名称
         * @return 加载完成的Class
         */
        @Override
        protected Class<?> findClass(String name) {
            try {
                byte[] bytes = loadByte(name);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return findClass(name);
        }

        /**
         * 打破双亲委派，需要重写loadClass方法，因为这个方法核心会调用parent的类加载器
         * 但是由于Object类，或者类中使用的其他类，都是需要在我们自定义类加载器加载的时候跟着加载
         * 所以我们只加载我们的包路径类，其他的还是交给parent的类加载器进行加载
         * @param name 包路径名称
         * @param resolve 是否需要解析
         * @return Class对象
         * @throws ClassNotFoundException 找不到类异常
         */
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                // First, check if the class has already been loaded
                Class<?> c = findLoadedClass(name);
                long t0 = System.nanoTime();
                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    if (!name.startsWith("com.cxy.goodgoodstudy.jvm")) {
                        c = this.getParent().loadClass(name);
                    } else {
                        c = findClass(name);
                    }
                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

        private byte[] loadByte(String name) throws Exception {
            name = name.replaceAll("\\.", "/");
            FileInputStream fis = new FileInputStream(classPath + "/" + name+ ".class");
            int len = fis.available();
            byte[] data = new byte[len];
            fis.read(data);
            fis.close();
            return data;
        }


        public static void main(String[] args) {
            MyClassLoader myClassLoader = new MyClassLoader("/Users/wuhaigang/Downloads");
            ClassLoader parent = myClassLoader.getParent();
            // 默认parent属性的类加载器是AppClassLoader，因为在实例化自定义类加载器的时候，会先实例化父构造器，这样父构造器会给这个属性赋值
            System.out.println("自定义构造器的parent属性，也就是父加载器：==========>> " + parent);
            try {
                Class<?> aClass = myClassLoader.loadClass("com.cxy.goodgoodstudy.jvm.User1");
                Object object = aClass.newInstance();
                Method sayHello = aClass.getDeclaredMethod("sayHello");
                sayHello.invoke(object);
                /*
                    这里的classLoader是com.cxy.goodgoodstudy.jvm.MyClassLoaderTest$MyClassLoader@64cee07
                    如果在AppClassLoader中已经加载了这个类，那么返回的就是AppClassLoader，因为遵循双亲委派机制
                 */
                System.out.println(aClass.getClassLoader());
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
