package com.lyf.fault.retry;

import com.lyf.model.RpcResponse;
import com.github.rholder.retry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔执行重试
 */
public class FixedIntervalRetryStrategy implements RetryStrategy{
  private static final Logger log = LoggerFactory.getLogger(FixedIntervalRetryStrategy.class);

  @Override
  public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
    Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
        .retryIfExceptionOfType(Exception.class)
        .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
        .withStopStrategy(StopStrategies.stopAfterAttempt(3))
        .withRetryListener(new RetryListener() {
          @Override
          public <V> void onRetry(Attempt<V> attempt) {
            log.info("第{}次重试", attempt.getAttemptNumber());
          }
        })
        .build();
    return retryer.call(callable);
  }
}
