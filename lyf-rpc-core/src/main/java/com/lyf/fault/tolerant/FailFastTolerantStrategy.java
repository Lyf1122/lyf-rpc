package com.lyf.fault.tolerant;

import com.lyf.model.RpcResponse;

import java.util.Map;

/**
 * 发生错误，立刻通知调用方
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
  @Override
  public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
    /*将异常抛出交给外层服务处理*/
    throw new RuntimeException("服务报错", e);
  }
}
