package com.lyf.annotation;

import com.lyf.bootstrap.RpcConsumerBootstrap;
import com.lyf.bootstrap.RpcInitBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
  boolean needServer() default true;
}
