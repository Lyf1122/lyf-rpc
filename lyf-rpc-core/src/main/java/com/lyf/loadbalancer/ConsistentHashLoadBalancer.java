package com.lyf.loadbalancer;

import com.lyf.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer{

  /*存放虚拟节点 一致性哈希环*/
  private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

  private static final int VIRTUAL_NODE_SIZE = 100;

  @Override
  public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
    if (serviceMetaInfoList.isEmpty()) {
      return null;
    }
    // 构建虚拟节点环
    for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
      for (int i=0; i<VIRTUAL_NODE_SIZE; i++) {
        int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
        virtualNodes.put(hash, serviceMetaInfo);
      }
    }
    // 获取调用请求的hash值，选择最接近的虚拟节点
    int hash = getHash(requestParams);
    Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
    if (entry == null) {
      entry = virtualNodes.firstEntry();
    }
    return entry.getValue();
  }

  private int getHash(Object key) {
    return key.hashCode();
  }

}
