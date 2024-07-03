package com.lyf.example.provider;

import com.lyf.RpcApplication;
import com.lyf.config.RegistryConfig;
import com.lyf.config.RpcConfig;
import com.lyf.example.common.service.UserService;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.registry.LocalRegistry;
import com.lyf.registry.RegistryFactory;
import com.lyf.registry.RegistryService;
import com.lyf.server.HttpServer;
import com.lyf.server.VertxHttpServer;

public class ProviderExample {
  public static void main(String[] args) {
    // rpc init
    RpcApplication.init();
    // service registry
    String serviceName = UserService.class.getName();
    LocalRegistry.registerService(serviceName, UserServiceImpl.class);
    // 注册服务到注册中心
    RpcConfig rpcConfig = RpcApplication.getConfig();
    RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
    RegistryService registryService = RegistryFactory.getInstance(registryConfig.getRegistryType());
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName(serviceName);
    serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
    serviceMetaInfo.setServicePort(rpcConfig.getPort());
    try {
      registryService.register(serviceMetaInfo);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    // 启动web服务
    HttpServer httpServer = new VertxHttpServer();
    httpServer.start(RpcApplication.getConfig().getPort());
  }
}
