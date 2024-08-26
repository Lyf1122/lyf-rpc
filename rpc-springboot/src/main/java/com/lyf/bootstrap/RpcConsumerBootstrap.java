package com.lyf.bootstrap;

import com.lyf.annotation.RpcReference;
import com.lyf.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RpcConsumerBootstrap implements BeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    Field[] declaredFields = beanClass.getDeclaredFields();
    for (Field declaredField : declaredFields) {
      RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
      if (rpcReference != null) {
        Class<?> interfaceClass = rpcReference.interfaceClass();
        if (interfaceClass == void.class) {
          interfaceClass = declaredField.getType();
        }
        declaredField.setAccessible(true);
        Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
        try {
          declaredField.set(bean, proxyObject);
          declaredField.setAccessible(false);
        } catch (IllegalAccessException e) {
          throw new RuntimeException("为字段注入代理对象失败", e);
        }
      }
    }

    return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
  }
}
