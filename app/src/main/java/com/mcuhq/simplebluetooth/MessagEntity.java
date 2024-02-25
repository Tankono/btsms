package com.mcuhq.simplebluetooth;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.Date;

public class MessagEntity {
    public String sender;
    public String body;
    public String threadId;
    public boolean isReply = false;
    public String type = "";
    public Date dateTime = new Date();

    public boolean isSMS = true;
    public Bitmap bitmap;
    public MessagEntity(){}
    public MessagEntity(String sender, String content){
        this.sender = sender;
        this.body = content;
    }

    public MessagEntity(String owner, String content, boolean isReply){
        this.sender = owner;
        this.body = content;
        this.isReply = isReply;
    }

    public MessagEntity(String data){
        String[] arr = data.split("::");
        try{
            sender = arr[1];
            body = arr[2];
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
//        if(isReply) return "reply::"+ sender +"::"+ body;
        return "sms::"+ sender +"::"+ body;
    }
}
