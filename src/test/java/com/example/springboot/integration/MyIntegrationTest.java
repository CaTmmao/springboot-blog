package com.example.springboot.integration;

import com.example.springboot.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// 使用 spring 插件
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = Application.class, // 测试 Application 类
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 应用启动时使用随机端口
// 指明集成测试时使用的 properties 文件的地址
//        (所有资源存放在 resources 文件夹中,
//        打包后该文件夹里所有的资源以原样形式存放在 jar 包内的 classpath 中,
//        所以这里指在 classpath 中的寻找 test.properties 文件)
@TestPropertySource(locations = "classpath:test.properties")
public class MyIntegrationTest {
    @Inject// 注入环境变量
    Environment environment;

    @Test
    public void notLoggedInByDefault() throws IOException, InterruptedException {
        // 获取当前环境中的端口
        String port = environment.getProperty("local.server.port");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(response.body().contains("unLog"));
        Assertions.assertEquals(200, response.statusCode());
    }
}
