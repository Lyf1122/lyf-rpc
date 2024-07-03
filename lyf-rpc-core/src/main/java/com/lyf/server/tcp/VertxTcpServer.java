package com.lyf.server.tcp;

import com.lyf.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * VertxTcpServer 服务端实现
 */
public class VertxTcpServer implements HttpServer {

  private byte[] handleRequest(byte[] requestData) {
    // 实现处理请求数据的逻辑

    return "Hello, client".getBytes();
  }

  @Override
  public void start(int port) {
    Vertx vertx = Vertx.vertx();
    // 创建tcp服务器
    NetServer netServer = vertx.createNetServer();
    // 处理请求
    netServer.connectHandler(netSocket -> {
      // 处理连接
      netSocket.handler(buffer -> {
        // 处理收到的字节组
        byte[] requestData = buffer.getBytes();
        // 这里可以自定义对requestData的处理逻辑，例如进行请求解析，服务调用
        byte[] responseData = handleRequest(requestData);
        // 发送响应
        netSocket.write(Buffer.buffer(requestData));
      });
    });
    // 启动 TCP 服务器并监听指定端口
    netServer.listen(port, result -> {
      if (result.succeeded()) {
        System.out.println("TCP server started on port " + port);
      } else {
        System.out.println("Failed to start TCP server: " + result.cause());
      }
    });
  }

  public static void main(String[] args) {
    VertxTcpServer server = new VertxTcpServer();
    server.start(8888);
  }

}
