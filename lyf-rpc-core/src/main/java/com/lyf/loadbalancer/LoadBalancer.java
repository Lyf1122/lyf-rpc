package com.lyf.loadbalancer;

import com.lyf.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
  /**
   * 根据负载均衡策略选择一个服务
   * @param requestParams
   * @param serviceMetaInfoList
   * @return
   */
  ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
