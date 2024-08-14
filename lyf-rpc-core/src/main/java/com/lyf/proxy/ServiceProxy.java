package com.lyf.proxy;

import cn.hutool.core.collection.CollUtil;
import com.lyf.RpcApplication;
import com.lyf.config.RpcConfig;
import com.lyf.constant.RpcConstant;
import com.lyf.fault.retry.RetryFactory;
import com.lyf.fault.retry.RetryStrategy;
import com.lyf.fault.tolerant.TolerantStrategy;
import com.lyf.fault.tolerant.TolerantStrategyFactory;
import com.lyf.loadbalancer.LoadBalanceFactory;
import com.lyf.loadbalancer.LoadBalancer;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.registry.RegistryFactory;
import com.lyf.registry.RegistryService;
import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;
import com.lyf.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于JDK的动态代理
 */
public class ServiceProxy implements InvocationHandler {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypeList(method.getParameterTypes())
                .args(args).build();

      // 序列化
      RpcConfig rpcConfig = RpcApplication.getConfig();
      RegistryService registryService = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistryType());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(serviceName);
      serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
      List<ServiceMetaInfo> serviceMetaInfoList = registryService.serviceDiscovery(serviceMetaInfo.getServiceKey());
      if (CollUtil.isEmpty(serviceMetaInfoList)) {
          throw new RuntimeException("No server address");
      }
      // load balance
      LoadBalancer loadBalancer = LoadBalanceFactory.getInstance(rpcConfig.getLoadBalancer());
      Map<String, Object> requestParams = new HashMap<>();
      requestParams.put("methodName", rpcRequest.getMethodName());
      ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
      // rpc & retry
      RpcResponse rpcResponse;
      try {
        RetryStrategy retryStrategy = RetryFactory.getInstance(rpcConfig.getRetryStrategy());
        rpcResponse = retryStrategy.doRetry(
            () -> VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo)
        );
      } catch (Exception e) {
        // tolerant
        TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
        rpcResponse = tolerantStrategy.doTolerant(null, e);
      }

      return rpcResponse.getData();
    }
}
