package com.aurora.user.interfaces.assembler;

import com.aurora.user.interfaces.dto.UserLoginDto;

import java.util.List;
import java.util.stream.Stream;

/**
 * @Description 用户相关DTO--->DO
 * @Date 2022-11-29 0:37
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public class UserLoginMapping implements BaseMapping<UserLoginDto, UserLoginDo> {
    @Override
    public UserLoginDo sourceToTarget(UserLoginDto var1) {
        return null;
    }

    @Override
    public UserLoginDto targetToSource(UserLoginDo var1) {
        return null;
    }

    @Override
    public List<UserLoginDo> sourceToTarget(List<UserLoginDto> var1) {
        return null;
    }

    @Override
    public List<UserLoginDto> targetToSource(List<UserLoginDo> var1) {
        return null;
    }

    @Override
    public List<UserLoginDo> sourceToTarget(Stream<UserLoginDto> stream) {
        return null;
    }

    @Override
    public List<UserLoginDto> targetToSource(Stream<UserLoginDo> stream) {
        return null;
    }
}
