package com.lyf.constant;

/**
 * 为什么用接口存储常量？
 * 接口可以被实现继承，多个类可以共享，避免重复定义
 */
public interface RpcConstant {
    String DEFAULT_CONFIG_PREFIX = "rpc";
}
