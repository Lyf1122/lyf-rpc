package com.lyf.fault.tolerant;

import com.lyf.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
  /**
   * 容错策略
   * @param context 用于传递数据
   * @param e
   * @return
   * @throws Exception
   */
  RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception;
}
