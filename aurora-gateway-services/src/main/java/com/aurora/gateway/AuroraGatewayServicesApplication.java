package com.aurora.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author wh14787946648@outlook.com
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuroraGatewayServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuroraGatewayServicesApplication.class, args);
    }

}
