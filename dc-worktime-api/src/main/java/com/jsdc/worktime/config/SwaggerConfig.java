//package com.jsdc.worktime.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//            .apiInfo(new ApiInfoBuilder().title("API文档").build())
//            .select()
//            .apis(RequestHandlerSelectors.basePackage("com.example.demo"))
//            .build();
//    }
//}
