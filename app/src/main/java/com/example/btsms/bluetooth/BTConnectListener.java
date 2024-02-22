package com.example.btsms.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface BTConnectListener {
    void onConnect(BluetoothDevice device, int status);
    void onLostConnect(BluetoothDevice device);
}
