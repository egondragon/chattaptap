package com.example.websocket.client;

import com.example.websocket.myMessage;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {
    private StompSession session;
    private String sz_username;

    public MyStompClient(MessageListener messageListener, String user_) throws ExecutionException, InterruptedException {
        this.sz_username = user_;
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler(messageListener, user_);
        String sz_url = "ws://localhost:8080/ws";

        session = stompClient.connectAsync(sz_url, sessionHandler).get();
    }

    public void sendMessage(myMessage message) {
        try {
            session.send("/app/message", message);
            System.out.println("Message Sent: " + message.getMesg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectUser(String sz_username) {
        session.send("/app/disconnect", sz_username);
        System.out.println("Disconnect User: " + sz_username);
    }
}
