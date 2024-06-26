package com.lyf.example.consumer;

import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;
import com.lyf.proxy.ServiceProxyFactory;

public class ConsumerExample {
  public static void main(String[] args) {
    UserService userService = ServiceProxyFactory.getProxy(UserService.class);
    User user = new User();
    user.setUsername("Evan");
    user.setAge(23);
    User newUser = userService.getUser(user);
    if (newUser != null) {
      System.out.println(newUser.getUsername() + ":" + newUser.getAge());
    } else {
      System.out.println("User not found");
    }
    short num = userService.getNumber();
    System.out.println(num);
  }
}
