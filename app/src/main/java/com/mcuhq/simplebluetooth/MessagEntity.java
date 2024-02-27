package com.mcuhq.simplebluetooth;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.mcuhq.simplebluetooth.bluetooth.Logger;

import java.io.File;
import java.util.Date;

public class MessagEntity {
    public String id;
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
            if(arr[0].equalsIgnoreCase("file")){
                imageFilePath =arr[1] ;
                String fileName = new File(imageFilePath).getName();
                String[] part = fileName.split("_");
                id = part[0];
                isSMS = false;

                return;
            }

            sender = arr[1];
            body = arr[2];
            threadId =arr[3];
            type =arr[4];
            if(arr[5].equalsIgnoreCase("1")){
                isSMS = true;
            }else {
                isSMS = false;
            }
            id = arr[6];
        }catch (Exception ex){
            Logger.log("msg error:"+data);
            ex.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        int ismsm = isSMS ? 1 : 0;
        String sms = sender +"::"+ body+"::"+threadId+"::"+type+"::"+ismsm+"::"+id;
        if(isReply) return "reply::"+ sms;
        return "sms::"+ sms;
    }
}
