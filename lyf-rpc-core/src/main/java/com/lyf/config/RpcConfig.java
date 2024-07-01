package com.lyf.config;

import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerKeys;
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
  // serializer type
  private String serializer = SerializerKeys.JDK;
}
