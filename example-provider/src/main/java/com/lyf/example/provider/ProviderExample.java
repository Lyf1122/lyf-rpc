package com.lyf.example.provider;

import com.lyf.RpcApplication;
import com.lyf.example.common.service.UserService;
import com.lyf.registry.Registry;
import com.lyf.server.HttpServer;
import com.lyf.server.VertxHttpServer;

public class ProviderExample {
  public static void main(String[] args) {
    RpcApplication.init();
    Registry.registerService(UserService.class.getName(), UserServiceImpl.class);
    HttpServer httpServer = new VertxHttpServer();
    httpServer.start(RpcApplication.getConfig().getPort());
  }
}
