package com.lyf.server.tcp;

import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.protocol.ProtocolMessage;
import com.lyf.protocol.ProtocolMessageDecoder;
import com.lyf.protocol.ProtocolMessageEncoder;
import com.lyf.protocol.ProtocolMessageTypeEnum;
import com.lyf.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.lang.reflect.Method;

/**
 * 装饰器模式实现 添加 半包粘包 处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
  @Override
  public void handle(NetSocket netSocket) {
    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
      // 接受请求并解码
      ProtocolMessage<RpcRequest> protocolMessage;
      try {
        protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
      } catch (Exception e) {
        throw new RuntimeException("协议消息解码错误");
      }
      RpcRequest rpcRequest = protocolMessage.getBody();

      // 处理请求 构建response
      RpcResponse rpcResponse = new RpcResponse();
      try {
        // 获取要调用服务的实现类 反射调用
        Class<?> implClass = LocalRegistry.getService(rpcRequest.getServiceName());
        Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypeList());
        Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
        // 封装返回结果
        rpcResponse.setData(result);
        rpcResponse.setDataType(method.getReturnType());
        rpcResponse.setMessage("ok");
      } catch (Exception e) {
        e.printStackTrace();
        rpcResponse.setMessage(e.getMessage());
        rpcResponse.setException(e);
      }
      // 发送响应并编码
      ProtocolMessage.Header header = protocolMessage.getHeader();
      header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
      ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
      try {
        Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
        netSocket.write(encode);
      } catch (Exception e) {
        throw new RuntimeException("协议消息编码错误");
      }
    });
    netSocket.handler(bufferHandlerWrapper);
  }
}
