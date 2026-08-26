package org.example.qqchat.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.qqchat.runner.Load;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
public class ReceptionMsg extends WebSocketListener {

    //自身id
    private static String selfId;
    //收到消息的时间
    private static String receiveTime;
    //发送者id
    private static String senderId;
    //收到的信息
    private static String receiveMsg;
    //收到的消息是从什么地方发送过来的 如group 或private
    private static String receiveType;
    //群聊名
    private static String groupName;
    //发送消息的用户名
    private static String nickname;
    //群聊id
    private static String groupId;

    //群聊Id集合
    public static Set<String> groupIdSet = ConcurrentHashMap.newKeySet();
    //私聊Id集合
    public static Set<String> privateSet = ConcurrentHashMap.newKeySet();

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.debug("napcat连接成功");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        //原始消息
        JsonElement jsonObject = JsonParser.parseString(text);
        //拦截心跳消息
        String postType = jsonObject.getAsJsonObject().get("post_type").getAsString();
        if (postType.equals("meta_event")) return;
        //加载发送者和自身的信息
        LoadSenderMessage(jsonObject);
        log.debug("格式化后收到消息为: "+receiveMsg);

        //原始消息 调试使用
//        log.info("接收的原始消息:"+text);

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
                .url("ws://127.0.0.1:"+ Load.receviePort)
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

    private void LoadSenderMessage(JsonElement jsonObject){

        if (selfId==null|| selfId.isEmpty())selfId=jsonObject.getAsJsonObject().get("self_id").getAsString();

        long receiveTimeSecond=jsonObject.getAsJsonObject().get("time").getAsLong();
        receiveTime =dateTimeFormatter.format(Instant.ofEpochSecond(receiveTimeSecond));

        senderId = jsonObject.getAsJsonObject().get("user_id").getAsString();
        nickname=jsonObject.getAsJsonObject().get("sender").getAsJsonObject().get("nickname").getAsString();

        receiveType=jsonObject.getAsJsonObject().get("message_type").getAsString();

        JsonElement message = jsonObject.getAsJsonObject().get("message");
        receiveMsg=MessageFormat(message);

        if (Objects.equals(receiveType, "group")){
            groupId=jsonObject.getAsJsonObject().get("group_id").getAsString();
            groupName=jsonObject.getAsJsonObject().get("group_name").getAsString();
            groupIdSet.add(groupId);
            return;
        }

        privateSet.add(senderId);

    }

    private String MessageFormat(JsonElement message){
        StringBuffer formatMsg = new StringBuffer();
        for (JsonElement msg : message.getAsJsonArray()) {
            String type= msg.getAsJsonObject().get("type").getAsString();
            JsonElement data = msg.getAsJsonObject().get("data").getAsJsonObject();
            if (type.equals("face")){
                String faceId = data.getAsJsonObject().get("id").getAsString();
                String faceIntro = Load.faceMapper.get(faceId);
                formatMsg.append(" face表情 ").append(faceIntro==null?faceId:faceIntro);
            }
            if (type.equals("text")){
                formatMsg.append(data.getAsJsonObject().get("text").getAsString());
            }
            if (type.equals("at")){
                formatMsg.append("@").append(data.getAsJsonObject().get("qq").getAsString());
            }
            if (type.equals("image")){
                String url = data.getAsJsonObject().get("url").getAsString();
                //暂未实现
            }
        }

        return "<sender id="+senderId+" nickname="+nickname+" receiveType="+receiveType+" receiveTime="+receiveTime+"/>"+ formatMsg;
    }
}
