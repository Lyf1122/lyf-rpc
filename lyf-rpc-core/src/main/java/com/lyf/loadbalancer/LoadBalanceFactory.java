package com.lyf.loadbalancer;

import com.lyf.spi.SpiLoader;

public class LoadBalanceFactory {
  static {
    SpiLoader.load(LoadBalancer.class);
  }

  private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

  public static LoadBalancer getInstance(String key) {
    return SpiLoader.getInstance(LoadBalancer.class, key);
  }

}
