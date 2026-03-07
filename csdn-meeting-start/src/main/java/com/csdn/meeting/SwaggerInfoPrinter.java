package com.csdn.meeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用启动后打印 OpenAPI/Swagger 文档地址（SpringDoc）
 */
@Component
public class SwaggerInfoPrinter implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SwaggerInfoPrinter.class);

    private final Environment environment;

    public SwaggerInfoPrinter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws UnknownHostException {
        String serverPort = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String localhost = "localhost";

        String baseUrl = "http://" + localhost + ":" + serverPort + contextPath;
        String swaggerUiUrl = baseUrl + "/swagger-ui.html";
        String apiDocsUrl = baseUrl + "/v3/api-docs";

        log.info("==================================================");
        log.info("  CSDN 会议服务启动成功！");
        log.info("--------------------------------------------------");
        log.info("  Swagger UI 地址: {}", swaggerUiUrl);
        log.info("  API 文档地址:    {}", apiDocsUrl);
        log.info("--------------------------------------------------");
        log.info("  本地访问: http://localhost:{}{}", serverPort, contextPath);
        log.info("  网络访问: http://{}:{}{}", hostAddress, serverPort, contextPath);
        log.info("==================================================");
    }
}
