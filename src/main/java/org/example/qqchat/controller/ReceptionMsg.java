package org.example.qqchat.controller;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.qqchat.runner.LinkQQ;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@Slf4j
public class ReceptionMsg extends WebSocketListener {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private OkHttpClient okHttpClient;


    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.debug("napcat连接成功");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.debug("连接关闭");
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.debug("连接出现异常: "+t.getMessage());
        log.debug("5s后重新进行连接...");

        Request request = new Request.Builder()
                .url("ws://127.0.0.1:"+LinkQQ.port)
                .build();

        threadPoolTaskScheduler.schedule(() -> {
            try {
                okHttpClient.newWebSocket(request, this);
                log.debug("连接请求发送成功");
            }catch (Exception e){
                log.error("ws连接napcat时出现异常：{}",e.getMessage());
            }
        }, Instant.now().plusMillis(5000));

    }
}
