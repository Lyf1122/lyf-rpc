package com.lyf.example.provider;

import com.lyf.example.common.service.UserService;
import com.lyf.registry.Registry;
import com.lyf.server.HttpServer;
import com.lyf.server.VertxHttpServer;

/**
 * 服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        Registry.registerService(UserService.class.getName(), UserServiceImpl.class);

        // 启动Web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(8090);
    }
}
