package com.lyf.example.common.service;

import com.lyf.example.common.model.User;

/**
 * 由服务提供方实现该接口，消费方调用即可
 */
public interface UserService {
    /**
     * 根据name获取用户
     * @param user
     * @return
     */
    User getUser(User user);

    default short getNumber() {
        return 1;
    }
}
