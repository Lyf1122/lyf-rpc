package com.lyf.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void start(int port) {
        // new instance
        Vertx vertx = Vertx.vertx();
        // new http server
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // 监听端口
        server.requestHandler(httpServerRequest -> {
            System.out.println("接收请求：" + httpServerRequest.method());
            /**
             * text/plain: 纯文本格式，常用于传输普通文本数据。它不包含任何格式或结构信息，只是将文本内容原样传输。
             * application/json: 用于传输结构化数据。它使用JSON表示数据，可以表示复杂的数据结构
             */
            httpServerRequest.response().putHeader("Content-Type", "text/plain");
            server.listen(port, httpServerResult -> {
                if (httpServerResult.succeeded()) {
                    System.out.println("Server启动成功");
                } else {
                    System.out.println("Server启动失败");
                }
            });
        });
    }
}
