package com.lyf.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 读取配置文件，返回配置对象
 */
public class ConfigUtils {
  public static <T> T loadConfig(Class<T> klass, String prefix) {
    return loadConfig(klass, prefix, "");
  }

  public static <T> T loadConfig(Class<T> klass, String prefix, String env) {
    StringBuilder builder = new StringBuilder("application");
    if (StrUtil.isNotBlank(env)) {
      builder.append("-").append(env);
    }
    builder.append(".properties");
    Props props = new Props(builder.toString());
    return props.toBean(klass, prefix);
  }
}
