package com.lyf.protocol;

import com.lyf.serializer.Serializer;
import com.lyf.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

public class ProtocolMessageEncoder {
  public static Buffer encode(ProtocolMessage<?> protocolMessage) {
    if (protocolMessage == null || protocolMessage.getHeader() == null) {
      return Buffer.buffer();
    }
    ProtocolMessage.Header header = protocolMessage.getHeader();
    // 向缓冲区写入byte 顺序不能错误 否则编码时字段会错位
    Buffer buffer = Buffer.buffer();
    buffer.appendByte(header.getMagic());
    buffer.appendByte(header.getVersion());
    buffer.appendByte(header.getSerializer());
    buffer.appendByte(header.getType());
    buffer.appendByte(header.getStatus());
    buffer.appendLong(header.getRequestId());
    // get serializer
    ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
    if (serializerEnum == null) {
      throw new RuntimeException("序列化协议不存在");
    }
    Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
    try {
      byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
      // 写入body的数据和长度
      buffer.appendInt(bodyBytes.length);
      buffer.appendBytes(bodyBytes);
      return buffer;
    } catch (Exception e) {
      throw new RuntimeException("序列化失败");
    }
  }
}
