package com.lyf;

import com.lyf.config.RpcConfig;
import com.lyf.constant.RpcConstant;
import com.lyf.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
  private static volatile RpcConfig rpcConfig;

  public static void init(RpcConfig newConfig) {
    rpcConfig = newConfig;
    log.info("RpcApplication init, config = {}", newConfig.toString());
  }

  public static void init() {
    RpcConfig newConfig;
    try {
      newConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
    } catch (Exception e) {
      newConfig = new RpcConfig();
    }
    init(newConfig);
  }

  public static RpcConfig getConfig() {
    /*double-check singleton*/
    if (rpcConfig == null) {
      synchronized (RpcApplication.class) {
        if (rpcConfig == null) {
          init();
        }
      }
    }
    return rpcConfig;
  }

}
