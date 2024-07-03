package com.lyf.registry;

import com.lyf.spi.SpiLoader;

public class RegistryFactory {
  static {
    SpiLoader.load(RegistryService.class);
  }

  private static final RegistryService DEFAULT_REGISTRY = new EtcdRegistry();

  public static RegistryService getInstance(String key) {
    return SpiLoader.getInstance(RegistryService.class, key);
  }

}
