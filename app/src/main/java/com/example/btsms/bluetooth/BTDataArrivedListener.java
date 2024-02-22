package com.example.btsms.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BTDataArrivedListener {
    void onReceivedData(BluetoothDevice device,String data);
    void onSendData(String data);

}
