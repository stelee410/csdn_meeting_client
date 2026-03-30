//package com.csdn.meeting.infrastructure.client;
//
//import com.csdn.meeting.infrastructure.config.ImageStorageProperties;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.nio.file.Path;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("LocalImageStorageClient")
//class LocalImageStorageClientTest {
//
//    @TempDir
//    Path tempDir;
//
//    @AfterEach
//    void clearRequestContext() {
//        RequestContextHolder.resetRequestAttributes();
//    }
//
//    @Test
//    @DisplayName("issue001：URL 不含双 http")
//    void store_urlWithoutDoubleProtocol() {
//        ImageStorageProperties props = new ImageStorageProperties();
//        props.setBasePath(tempDir.toString());
//        props.setAccessUrlPrefix("http://http://47.94.193.166:5678/uploads/images");
//        LocalImageStorageClient client = new LocalImageStorageClient(props);
//        client.init();
//
//        byte[] bytes = "fake-image".getBytes();
//        String url = client.store(bytes, "test.png");
//
//        assertNotNull(url);
//        assertFalse(url.contains("http://http://"));
//        assertTrue(url.startsWith("http://"));
//    }
//
//    @Test
//    @DisplayName("正常前缀时 URL 拼接正确")
//    void store_normalPrefix() {
//        ImageStorageProperties props = new ImageStorageProperties();
//        props.setBasePath(tempDir.toString());
//        props.setAccessUrlPrefix("http://localhost:8080/uploads/images");
//        LocalImageStorageClient client = new LocalImageStorageClient(props);
//        client.init();
//
//        byte[] bytes = "fake".getBytes();
//        String url = client.store(bytes, "a.jpg");
//
//        assertNotNull(url);
//        assertTrue(url.startsWith("http://localhost:8080/uploads/images/"));
//        assertTrue(url.endsWith(".jpg"));
//        assertFalse(url.replace("http://", "").contains("//"));
//    }
//
//    @Test
//    @DisplayName(":port/path 前缀时按当前请求拼 host，端口用配置")
//    void store_relativePortPrefix_usesRequestHost() {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setScheme("http");
//        request.setServerName("localhost");
//        request.setServerPort(8080);
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//
//        ImageStorageProperties props = new ImageStorageProperties();
//        props.setBasePath(tempDir.toString());
//        props.setAccessUrlPrefix(":9090/uploads/images/");
//        LocalImageStorageClient client = new LocalImageStorageClient(props);
//        client.init();
//
//        String url = client.store("x".getBytes(), "a.jpg");
//
//        assertTrue(url.startsWith("http://localhost:9090/uploads/images/"), url);
//        assertTrue(url.endsWith(".jpg"));
//    }
//
//    @Test
//    @DisplayName(":port/path 无请求上下文时回退 localhost")
//    void store_relativePortPrefix_noRequest_fallsBackLocalhost() {
//        ImageStorageProperties props = new ImageStorageProperties();
//        props.setBasePath(tempDir.toString());
//        props.setAccessUrlPrefix(":8080/uploads/images/");
//        LocalImageStorageClient client = new LocalImageStorageClient(props);
//        client.init();
//
//        String url = client.store("x".getBytes(), "b.png");
//
//        assertTrue(url.startsWith("http://localhost:8080/uploads/images/"), url);
//    }
//}
