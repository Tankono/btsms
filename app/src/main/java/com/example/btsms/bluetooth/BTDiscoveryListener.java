package com.example.btsms.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface BTDiscoveryListener {
  void onDiscoveryDone();
  void onFound(BluetoothDevice device);
  void onGetPairDevices(List<BluetoothDevice> devices);
}
