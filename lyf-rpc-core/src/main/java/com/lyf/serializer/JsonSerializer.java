package com.lyf.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements Serializer{

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public <T> byte[] serialize(T object) throws IOException {
    return OBJECT_MAPPER.writeValueAsBytes(object);
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
    T obj = OBJECT_MAPPER.readValue(bytes, type);
    if (obj instanceof RpcRequest) {
      return handleRequest((RpcRequest) obj, type);
    }
    if (obj instanceof RpcResponse) {
      return handleResponse((RpcResponse) obj, type);
    }
    return obj;
  }

  /**
   * 根据请求体的参数类型和参数值，将参数值转换成对应的类型
   * @param rpcRequest
   * @param type
   * @return
   * @param <T>
   * @throws IOException
   */
  private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
    Class<?>[] parametersType = rpcRequest.getParameterTypeList();
    Object[] args = rpcRequest.getArgs();

    for (int i=0; i< parametersType.length; i++) {
      Class<?> clazz = parametersType[i];
      if (!clazz.isAssignableFrom(args[i].getClass())) {
        byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
        args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
      }
    }

    return type.cast(rpcRequest);

  }

  private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
    // 处理响应数据
    byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
    rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, type));
    return type.cast(rpcResponse);
  }


}
