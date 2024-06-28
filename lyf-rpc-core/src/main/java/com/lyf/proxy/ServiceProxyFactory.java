package com.lyf.proxy;

import com.lyf.RpcApplication;
import com.lyf.utils.ConfigUtils;

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
        if (RpcApplication.getConfig().isMock()) {
            return getMockProxy(interfaceClass);
        }

        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ServiceProxy()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> T getMockProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new MockServiceProxy()
        );
    }

}
