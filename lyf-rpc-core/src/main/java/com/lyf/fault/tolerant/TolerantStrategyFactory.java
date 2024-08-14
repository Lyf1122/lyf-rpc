package com.lyf.fault.tolerant;

import com.lyf.spi.SpiLoader;

public class TolerantStrategyFactory {
  static {
    SpiLoader.load(TolerantStrategy.class);
  }

  private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new SilentTolerantStrategy();

  public static TolerantStrategy getInstance(String key) {
    return SpiLoader.getInstance(TolerantStrategy.class, key);
  }

}
