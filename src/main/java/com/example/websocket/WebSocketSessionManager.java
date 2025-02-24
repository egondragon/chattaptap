package com.example.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WebSocketSessionManager {
    private final ArrayList<String> activeUserNames = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addUsername(String sz_username) {
        this.activeUserNames.add(sz_username);
    }

    public void removeUsername(String sz_username) {
        this.activeUserNames.remove(sz_username);
    }

    public void broadcastActiveUsernames() {
        messagingTemplate.convertAndSend("/topic/users", this.activeUserNames);
        System.out.println("Broadcasting active users to /topic/users " + this.activeUserNames);
    }
}
