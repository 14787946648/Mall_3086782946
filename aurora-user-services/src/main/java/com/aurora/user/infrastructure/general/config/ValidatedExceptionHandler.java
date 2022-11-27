package com.aurora.user.infrastructure.general.config;

import com.aurora.response.CommonResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 参数校验异常统一格式返回
 * @Date 2022-11-28 1:34
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@ControllerAdvice
public class ValidatedExceptionHandler {

    /**
     * 实体对象校验时抛出的异常
     *
     * @param bindException BindException
     * @return 统一响应格式，网关会拦截处理
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResult<String> handleBindException(BindException bindException) {
        //获取参数校验异常message
        List<String> defaultMsg = new ArrayList<>(6);
        bindException.getBindingResult().getAllErrors().forEach(re -> defaultMsg.add(re.getDefaultMessage()));
        return CommonResult.fail(400, defaultMsg.toString());
    }
}