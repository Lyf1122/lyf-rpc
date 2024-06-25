package com.lyf.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lyf.example.common.model.User;
import com.lyf.example.common.service.UserService;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.serializer.JDKSerializerImpl;
import com.lyf.serializer.Serializer;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        Serializer serializer = new JDKSerializerImpl();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypeList(new Class[]{User.class})
                .args(new Object[]{user}).build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8090")
                    .body(bodyBytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process RPC request", e);
        }
    }
}
