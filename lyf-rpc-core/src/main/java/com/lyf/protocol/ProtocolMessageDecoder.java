package com.lyf.protocol;

import com.lyf.model.RpcRequest;
import com.lyf.model.RpcResponse;
import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

public class ProtocolMessageDecoder {
  public static ProtocolMessage<?> decode(Buffer buffer) {
    ProtocolMessage.Header header = new ProtocolMessage.Header();
    byte magic = buffer.getByte(0);
    // check magic number
    if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
      throw new IllegalArgumentException("Magic number is illegal");
    }
    header.setMagic(magic);
    header.setVersion(buffer.getByte(1));
    header.setSerializer(buffer.getByte(2));
    header.setType(buffer.getByte(3));
    header.setStatus(buffer.getByte(4));
    header.setRequestId(buffer.getLong(5));
    header.setBodyLength(buffer.getInt(13));
    // 解决粘包问题 只读指定区间内的字节
    byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
    // 解析消息体
    ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
    if (serializerEnum == null) {
      throw new RuntimeException("序列化消息的协议不存在");
    }
    Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
    ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnum(header.getType());
    if (typeEnum ==  null) {
      throw new RuntimeException("消息类型不存在");
    }
    try {
      return switch (typeEnum) {
        case REQUEST -> {
          RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
          yield new ProtocolMessage<>(header, request);
        }
        case RESPONSE -> {
          RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
          yield new ProtocolMessage<>(header, response);
        }
        case OTHER -> new ProtocolMessage<>(header, null);
        case HEART_BEAT -> new ProtocolMessage<>(header, null);
      };
    } catch (Exception e) {
      throw new RuntimeException("消息体解析失败");
    }
  }
}
