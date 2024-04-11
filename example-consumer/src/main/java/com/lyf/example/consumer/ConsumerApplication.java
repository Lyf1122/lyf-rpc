package com.lyf.example.consumer;

import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;

public class ConsumerApplication {
    public static void main(String[] args) {
        // todo: 通过RPC获取userService的实现类对象
        UserService userService = null;
        User user = new User();
        user.setUsername("Evan Li");
        user.setAge(23);


    }
}
