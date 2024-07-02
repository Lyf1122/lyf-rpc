package com.lyf.server;

import com.lyf.RpcApplication;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.registry.LocalRegistry;
import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 不同的web服务器对应的请求处理器的实现有所不同
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().getSerializer());

    @Override
    public void handle(HttpServerRequest request) {
        System.out.println("Received request: " + request.method() + " " + request.uri());
        // async request
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // response
            RpcResponse response = new RpcResponse();
            if (rpcRequest == null) {
                response.setMessage("rpcRequest is null!");
                doResponse(request, response, serializer);
                return;
            }
            try {
                // 尝试调用实现类
                Class<?> implClass = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypeList());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // set response
                response.setData(result);
                response.setDataType(method.getReturnType());
                response.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setException(e);
            }
            // do response
            doResponse(request, response, serializer);
        });

    }

    private void doResponse(HttpServerRequest request, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.setStatusCode(500).end(Buffer.buffer("{\"error\":\"Internal server error\"}"));
        }
    }
}
