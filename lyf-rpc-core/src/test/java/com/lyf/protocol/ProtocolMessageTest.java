package com.lyf.protocol;

import cn.hutool.core.util.IdUtil;
import com.lyf.constant.RpcConstant;
import com.lyf.model.RpcRequest;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

public class ProtocolMessageTest {

  @Test
  public void testEncodeAndDecode() {
    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
    ProtocolMessage.Header header = new ProtocolMessage.Header();
    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
    header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
    header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
    header.setRequestId(IdUtil.getSnowflakeNextId());
    header.setBodyLength(0);

    RpcRequest rpcRequest = new RpcRequest();
    rpcRequest.setServiceName("testService");
    rpcRequest.setMethodName("testMethod");
    rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
    rpcRequest.setParameterTypeList(new Class[]{String.class});
    rpcRequest.setArgs(new Object[]{"testA", "testB"});
    protocolMessage.setHeader(header);
    protocolMessage.setBody(rpcRequest);

    Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
    ProtocolMessage<?> msg = ProtocolMessageDecoder.decode(encodeBuffer);
    Assert.assertNotNull(msg);

  }

}
