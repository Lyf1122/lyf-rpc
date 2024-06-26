package com.lyf.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    /**
     *
     * @param interfaceClass
     * @return
     * @param <T>
     */

    public static <T> T getProxy(Class<T> interfaceClass)
    {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ServiceProxy()
        );
    }
}
