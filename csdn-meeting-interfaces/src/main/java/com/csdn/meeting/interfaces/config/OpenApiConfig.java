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
                        .description("会议管理完整 API：发起会议（AI 解析、活动模板、四级日程）、我的会议三页签、" +
                                "会议详情与数据统计、权益购买、会议简报、推广配置、报名审核、标签订阅等。")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSDN")
                                .url("https://www.csdn.net")
                                .email("support@csdn.net")));
    }
}
