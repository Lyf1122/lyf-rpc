package com.lyf.registry;

import com.lyf.config.RegistryConfig;
import com.lyf.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryServiceTest {
  final RegistryService registryService = new EtcdRegistry();

  @Before
  public void init() {
    RegistryConfig registryConfig = new RegistryConfig();
    registryConfig.setAddress("http://localhost:2379");
    registryService.init(registryConfig);
  }

  @Test
  public void register() {
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("LyfService");
    serviceMetaInfo.setServiceVersion("0.1.0");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServicePort(1234);
    registryService.register(serviceMetaInfo);

    serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("LyfService");
    serviceMetaInfo.setServiceVersion("0.1.0");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServicePort(1235);
    registryService.register(serviceMetaInfo);

    serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("LyfService");
    serviceMetaInfo.setServiceVersion("0.2.0");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServicePort(1234);
    registryService.register(serviceMetaInfo);

  }

  @Test
  public void unRegister() {
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("LyfService");
    serviceMetaInfo.setServiceVersion("0.1.0");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServicePort(1234);
    registryService.unregister(serviceMetaInfo);
  }

  @Test
  public void serviceDiscovery() {
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("LyfService");
    serviceMetaInfo.setServiceVersion("0.1.0");
    String serviceKey = serviceMetaInfo.getServiceKey();
    List<ServiceMetaInfo> serviceMetaInfoList = registryService.serviceDiscovery(serviceKey);
    Assert.assertNotNull(serviceMetaInfoList);
  }

}
