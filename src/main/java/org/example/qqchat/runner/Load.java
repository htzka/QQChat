package org.example.qqchat.runner;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.qqchat.controller.ReceptionMsg;
import org.example.qqchat.pojo.Face;
import org.example.qqchat.pojo.FaceXml;
import org.example.qqchat.pojo.UserXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class Load {
    @Autowired
    OkHttpClient okHttpClient;
    @Autowired
    ReceptionMsg receptionMsg;

    public static String receviePort;
    public static String sendPort;
    public static Map<String,String> faceMapper=new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    private void Loading(){

        try {

            LoadUserXml();

            LinkToQQ();

            LoadFace();


        }catch (Exception e){
            log.error("出现异常"+e.getMessage());
        }


    }


    private void LinkToQQ() {
        //后续可以修改成从配置文件读取端口

        Request request = new Request.Builder()
                .url("ws://127.0.0.1:"+receviePort)
                .build();
        log.debug("连接napcat中... 接收端口号为"+receviePort);

        try {
            okHttpClient.newWebSocket(request, receptionMsg);
            log.debug("连接请求发送成功");
        }catch (Exception e){
            log.error("ws连接napcat时出现异常：{}",e.getMessage());
        }
    }

    private void LoadUserXml() throws IOException {
        log.debug("正在载入用户配置");
        XmlMapper xmlMapper = new XmlMapper();
        UserXml userXml=xmlMapper.readValue(new File("src\\main\\resources\\user.xml"), UserXml.class);
        receviePort=userXml.getReceivePort();
        sendPort= userXml.getSendPort();


    }

    private void LoadFace() throws IOException {
        log.debug("正在载入face映射");
        XmlMapper xmlMapper = new XmlMapper();
        for (Face face : xmlMapper.readValue(new File("src\\main\\resources\\face.xml"), FaceXml.class).getFaces()) {
            faceMapper.put(face.getFaceId(),face.getMapper());
        }


    }























}
