package com.lyf.serializer;

import com.lyf.spi.SpiLoader;

public class SerializerFactory {

  static {
    // 确保在使用Factory时，已经加载了Serializer接口的实现类
    SpiLoader.load(Serializer.class);
  }

  private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

  public static Serializer getInstance(String key) {
    return SpiLoader.getInstance(Serializer.class, key);
  }

}
