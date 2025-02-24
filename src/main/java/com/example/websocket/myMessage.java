package com.example.websocket;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class myMessage {
    private String user;
    private String mesg;

    // Constructeur vide requis par Jackson
    public myMessage() {}

    // Constructeur avec annotations JSON
    @JsonCreator
    public myMessage(@JsonProperty("user") String user, @JsonProperty("mesg") String mesg) {
        this.user = user;
        this.mesg = mesg;
    }

    public String getUser() {
        return user;
    }

    public String getMesg() {
        return mesg;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMesg(String mesg) {
        this.mesg = mesg;
    }
}
