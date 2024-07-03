package com.lyf.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ProtocolMessageSerializerEnum {

  JDK(0, "jdk"),
  KRYO(1, "kryo"),
  HESSIAN(2, "hessian"),
  JSON(3, "json");

  private final int key;
  private final String value;

  ProtocolMessageSerializerEnum(int key, String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * 获取值列表
   */
  public static List<String> getValues() {
    return Arrays.stream(values()).map(e -> e.value).collect(Collectors.toList());
  }

  /**
   * 根据key获取枚举
   */
  public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
    for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
      if (serializerEnum.key == key) {
        return serializerEnum;
      }
    }
    return null;
  }

  /**
   * 根据value获取枚举
   */
  public static ProtocolMessageSerializerEnum getValueByKey(String value) {
    if (ObjectUtil.isEmpty(value)) {
      return null;
    }
    for (ProtocolMessageSerializerEnum serializerEnum : ProtocolMessageSerializerEnum.values()) {
      if (serializerEnum.value.equals(value)) {
        return serializerEnum;
      }
    }
    return null;
  }

}
