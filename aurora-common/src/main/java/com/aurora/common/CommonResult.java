package com.aurora.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description 统一返回web结果封装
 * @Date 2022-11-19 23:49
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class CommonResult<T> implements Serializable {

    /**
     * 序列化id
     */
    @Serial
    private static final long serialVersionUID = 3886133510113334083L;

    /**
     * 信息
     */
    private String message;

    /**
     * 响应码
     */
    private int code;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功带数据返回
     *
     * @param data 数据
     * @param <T>  响应数据
     * @return CommonResult
     */
    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(data);
        commonResult.setCode(CodeEnum.SUCCESS.getCode());
        commonResult.setMessage(CodeEnum.SUCCESS.getMessage());
        return commonResult;
    }


    /**
     * 成功带数据、消息返回
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     响应数据
     * @return CommonResult
     */
    public static <T> CommonResult<T> success(String message, T data) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(data);
        commonResult.setCode(CodeEnum.SUCCESS.getCode());
        commonResult.setMessage(message);
        return commonResult;
    }


    /**
     * 成功空参返回
     *
     * @param <T> 响应数据
     * @return CommonResult
     */
    public static <T> CommonResult<T> success() {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(null);
        commonResult.setCode(CodeEnum.SUCCESS.getCode());
        commonResult.setMessage(CodeEnum.SUCCESS.getMessage());
        return commonResult;
    }

    /**
     * 失败返回
     *
     * @param <T> 响应数据
     * @return CommonResult
     */
    public static <T> CommonResult<T> fail() {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(null);
        commonResult.setCode(CodeEnum.ERROR.getCode());
        commonResult.setMessage(CodeEnum.ERROR.getMessage());
        return commonResult;
    }


    /**
     * 失败带数据、状态码返回
     *
     * @param errCode    错误代码
     * @param errMessage 错误消息
     * @param <T>        响应数据
     * @return CommonResult
     */
    public static <T> CommonResult<T> fail(int errCode, String errMessage) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(null);
        commonResult.setCode(errCode);
        commonResult.setMessage(errMessage);
        return commonResult;
    }


    /**
     * 失败全参返回
     *
     * @param <T>        响应数据
     * @param errMessage 错误消息
     * @param errCode    错误代码
     * @return CommonResult
     */
    public static <T> CommonResult<T> fail(int errCode, String errMessage, T data) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setData(data);
        commonResult.setCode(errCode);
        commonResult.setMessage(errMessage);
        return commonResult;
    }

}
