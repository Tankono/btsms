package com.mcuhq.simplebluetooth;

import androidx.annotation.NonNull;

public class SmsEntity {
    public String owner;
    public String content;

    public boolean isReply = false;

    public SmsEntity(){}
    public SmsEntity(String owner, String content){
        this.owner = owner;
        this.content = content;
    }

    public SmsEntity(String owner, String content, boolean isReply){
        this.owner = owner;
        this.content = content;
        this.isReply = isReply;
    }

    public SmsEntity(String data){
        String[] arr = data.split("::");
        try{
            owner = arr[1];
            content = arr[2];
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        if(isReply) return "reply::"+owner+"::"+content;
        return "sms::"+owner+"::"+content;
    }
}
