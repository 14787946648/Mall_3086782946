package com.aurora.exception;

import com.aurora.enumeration.CodeEnum;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * @Description 自定义异常类
 * @Author wh14787946648@outlook.com
 * @Date 2022-11-19 23:27
 * @Version 1.0
 **/
@NoArgsConstructor
@Setter
public class BusinessException extends RuntimeException {
    /**
     * 默认错误码
     */
    private static final Integer DEFAULT_ERROR_CODE = -1;

    /**
     * 错误代码
     */
    private int errorCode;

    /**
     * 错误提示消息
     */
    private String errorMsg;

    /**
     * BaseBizException构造
     *
     * @param errorMsg 错误消息
     */
    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorCode = DEFAULT_ERROR_CODE;
        this.errorMsg = errorMsg;
    }

    /**
     * BaseBizException构造
     *
     * @param errorCode 错误代码
     * @param errorMsg  错误消息
     */
    public BusinessException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * BaseBizException构造
     *
     * @param baseErrorCodeEnum 错误代码接口
     */
    public BusinessException(CodeEnum baseErrorCodeEnum) {
        super(baseErrorCodeEnum.getMessage());
        this.errorCode = baseErrorCodeEnum.getCode();
        this.errorMsg = baseErrorCodeEnum.getMessage();
    }

    /**
     * BaseBizException构造
     *
     * @param errorCode 错误代码
     * @param errorMsg  错误消息
     * @param arguments 错误消息详情
     */
    public BusinessException(int errorCode, String errorMsg, Object... arguments) {
        super(MessageFormat.format(errorMsg, arguments));
        this.errorCode = errorCode;
        this.errorMsg = MessageFormat.format(errorMsg, arguments);
    }

    /**
     * BaseBizException构造
     *
     * @param baseErrorCodeEnum 错误代码接口
     * @param arguments         错误消息详情
     */
    public BusinessException(CodeEnum baseErrorCodeEnum, Object... arguments) {
        super(MessageFormat.format(baseErrorCodeEnum.getMessage(), arguments));
        this.errorCode = baseErrorCodeEnum.getCode();
        this.errorMsg = MessageFormat.format(baseErrorCodeEnum.getMessage(), arguments);
    }
}

