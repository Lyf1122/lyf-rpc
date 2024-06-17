package com.lyf.server;

import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.serializer.JDKSerializerImpl;
import com.lyf.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;

/**
 * 不同的web服务器对应的请求处理器的实现有所不同
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器 已在package com.lyf.serializer下
        Serializer serializer = new JDKSerializerImpl();
        // log
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
        });
        // response
        RpcResponse response = new RpcResponse();
        if (request == null) {
            response.setMessage("rpcRequest is null");

        }
    }

    private void doResponse(HttpServerRequest request, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
