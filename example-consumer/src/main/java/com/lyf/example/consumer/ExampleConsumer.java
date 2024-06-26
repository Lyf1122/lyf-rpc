package com.lyf.example.consumer;

import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;
import com.lyf.proxy.ServiceProxyFactory;

public class ExampleConsumer {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setUsername("Evan Li");
        user.setAge(23);
        // 调用
        User newUser = userService.getUser(user);
        if (null != newUser) {
            System.out.println(newUser.getUsername());
        } else {
            System.out.println("User is null!");
        }
    }
}
