package com.aurora.user.interfaces.assembler;

import com.aurora.user.interfaces.dto.UserLoginDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Description User控制层DTO转换DO
 * @Date 2022-11-28 0:16
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Mapper
public interface UserConverter {
    /**
     * 实例化
     */
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

}

