package com.mcuhq.simplebluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class AppPref {
    SharedPreferences pref;
    public static String fileSelectPath;

    private static AppPref instance;
    public static BluetoothDevice currentPair;
    public static ArrayList<MessagEntity> messageList = new ArrayList<>();
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
