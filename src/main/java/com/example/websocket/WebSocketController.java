package com.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebSocketController(SimpMessagingTemplate mesgTemplate, WebSocketSessionManager sessionMgr_) {
        this.sessionManager = sessionMgr_;
        this.messagingTemplate = mesgTemplate;
    }

    @MessageMapping("/message")
    public void handleMessage(myMessage message) {
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMesg());
        messagingTemplate.convertAndSend("/topic/messages", message);
        System.out.println("Sent message to /topic/messages: " + message.getUser() + ": " + message.getMesg());
    }

    @MessageMapping("/connect")
    public void connectUser(String sz_username) {
        sessionManager.addUsername(sz_username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(sz_username + " connected");
    }

    @MessageMapping("/disconnect")
    public void disconnectUser(String sz_username) {
        sessionManager.removeUsername(sz_username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(sz_username + " disconnected");
    }

    @MessageMapping("/request-users")
    public void requestUsers() {
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }
}
