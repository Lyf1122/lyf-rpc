package com.lyf.server.tcp;

import com.lyf.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 半包 粘包处理
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

  private final RecordParser recordParser;

  public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
    this.recordParser = initRecordParser(bufferHandler);
  }

  private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
    RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
    parser.setOutput(new Handler<Buffer>() {
      int size = -1;
      Buffer buffer = Buffer.buffer();
      @Override
      public void handle(Buffer buffer) {
        if (-1 == size) {
          size = buffer.getInt(13);
          parser.fixedSizeMode(size);
          buffer.appendBuffer(buffer);
        } else {
          buffer.appendBuffer(buffer);
          bufferHandler.handle(buffer);
          parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
          size = -1;
          buffer = Buffer.buffer();
        }
      }
    });
    return parser;
  }

  @Override
  public void handle(Buffer buffer) {

  }
}
