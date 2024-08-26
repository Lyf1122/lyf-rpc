package com.lyf.example.consumer;

import com.lyf.annotation.RpcReference;
import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
  @RpcReference
  private UserService userService;

  public void test() {
    User user = new User();
    user.setUsername("lyf");
    User resultUser = userService.getUser(user);
    System.out.println(resultUser.getUsername());
  }
}
