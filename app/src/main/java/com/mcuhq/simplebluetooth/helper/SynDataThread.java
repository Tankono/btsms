package com.mcuhq.simplebluetooth.helper;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;

import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.bluetooth.BTController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
                if(sms.isSMS){
                    BTController.getInstance().sendString(sms.toString());
                }else {
                    BTController.getInstance().sendString(sms.toString());
                    if(sms.bitmap != null){
                        String file = savebitmap(sms);
                        BTController.getInstance().sendFile(file);
                    }
                }
                sleep(500);
            }catch (Exception ex){ex.printStackTrace();}
        }
    }
    public String savebitmap(MessagEntity sms) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            sms.bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator +sms.id+"_"+System.currentTimeMillis()+"_mms.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            return f.getAbsolutePath();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
