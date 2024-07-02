package com.lyf.model;

import cn.hutool.core.util.StrUtil;

public class ServiceMetaInfo extends AbstractServiceMetaInfo{
  @Override
  public String getServiceKey() {
    return String.format("%s, %s", getServiceName(), getServiceVersion());
  }

  @Override
  public String getServiceNodeKey() {
    return String.format("%s/%s:%s", getServiceKey(), getServiceHost(), getServicePort());
  }

  public String getServiceAddress() {
    if (!StrUtil.contains(getServiceHost(), "http")) {
      return String.format("http://%s:%s", getServiceHost(), getServicePort());
    }
    return String.format("%s:%s", getServiceHost(), getServicePort());
  }
}
