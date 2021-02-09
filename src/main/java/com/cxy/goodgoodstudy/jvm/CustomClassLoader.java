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
