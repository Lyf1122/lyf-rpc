package com.lyf.registry;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个简单的注册器
 */
@Slf4j
public class Registry {
    // 注册信息存储在内存中
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>(16);

    /**
     * 注册服务
     * @param serviceName
     * @param classImpl
     */
    public static void registerService(String serviceName, Class<?> classImpl) {
        map.put(serviceName, classImpl);
        System.out.println("register service: " + serviceName);
    }

    /**
     * 获取注册的服务
     * @param serviceName
     * @return
     */
    public static Class<?> getService(String serviceName) {
        return map.get(serviceName);
    }

    public static void deleteService(String serviceName) {
        map.remove(serviceName);
    }

}
