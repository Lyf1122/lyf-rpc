package com.lyf.example.provider;

import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String userName) {
        User user = new User();
        user.setUsername("Evan Li");
        user.setAge(22);
        return user;
    }
}
