package com.mcuhq.simplebluetooth.bluetooth;

import android.util.Log;

public class Logger {
    public static final String TAG = "btsms";
    public static void log(String s) {
        Log.e(TAG,s);
    }
}
