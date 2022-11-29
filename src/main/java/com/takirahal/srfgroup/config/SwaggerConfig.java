package com.takirahal.srfgroup.config;

import com.takirahal.srfgroup.constants.SrfGroupConstants;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.parameters.Parameter;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> operation
                // .addParametersItem(new Parameter().in("header").required(false).description("Alert header").name("X-app-alert"))
                .addParametersItem(new Parameter().in("header").required(false).description("Access token mandatory").name("X-Access-Token"))
                .addParametersItem(new Parameter().in("header").required(true).description("ar / fr / en").name(SrfGroupConstants.LANG_KEY))
                .addParametersItem(new Parameter().in("header").required(true).description("WebBrowser / MobileBrowser").name(SrfGroupConstants.SOURCE_CONNECTED_DEVICE));
                // .addParametersItem(new Parameter().in("cookie").required(false).description("Caller indentifier").name("X-Caller-Name"));
    }

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI().addServersItem(new Server().url("https://myserver.com"))
//                .addServersItem(new Server().url("https://google.com"))
//                .components(new Components().addSecuritySchemes("basicScheme",
//                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
//                .info(new Info().title("SpringShop API").version("0.0.1")
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
//    }
}
