package com.mcuhq.simplebluetooth;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPref {
    SharedPreferences pref;
    private static AppPref instance;

    public static AppPref getIns(){
        return instance;
    }
    public static void init(Context context){
        instance = new AppPref();
        instance.pref = context.getSharedPreferences("btsms",Context.MODE_PRIVATE);
    }

    public void saveLastDeviceConnected(String device){
        pref.edit().putString("last_device",device).commit();
    }

    public String getLastDeviceConnected(){
        return pref.getString("last_device","");
    }
}
