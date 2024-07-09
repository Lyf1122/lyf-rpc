package com.lyf.loadbalancer;

import com.lyf.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{

  private final Random random = new Random();

  @Override
  public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
    if (serviceMetaInfoList.isEmpty()) {
      return null;
    }
    int size = serviceMetaInfoList.size();
    if (size == 1) {
      return serviceMetaInfoList.get(0);
    }

    return serviceMetaInfoList.get(random.nextInt(size));
  }
}
