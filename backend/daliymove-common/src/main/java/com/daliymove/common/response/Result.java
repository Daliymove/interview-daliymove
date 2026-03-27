package com.daliymove.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * - 封装所有 API 接口的响应数据
 * - 提供统一的响应格式
 * @param <T> 响应数据的类型
 */
@Data
public class Result<T> implements Serializable {

    /** 响应状态码，200 表示成功 */
    private Integer code;
    
    /** 响应消息 */
    private String message;
    
    /** 响应数据 */
    private T data;
    
    /** 响应时间戳 */
    private Long timestamp;

    /** 默认构造函数，自动设置当前时间戳 */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /** 返回成功响应（无数据） */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功响应（带数据）
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 返回成功响应（带消息和数据）
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 返回错误响应（默认 500 状态码）
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 返回错误响应（自定义状态码）
     * @param code 错误状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}