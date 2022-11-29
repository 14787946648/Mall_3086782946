package com.aurora.user.interfaces.assembler;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Stream;

/**
 * @Description User控制层DTO转换DO
 * @Date 2022-11-28 0:16
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@MapperConfig
@SuppressWarnings("unused")
public interface BaseMapping<S, T> {

    /**
     * 映射同名属性
     *
     * @param var1 var1
     * @return 转换结果
     */
    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    T sourceToTarget(S var1);

    /**
     * 反向，映射同名属性
     *
     * @param var1 var1
     * @return 转换结果
     */
    @InheritInverseConfiguration(name = "sourceToTarget")
    S targetToSource(T var1);

    /**
     * 映射同名属性，集合形式
     *
     * @param var1 var1
     * @return 转换结果
     */
    @InheritConfiguration(name = "sourceToTarget")
    List<T> sourceToTarget(List<S> var1);

    /**
     * 反向，映射同名属性，集合形式
     *
     * @param var1 var1
     * @return 转换结果
     */
    @InheritConfiguration(name = "targetToSource")
    List<S> targetToSource(List<T> var1);

    /**
     * 映射同名属性，集合流形式
     *
     * @param stream var1
     * @return 转换结果
     */
    List<T> sourceToTarget(Stream<S> stream);

    /**
     * 反向，映射同名属性，集合流形式
     *
     * @param stream var1
     * @return 转换结果
     */
    List<S> targetToSource(Stream<T> stream);

}


