package com.lyf.registry;

import com.lyf.config.RegistryConfig;
import com.lyf.model.ServiceMetaInfo;

import java.util.List;

public interface RegistryService {
  /**
   * 初始化注册中心
   * @param registryConfig
   */
  void init(RegistryConfig registryConfig);

  /**
   * 注册服务
   * @param serviceMetaInfo
   */
  void register(ServiceMetaInfo serviceMetaInfo);
  /**
   * 注销服务
   * @param serviceMetaInfo
   */
  void unregister(ServiceMetaInfo serviceMetaInfo);
  /**
   * 服务发现
   * @param serviceKey
   * @return
   */
  List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
  /**
   * 销毁
   */
  void destroy();
  /**
   * 心跳检测
   */
  void heartbeat();
}
