package org.example.qqchat.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientBean {
    //connectTimeout：建立连接的超时时间，10秒。
    //readTimeout：从连接读取数据的超时时间，240秒（4分钟）。
    //writeTimeout：向连接写入数据的超时时间，120秒（2分钟）。
    //callTimeout：整个HTTP调用的超时时间（包括连接、写入、读取等全过程），300秒（5分钟）。
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(240, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .callTimeout(300, TimeUnit.SECONDS).build();
    }
}
