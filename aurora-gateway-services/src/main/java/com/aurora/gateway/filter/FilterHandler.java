package com.aurora.gateway.filter;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

/**
 * @Description 全局过滤器接口
 * @Date 2022-11-22 2:06
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public interface FilterHandler extends GatewayFilter, GlobalFilter, Ordered {
}
