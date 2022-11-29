package com.aurora.user.application.service;

import com.aurora.common.CommonResult;
import com.aurora.user.interfaces.dto.UserLoginDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description 用户Web请求服务接口
 * @Date 2022-11-29 1:09
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public interface UserRestService {
    /**
     * 用户登录接口
     *
     * @param userLoginDto 登录请求参数
     * @return 登录结果
     */
    CommonResult<Object> login(@RequestBody @Validated UserLoginDto userLoginDto);


    /**
     * 用户登录状态查询接口
     *
     * @return 查询结果
     */
    CommonResult<Object> reLoginStatus();


    /**
     * 用户注销登录接口
     *
     * @return 注销结果
     */
    CommonResult<Object> logout();
}
