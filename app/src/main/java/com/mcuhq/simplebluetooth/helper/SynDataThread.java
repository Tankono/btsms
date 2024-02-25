package com.mcuhq.simplebluetooth.helper;

import android.os.Build;

import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.bluetooth.BTController;

import java.util.ArrayList;

public class SynDataThread extends Thread{
    String threadId;
    public SynDataThread(String threadId){
        this.threadId = threadId;
    }
    @Override
    public void run() {
        super.run();
        ArrayList<MessagEntity> data = new ArrayList<>();
        data.addAll(SmsHepler.Instance().getMMS(threadId));
        data.addAll(SmsHepler.Instance().getSmsByThread(threadId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.sort((t2, t1) -> {
                if(t2.dateTime.getTime() > t1.dateTime.getTime()){
                    return 1;
                }else {
                    return -1;
                }
            });
        }
        for (MessagEntity sms: data){
            try {
                BTController.getInstance().sendString(sms.toString());
                sleep(500);
            }catch (Exception ex){ex.printStackTrace();}
        }
    }
}
