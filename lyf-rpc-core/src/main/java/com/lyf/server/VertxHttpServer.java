package com.lyf.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void start(int port) {
        // new instance
        Vertx vertx = Vertx.vertx();
        // new http server
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // create http server
        server.requestHandler(new HttpServerHandler());
        // 监听端口
        server.listen(port, httpServerResult -> {
            if (httpServerResult.succeeded()) {
                System.out.println("Server启动成功, on port: " + port);
            } else {
                System.out.println("Server启动失败: " + httpServerResult.cause());
            }
        });
    }
}
