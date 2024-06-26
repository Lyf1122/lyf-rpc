package com.lyf.config;

import lombok.Data;

/**
 * RPC 配置类
 */
@Data
public class RpcConfig {
  private String name = "lyf-rpc";
  private String version = "0.1";
  private String serverHost = "localhost";
  private Integer port = 8080;
  // mock mode
  private boolean mock = false;
}
