package com.aurora.user.application.service.impl;

import com.aurora.common.CommonResult;
import com.aurora.user.interfaces.dto.UserLoginDto;
import com.aurora.user.application.service.UserRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description 用户Web请求服务接口实现：领域服务编排、日志、切面等
 * @Date 2022-11-29 1:13
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Service
@Slf4j
public class UserRestServiceImpl implements UserRestService {

    /**
     * 用户登录接口
     *
     * @param userLoginDto 登录请求参数
     * @return 登录结果
     */
    @Override
    public CommonResult<Object> login(UserLoginDto userLoginDto) {
        // 1、获取登录参数
        // 2、验证是否重复登录：Token是否过期（Redis）
        // 3、校验短信、图像验证码
        // 4、数据库查询匹配账户密码
        // 4.1、匹配失败返回登陆失败
        // 5、登录成功
        // 5.1、签发Token、Token存入cookie
        // 6、触发登录成功事件：加载Token、可访问uri（访问权限）到redis
        // 7、响应web前端
        return null;
    }


    /**
     * 用户登录状态查询接口
     *
     * @return 查询结果
     */
    @Override
    public CommonResult<Object> reLoginStatus() {
        // 1.获取Cookie中的User-Token
        // 2、判断Redis中的Token是否失效
        // 2.1、失效：抛出异常由网关处理 web前端自动跳转到登录页面
        // 3、判断Token过期时间减去刷新时间是否小于当前时间
        // 3.1、ture：重置Token生成新的cookie
        // 3.2、false
        // 4、响应web前端
        return null;
    }

    /**
     * 用户注销登录接口
     *
     * @return 注销结果
     */
    @Override
    public CommonResult<Object> logout() {
        // 1、更新redis中User-Token过期
        // 2、清除Cookie
        return null;
    }
}
