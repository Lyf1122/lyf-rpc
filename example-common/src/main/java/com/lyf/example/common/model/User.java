package com.lyf.example.common.model;


import java.io.Serializable;

/**
 * 在网络中传输的实体类 需要支持序列化
 */
public class User implements Serializable {
    private String username;

    private Integer age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
