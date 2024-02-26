package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;

import com.mcuhq.simplebluetooth.base.ActivitySingleFragment;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.ui.HostFragment;
import com.mcuhq.simplebluetooth.ui.ScanFragment;

public class Testing {
    public static void test(Activity context){

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
//        Logger.log("ID:" + android_id);
        if (android_id.equalsIgnoreCase("0354e4729a0f1171")) {
            ActivitySingleFragment.show(context, new ScanFragment());
        }else {
            ActivitySingleFragment.show(context, new HostFragment());
        }
    }
}
