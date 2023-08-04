package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用于返回结果
 * @param <T>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> {


    private Integer code;
    private String msg;
    private T data;


    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(1, "success", null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, null);
    }
}
