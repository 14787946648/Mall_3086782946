package com.aurora.exception;

import com.aurora.common.CodeEnum;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * @Description 缓存操作异常
 * @Date 2022-11-30 2:53
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@NoArgsConstructor
@Setter
@SuppressWarnings("unused")
public class CacheOperateException extends RuntimeException {
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
    public CacheOperateException(String errorMsg) {
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
    public CacheOperateException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * BaseBizException构造
     *
     * @param baseErrorCodeEnum 错误代码接口
     */
    public CacheOperateException(CodeEnum baseErrorCodeEnum) {
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
    public CacheOperateException(int errorCode, String errorMsg, Object... arguments) {
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
    public CacheOperateException(CodeEnum baseErrorCodeEnum, Object... arguments) {
        super(MessageFormat.format(baseErrorCodeEnum.getMessage(), arguments));
        this.errorCode = baseErrorCodeEnum.getCode();
        this.errorMsg = MessageFormat.format(baseErrorCodeEnum.getMessage(), arguments);
    }
}
