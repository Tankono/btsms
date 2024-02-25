package com.mcuhq.simplebluetooth.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BTFileTransferListener {
    void onReceivedFileData(BluetoothDevice device,String fileName,byte[] fileData);
    void onSendFile(String fileName);
}
