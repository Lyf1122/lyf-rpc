package com.lyf.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.lyf.config.RegistryConfig;
import com.lyf.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EtcdRegistry implements RegistryService{
  private Client client;
  private KV kvClient;

  private static final String ETCD_ROOT_PATH = "/rpc/";
  private final Set<String> localRegisterNodeKeySet = new HashSet<>();
  private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
  // 正在监听的key
  private final Set<String> watchKeySet = new ConcurrentHashSet<>();

  @Override
  public void init(RegistryConfig registryConfig) {
    client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
    kvClient = client.getKVClient();
    heartbeat();
  }

  @Override
  public void register(ServiceMetaInfo serviceMetaInfo) {
    // 创建客户端
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
      // 添加本地缓存
      localRegisterNodeKeySet.add(registryKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void unregister(ServiceMetaInfo serviceMetaInfo) {
    String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
    kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8));
    // delete from cache
    localRegisterNodeKeySet.remove(registryKey);
  }

  @Override
  public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
    // search from cache first
    List<ServiceMetaInfo> cacheServiceList = registryServiceCache.readCache();
    if (CollUtil.isNotEmpty(cacheServiceList)) {
      return cacheServiceList;
    }

    // prefix search
    String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
    try {
      GetOption getOption = GetOption.builder().isPrefix(true).build();
      List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();
      // 解析服务信息
      List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
        // watch key
        watch(key);
        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
        return JSONUtil.toBean(value, ServiceMetaInfo.class);
      }).collect(Collectors.toList());
      // write to cache
      registryServiceCache.writeCache(serviceMetaInfoList);
      return serviceMetaInfoList;
    } catch (Exception e) {
      throw new RuntimeException("Failed to get service list", e);
    }
  }

  @Override
  public void destroy() {
    System.out.println("Current node is down");
    // 下线节点
    for (String key : localRegisterNodeKeySet) {
      try {
        kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
      } catch (Exception e) {
        throw new RuntimeException(key + "failed to delete", e);
      }
    }
    // release resources
    if (kvClient != null) {
      kvClient.close();
    }
    if (client != null) {
      client.close();
    }
  }

  @Override
  public void heartbeat() {
    CronUtil.schedule("*/10 * * * * *", new Task() {
      @Override
      public void execute() {
        for (String key : localRegisterNodeKeySet) {
          try {
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
            if (CollUtil.isEmpty(keyValues)) {
              // outdated, need to restart node
              continue;
            }
            // re-register
            KeyValue keyValue = keyValues.get(0);
            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
            ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
            register(serviceMetaInfo);
          } catch (Exception e) {
            throw new RuntimeException(key + "failed to re-register", e);
          }
        }
      }
    });
    // support second-level task
    CronUtil.setMatchSecond(true);
    CronUtil.start();
  }

  @Override
  public void watch(String serviceNodeKey) {
    Watch watchClient = client.getWatchClient();
    // 如果set已存在该key，返回false
    boolean newWatch = watchKeySet.add(serviceNodeKey);
    if (newWatch) {
      watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), watchResponse -> {
        for (WatchEvent watchEvent : watchResponse.getEvents()) {
          switch (watchEvent.getEventType()) {
            case DELETE:
              // 清除注册服务缓存
              registryServiceCache.clearCache();
              break;
            case PUT:
            default:
              break;
          }
        }
      });
    }
  }
}
