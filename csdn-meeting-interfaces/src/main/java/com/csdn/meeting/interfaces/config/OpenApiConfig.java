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
                        .description("会议管理完整 API，包含以下模块：\n\n" +
                                "- **图片上传**：上传封面图/海报图到本地磁盘，返回可直接使用的 URL\n" +
                                "- **发起会议**：AI 智能解析、活动模板、四级日程管理\n" +
                                "- **我的会议**：草稿、已发布、历史三页签\n" +
                                "- **会议详情与数据统计**：报名人数、浏览量等\n" +
                                "- **权益购买**：会议权益与价格配置\n" +
                                "- **会议简报**：Markdown 转 PDF/Word 导出\n" +
                                "- **推广配置**：推广渠道与效果估算\n" +
                                "- **报名审核**：报名列表与审核操作\n" +
                                "- **标签订阅**：用户标签订阅管理")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSDN")
                                .url("https://www.csdn.net")
                                .email("support@csdn.net")));
    }
}
