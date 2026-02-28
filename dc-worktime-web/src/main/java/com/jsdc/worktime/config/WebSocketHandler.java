package com.jsdc.worktime.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // 存储已连接的客户端
    private Map<String, WebSocketSession> sessionMap = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String msg = message.getPayload();
        System.out.println("Received message: " + msg);

        if (msg.startsWith("LOGIN:")) {
            // 处理登录信息
            String [] msgStr = msg.substring(6).split("&");
            String username = msgStr[0];
            String realname = msgStr[1];
            sessionMap.put(username, session);
            System.out.println("User logged in: " + username);
            JSONObject result = new JSONObject();
            result.put("title", "登录提示");
            result.put("body", "欢迎, " + realname + " 登录成功！");
            session.sendMessage(new TextMessage(result.toJSONString()));
        } else if (msg.startsWith("DISCONNECT:"))  {
            // 处理断开逻辑
            String username = msg.substring(11);
            if (username != null && sessionMap.containsKey(username))  {
                sessionMap.remove("username");
                System.out.println("User  disconnected: " + username);
            }
        } else {
            // 处理其他消息
            System.out.println("Processing message: " + msg);
        }
    }

    // 发送消息给客户端
    public void sendMessageToUser(String username, String title,String message) {
        WebSocketSession session = sessionMap.get(username);
        if (session != null && session.isOpen()) {
            try {
                JSONObject result = new JSONObject();
                result.put("title", title);
                result.put("body", message);
                session.sendMessage(new TextMessage(result.toJSONString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 每次建立连接时，可以做一些初始化
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessionMap.remove(username);
            System.out.println("Connection  closed for user: " + username);
        }
    }
}
