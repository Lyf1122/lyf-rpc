package com.lyf.annotation;

import com.lyf.constant.RpcConstant;
import com.lyf.fault.retry.RetryStrategyKeys;
import com.lyf.fault.tolerant.TolerantStrategyKeys;
import com.lyf.loadbalancer.LoadBalanceKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liyifan
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
  /**
   * 服务接口类
   * @return
   */
  Class<?> interfaceClass() default void.class;

  /**
   * 版本
   * @return
   */
  String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

  /**
   * 负载均衡
   */
  String loadBalancer() default LoadBalanceKeys.RANDOM;

  /**
   * Retry
   */
  String retryStrategy() default RetryStrategyKeys.NO;

  /**
   * Tolerant
   */
  String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

  /**
   * Mock
   */
  boolean mock() default false;

}
