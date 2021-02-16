package com.cxy.goodgoodstudy.jvm;

import lombok.Data;

@Data
public class User {

    private int id;

    private String name;

    public void sayHello() {
        System.out.println("hello world");
    }
}
