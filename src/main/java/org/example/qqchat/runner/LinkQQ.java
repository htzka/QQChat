package org.example.qqchat.runner;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.qqchat.controller.ReceptionMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LinkQQ {
    @Autowired
    OkHttpClient okHttpClient;
    @Autowired
    ReceptionMsg receptionMsg;

    public static String port="3003";

    @EventListener(ApplicationReadyEvent.class)
    public void LinkToQQ() {
        //后续可以修改成从配置文件读取端口

        Request request = new Request.Builder()
                .url("ws://127.0.0.1:"+port)
                .build();
        log.debug("连接napcat中... ws端口号为"+port);
        try {
            okHttpClient.newWebSocket(request, receptionMsg);
            log.debug("连接请求发送成功");
        }catch (Exception e){
            log.error("ws连接napcat时出现异常：{}",e.getMessage());
        }

    }
}
