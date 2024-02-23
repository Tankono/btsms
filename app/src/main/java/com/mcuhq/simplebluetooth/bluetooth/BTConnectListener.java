package com.mcuhq.simplebluetooth.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BTConnectListener {
    void onConnect(BluetoothDevice device, int status);
    void onLostConnect(BluetoothDevice device);
}
