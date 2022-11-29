package com.aurora.user.interfaces.facade;

import com.aurora.common.CommonResult;
import com.aurora.user.interfaces.dto.UserLoginDto;
import com.aurora.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description 用户web调用接口
 * @Date 2022-11-22 3:12
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@RestController
@Slf4j
@RequestMapping("/api/rest/aurora-user-services")
public class UserRestController {

    @Resource
    private RedisUtil cacheService;
    /**
     * 用户登录接口
     *
     * @param userLoginDto 登录请求参数
     * @return 处理结果
     */
    @PostMapping("/login")
    public CommonResult<Object> login(@RequestBody @Validated UserLoginDto userLoginDto) {
        cacheService.set("key001", "woshiceshi1数据");
        System.err.println("设置完成！");
        System.err.println("开始获取：" + cacheService.get("key001"));
        return CommonResult.success(userLoginDto);
    }


    /**
     * 判断登录状态接口：用户刷新页面时由web前端请求
     *
     * @return 处理结果
     */
    @PostMapping("/reLoginStatus")
    public CommonResult<Object> reLoginStatus() {
        return CommonResult.success();
    }

    /**
     * 用户注销接口
     *
     * @return 处理结果
     */
    @PostMapping("/logout")
    public CommonResult<Object> logout() {
        return CommonResult.success();
    }
}
