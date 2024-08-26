package com.lyf.bootstrap;

import com.lyf.RpcApplication;
import com.lyf.annotation.RpcReference;
import com.lyf.annotation.RpcService;
import com.lyf.config.RegistryConfig;
import com.lyf.config.RpcConfig;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.registry.LocalRegistry;
import com.lyf.registry.RegistryFactory;
import com.lyf.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    RpcService rpcService = beanClass.getAnnotation(RpcService.class);
    if (rpcService != null) {
      // Get some info
      Class<?> interfaceClass = rpcService.interfaceClass();
      if (interfaceClass == void.class) {
        interfaceClass = beanClass.getInterfaces()[0];
      }
      String serviceName = interfaceClass.getName();
      String serviceVersion = rpcService.serviceVersion();
      // Registry
      LocalRegistry.registerService(serviceName, beanClass);
      // global config
      final RpcConfig rpcConfig = RpcApplication.getConfig();

      RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
      RegistryService registryService = RegistryFactory.getInstance(rpcConfig.getRetryStrategy());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(serviceName);
      serviceMetaInfo.setServiceVersion(serviceVersion);
      serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
      serviceMetaInfo.setServicePort(rpcConfig.getPort());
      try {
        registryService.register(serviceMetaInfo);
      } catch (Exception e) {
        log.error("Register service failed", e);
        throw new RuntimeException(serviceName + " Service is failed to register", e);
      }
    }

    return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
  }
}
