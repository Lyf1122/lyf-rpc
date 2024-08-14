package com.lyf.fault.tolerant;

import com.lyf.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 遇到异常后，记录日志，但不抛出异常，正常返回一个对象
 */
@Slf4j
public class SilentTolerantStrategy implements TolerantStrategy{
  @Override
  public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
    log.warn("SilentTolerantStrategy: {}", e.getMessage());
    return new RpcResponse();
  }
}
