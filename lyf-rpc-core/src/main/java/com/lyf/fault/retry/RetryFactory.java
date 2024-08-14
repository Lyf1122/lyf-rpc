package com.lyf.fault.retry;

import com.lyf.spi.SpiLoader;

public class RetryFactory {
  static {
    SpiLoader.load(RetryStrategy.class);
  }

  private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

  public static RetryStrategy getInstance(String key) {
    return SpiLoader.getInstance(RetryStrategy.class, key);
  }

}
