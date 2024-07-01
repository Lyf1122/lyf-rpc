package com.lyf.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lyf.RpcApplication;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.serializer.JDKSerializer;
import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 基于JDK的动态代理
 */
public class ServiceProxy implements InvocationHandler {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypeList(method.getParameterTypes())
                .args(args).build();

        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(bodyBytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process RPC request", e);
        }
    }
}
