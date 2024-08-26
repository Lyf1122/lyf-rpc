package com.lyf.bootstrap;

import com.lyf.RpcApplication;
import com.lyf.config.RegistryConfig;
import com.lyf.config.RpcConfig;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.model.ServiceRegisterInfo;
import com.lyf.registry.LocalRegistry;
import com.lyf.registry.RegistryFactory;
import com.lyf.registry.RegistryService;
import com.lyf.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
  public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
    // RPC init
    RpcApplication.init();
    // global config
    final RpcConfig rpcConfig = RpcApplication.getConfig();
    // register service
    for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
      String serviceName = serviceRegisterInfo.getServiceName();
      LocalRegistry.registerService(serviceName, serviceRegisterInfo.getImplClass());
      // register service to registry
      RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
      RegistryService registry = RegistryFactory.getInstance(registryConfig.getRegistryType());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(serviceName);
      serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
      serviceMetaInfo.setServicePort(rpcConfig.getPort());
      try {
        registry.register(serviceMetaInfo);
      } catch (Exception e) {
        throw new RuntimeException(serviceName + " Service register failed", e);
      }
    }

    // start server
    VertxTcpServer vertxTcpServer = new VertxTcpServer();
    vertxTcpServer.start(rpcConfig.getPort());

  }

}
