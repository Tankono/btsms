package com.example.btsms.helper;

import com.example.btsms.bluetooth.BTController;
import com.example.btsms.bluetooth.Logger;

public class AutoEnableDiscovery extends Thread{
    public boolean isStop = false;
    public AutoEnableDiscovery(){

    }
    @Override
    public void run() {
        super.run();

        while (true){
            if(isStop) break;
            Logger.log("start enable discovery in 300 seconds");
            BTController.getInstance().enableVisibility(300);

            try {
                Thread.sleep(350*1000);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
