package com.example.btsms.bluetooth;

import android.bluetooth.BluetoothDevice;

class BluetoothEndPoint {
    public String name;
    public String address;
    public BluetoothDevice bluetoothDevice;
    public String getDeviceName(){
        return bluetoothDevice.getName();
    }
}
