package com.aurora.user.interfaces.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * @Description 登录请求VO
 * @Date 2022-11-22 3:28
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Data
public class UserLoginDto {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 手机验证码
     */
    @NotNull(message = "手机验证码不能为空")
    private Integer mobilePhoneVerificationCode;

    /**
     * 图像验证码
     */
    @NotBlank(message = "图像验证码不能为空")
    private String imageVerificationCode;
}
