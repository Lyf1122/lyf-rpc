package com.lyf.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * 客户端实现
 */
public class VertxTcpClient {
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
