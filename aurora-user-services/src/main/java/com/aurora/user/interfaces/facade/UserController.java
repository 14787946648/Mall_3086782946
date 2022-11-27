package com.aurora.user.interfaces.facade;

import com.aurora.response.CommonResult;
import com.aurora.user.interfaces.dto.UserLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description User模块用户端管理器
 * @Date 2022-11-22 3:12
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@RestController
@Slf4j
@RequestMapping("/api/aurora-user-services")
public class UserController {

    /**
     * 用户登录接口
     *
     * @param userLoginDto 登录请求参数
     * @return 处理结果
     */
    @PostMapping("/login")
    public CommonResult<Object> login(@RequestBody @Validated UserLoginDto userLoginDto) {
        return CommonResult.success(userLoginDto);
    }
}
