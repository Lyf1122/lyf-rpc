package com.lyf.proxy;

import cn.hutool.core.collection.CollUtil;
import com.lyf.RpcApplication;
import com.lyf.config.RpcConfig;
import com.lyf.constant.RpcConstant;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.registry.RegistryFactory;
import com.lyf.registry.RegistryService;
import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;
import com.lyf.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
      // Get provider address from registry
      RpcConfig rpcConfig = RpcApplication.getConfig();
      RegistryService registryService = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistryType());
      ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
      serviceMetaInfo.setServiceName(serviceName);
      serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
      List<ServiceMetaInfo> serviceMetaInfoList = registryService.serviceDiscovery(serviceMetaInfo.getServiceKey());
      if (CollUtil.isEmpty(serviceMetaInfoList)) {
          throw new RuntimeException("No server address");
      }
      // 暂时取第一个
      ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
      // TCP
      RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
      return rpcResponse.getData();

    }
}
