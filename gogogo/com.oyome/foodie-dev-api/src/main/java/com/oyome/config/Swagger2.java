package com.oyome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {
    //http:localhost：8088/swagger-ui.html
    //http:localhost：8088/doc.html

    //配置Swagger2 核心配置 docket
    @Bean
    public Docket createRestApi(){
        //指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())//用于定义api文档汇总信息
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.oyome.controller"))//指定controller包
                .paths(PathSelectors.any()) //所有controller
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("天天吃货 电商平台接口api ")//文档页标题
        .contact(new Contact("oyome","https://www.imooc.com","496174574@qq.com"))//联系人信息
        .description("为天天吃货提供的api")//详细信息
        .version("1.0.1")//版本号
        .termsOfServiceUrl("https://www.imooc.com").build();

    }
}
