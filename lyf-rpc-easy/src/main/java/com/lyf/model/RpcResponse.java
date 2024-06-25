package com.lyf.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 返回数据
     */
    private Object data;
    /**
     * 返回数据类型
     */
    private Class<?> dataType;
    /**
     * 异常信息
     */
    private String message;
    /**
     * 异常
     */
    private Exception exception;

}
