package com.lyf.registry;

import cn.hutool.json.JSONUtil;
import com.lyf.config.RegistryConfig;
import com.lyf.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class EtcdRegistry implements RegistryService{
  private Client client;
  private KV kvClient;

  private static final String ETCD_ROOT_PATH = "/rpc/";

  @Override
  public void init(RegistryConfig registryConfig) {
    client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
    kvClient = client.getKVClient();
  }

  @Override
  public void register(ServiceMetaInfo serviceMetaInfo) {
    Lease leaseClient = client.getLeaseClient();
    try {
      long leaseId = leaseClient.grant(30).get().getID();
      // 要存储的key-value
      String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
      ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
      ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
      // 将k-v和租约关联，设置过期时间
      PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
      kvClient.put(key, value, putOption).get();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void unregister(ServiceMetaInfo serviceMetaInfo) {
    kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
  }

  @Override
  public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
    // prefix search
    String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
    try {
      GetOption getOption = GetOption.builder().isPrefix(true).build();
      List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
      return keyValues.stream().map(keyValue -> {
        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
        return JSONUtil.toBean(value, ServiceMetaInfo.class);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to get service list", e);
    }
  }

  @Override
  public void destroy() {
    System.out.println("Current node is down");
    if (kvClient != null) {
      kvClient.close();
    }
    if (client != null) {
      client.close();
    }
  }
}
