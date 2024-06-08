//package com.api_gateway.todo.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//public class GatewayConfig {
//    @Bean
//    public RestTemplate template() {
//        return new RestTemplate();
//    }
////    @Bean
////    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
////        return builder.routes()
////                .route("todo-user", r -> r.path("/v1/user/**")
////                        .uri("lb://todo-user"))
////                .route("mail-sms-todo", r -> r.path("/mail/**", "/sms/**")
////                        .uri("lb://mail_sms_todo"))
////                .build();
////    }
//}