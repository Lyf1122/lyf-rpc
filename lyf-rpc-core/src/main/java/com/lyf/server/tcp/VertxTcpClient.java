package com.lyf.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.lyf.RpcApplication;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.model.ServiceMetaInfo;
import com.lyf.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 客户端实现
 */
public class VertxTcpClient {

  public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) {
    // 构造TCP请求
    Vertx vertx = Vertx.vertx();
    NetClient netClient = vertx.createNetClient();
    CompletableFuture<RpcResponse> rpcResponseFuture = new CompletableFuture<>();
    netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
      if (result.succeeded()) {
        System.out.println("连接到TCP服务器");
        NetSocket socket = result.result();
        // 发送数据 构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getConfig().getSerializer()).getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);
        // 编码请求
        try {
          Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
          socket.write(buffer);
        } catch (Exception e) {
          throw new RuntimeException("Failed to encode RPC request", e);
        }
        // 接受响应
        socket.handler(buffer -> {
          try {
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
            rpcResponseFuture.complete(rpcResponseProtocolMessage.getBody());
          } catch (Exception e) {
            throw new RuntimeException("Failed to decode RPC response", e);
          }
        });
      } else {
        System.err.println("Failed to connect to TCP server");
        return;
      }
    });
    try {
      RpcResponse rpcResponse = rpcResponseFuture.get();
      netClient.close();
      return rpcResponse;
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void start() {
    Vertx vertx = Vertx.vertx();
    vertx.createNetClient().connect(8888, "localhost", netSocketAsyncResult -> {
      if (netSocketAsyncResult.succeeded()) {
        System.out.println("Connected to TCP server successfully");
        NetSocket netSocket = netSocketAsyncResult.result();
        // 发送数据
        netSocket.write("Hello, server");
        // 接收响应 这里写入的是buffer，还需要编码解码转换为Java对象
        netSocket.handler(buffer -> {
          System.out.println("Received response from server: " + buffer.toString());
        });
      } else {
        System.err.println("Failed to connect TCP server");
      }
    });
  }

  public static void main(String[] args) {
    new VertxTcpClient().start();
  }

}
