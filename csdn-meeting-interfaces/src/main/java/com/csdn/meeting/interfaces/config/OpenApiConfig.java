package com.csdn.meeting.interfaces.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 配置（SpringDoc）
 * 替代原 Springfox/Swagger 2 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CSDN会议服务API文档")
                        .description("会议列表、检索、标签订阅相关接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSDN")
                                .url("https://www.csdn.net")
                                .email("support@csdn.net")));
    }
}
