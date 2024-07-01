package com.lyf.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer{

  // Kryo线程不安全，需要使用 ThreadLocal保证每个线程只有一个kryo实例
  private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(
      () -> {
        Kryo kryo = new Kryo();
        // 默认情况下，Kryo是不支持类注册的，所以这里需要关闭
        kryo.setRegistrationRequired(false);
        return kryo;
      }
  );

  @Override
  public <T> byte[] serialize(T object) throws IOException {
    // Object to bytes
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Output output = new Output(byteArrayOutputStream);
    KRYO_THREAD_LOCAL.get().writeObject(output, object);
    output.close();

    return byteArrayOutputStream.toByteArray();
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
    // bytes to object
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    Input input = new Input(byteArrayInputStream);
    T object = KRYO_THREAD_LOCAL.get().readObject(input, type);
    input.close();

    return object;
  }
}
