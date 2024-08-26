package com.lyf.example.provider;

import com.lyf.annotation.RpcService;
import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("UserName：" + user.getUsername());
        return user;
    }
}
