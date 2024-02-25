package com.mcuhq.simplebluetooth.helper;

import com.mcuhq.simplebluetooth.AppPref;
import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.bluetooth.BTController;

public class SynData extends Thread{
    @Override
    public void run() {
        super.run();
        for (MessagEntity sms: AppPref.messageList){
            try {
                BTController.getInstance().sendString(sms.toString());
                sleep(500);
            }catch (Exception ex){ex.printStackTrace();}
        }
    }
}
