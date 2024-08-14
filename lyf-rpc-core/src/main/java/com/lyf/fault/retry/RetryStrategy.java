package com.lyf.fault.retry;

import com.lyf.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {
  /**
   * 执行重试
   * @param callable 任务参数
   * @return
   * @throws Exception
   */
  RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
