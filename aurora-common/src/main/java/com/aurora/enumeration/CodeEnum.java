package com.aurora.enumeration;

import lombok.Getter;

/**
 * @Description 状态码枚举
 * @Author wh14787946648@outlook.com
 * @Date 2022-11-22 1:48
 * @Version 1.0
 **/
@Getter
public enum CodeEnum {

    /**
     * 操作成功
     **/
    SUCCESS(200, "success"),
    /**
     * 服务调用异常
     **/
    SERVICE_CALL_EXCEPTION(400, "服务调用异常"),
    /**
     * 操作失败
     **/
    ERROR(500, "error"),
    /**
     * 参数不合法
     **/
    ILLEGAL_PARAMETER(5001, "参数不合法"),
    /**
     * 验证码已失效
     **/
    VERIFICATION_CODE_FAILURE(5002, "验证码已失效"),
    /**
     * 用户昵称重复
     **/
    DUPLICATE_NICKNAME(5003, "用户昵称重复"),
    /**
     * 用户名或密码错误
     **/
    LOGIN_FAILED(5004, "用户名或密码错误"),
    /**
     * 文件上传失败
     **/
    FILE_UPLOAD_FAILED(5005, "文件上传失败"),
    /**
     * 资源不存在
     */
    RESOURCE_DOES_NOT_EXIST(5006, "资源不存在"),
    /**
     * 无效签名
     **/
    JWT_INVALID(2001, "无效签名"),
    /**
     * token过期
     **/
    JWT_OVERDUE(2002, "token过期"),
    /**
     * token算法不一致
     **/
    JWT_ALGORITHM_INCONSISTENCY(2003, "token算法不一致"),
    /**
     * token失效
     **/
    JWT_LOSE_EFFECT(2004, "token失效"),
    /**
     * 非法请求
     **/
    ILLEGAL_REQUEST(2005, "非法请求,请求来源不合法");

    /**
     * 自定义状态码
     **/
    private final Integer code;
    /**
     * 自定义描述
     **/
    private final String message;

    CodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

