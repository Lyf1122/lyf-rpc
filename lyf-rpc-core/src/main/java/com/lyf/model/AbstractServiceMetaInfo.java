package com.lyf.model;

public abstract class AbstractServiceMetaInfo {
  /**
   * 服务名称
   */
  private String serviceName;
  /**
   * 服务版本
   */
  private String serviceVersion = "1.0";
  /**
   * 服务地址
   */
  private String serviceHost;
  /**
   * 服务端口
   */
  private Integer servicePort;
  /**
   * 服务分组
   */
  private String serviceGroup = "default";

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceVersion() {
    return serviceVersion;
  }

  public void setServiceVersion(String serviceVersion) {
    this.serviceVersion = serviceVersion;
  }

  public String getServiceHost() {
    return serviceHost;
  }

  public void setServiceHost(String serviceHost) {
    this.serviceHost = serviceHost;
  }

  public Integer getServicePort() {
    return servicePort;
  }

  public void setServicePort(Integer servicePort) {
    this.servicePort = servicePort;
  }

  public String getServiceGroup() {
    return serviceGroup;
  }

  public void setServiceGroup(String serviceGroup) {
    this.serviceGroup = serviceGroup;
  }

  abstract public String getServiceKey();

  abstract public String getServiceNodeKey();

}
