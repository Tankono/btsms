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
    public String imageFilePath;

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
            threadId =arr[3];
            type =arr[4];
            if(arr[5].equalsIgnoreCase("1")){
                isSMS = true;
            }else {
                isSMS = false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        int ismsm = isSMS ? 1 : 0;
        String sms = sender +"::"+ body+"::"+threadId+"::"+type+"::"+ismsm;
        if(isReply) return "reply::"+ sms;
        return "sms::"+ sms;
    }
}
