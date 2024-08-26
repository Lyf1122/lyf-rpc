package com.lyf.bootstrap;

import com.lyf.RpcApplication;
import com.lyf.annotation.EnableRpc;
import com.lyf.config.RpcConfig;
import com.lyf.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    boolean needServer = (boolean) Objects.requireNonNull(importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())).get("needServer");
    RpcApplication.init();
    final RpcConfig rpcConfig = RpcApplication.getConfig();

    if (needServer) {
      VertxTcpServer vertxTcpServer = new VertxTcpServer();
      vertxTcpServer.start(rpcConfig.getPort());
    } else {
      log.warn("No server");
    }
  }
}
