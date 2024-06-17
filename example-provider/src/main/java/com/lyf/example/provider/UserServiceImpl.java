package com.lyf.example.provider;

import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("UserName：" + user.getUsername());
        return user;
    }
}
