package com.lyf.protocol;

public enum ProtocolMessageStatusEnum {

  OK("ok", 20),
  BAD_REQUEST("badRequest", 40),
  BAD_RESPONSE("badResponse", 50),;

  private String text;
  private int value;

  ProtocolMessageStatusEnum(String text, int value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public int getValue() {
    return value;
  }

  /**
   * 根据value获取枚举
   */
  public static ProtocolMessageStatusEnum getEnumByValue(int value) {
    for (ProtocolMessageStatusEnum protocolMessageStatusEnum : ProtocolMessageStatusEnum.values()) {
      if (protocolMessageStatusEnum.getValue() == value) {
        return protocolMessageStatusEnum;
      }
    }
    return null;
  }

}
