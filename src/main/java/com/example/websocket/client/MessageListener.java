package com.example.websocket.client;

import com.example.websocket.myMessage;

import java.util.ArrayList;
public interface MessageListener {
    void onMessageReceive(myMessage msg);
    void onActiveUsersUpdated(ArrayList<String> users);
}
