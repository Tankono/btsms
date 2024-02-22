package com.example.btsms.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BluetoothHelper {
    private static BluetoothHelper instance;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> listDevices = new HashSet<>();

    Set<BluetoothEndPoint> btDevice  = new HashSet<>();

    public BTDiscoveryListener listener;
    public int deviceIndex = 0;
    private BTController chatController;


    private Handler handler = new Handler(new Handler.Callback() {

        @SuppressLint("MissingPermission")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BTController.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTController.STATE_CONNECTED:
//                            setStatus("Connected to: " + connectingDevice.getName());
//                            sendMessage("hello cucu...");
//                            showPad();
//                            btnConnect.setEnabled(false);

                            break;
                        case BTController.STATE_CONNECTING:
//                            setStatus("Connecting...");
//                            btnConnect.setEnabled(false);
                            break;
                        case BTController.STATE_LISTEN:
                        case BTController.STATE_NONE:
//                            setStatus("Not connected");
                            break;
                    }
                    break;
                case BTController.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
//                    chatMessages.add("Me: " + writeMessage);
//                    chatAdapter.notifyDataSetChanged();
                    break;
                case BTController.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
//                    chatAdapter.notifyDataSetChanged();
                    break;
                case BTController.MESSAGE_DEVICE_OBJECT:
//                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
//                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
//                            Toast.LENGTH_SHORT).show();
                    break;
                case BTController.MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
//                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public static BluetoothHelper init(Context context){
        instance = new BluetoothHelper(context);
        return instance;
    }

    public static BluetoothHelper Ins(){
        return instance;
    }

    private BluetoothHelper(Context context){
        this.context = context;
    }


    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                LogUtils.error("found devices !");
                deviceIndex++;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    LogUtils.error(deviceIndex +"- device bonded:"+device.getName()+ "\n" + device.getAddress());
                }

                if(!listDevices.contains(device)){
                    listDevices.add(device);
                    if(device.getName() != null) {
                        if(listener != null) listener.onFound(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtils.error("discovery finish:"+listDevices.size()+" devices.");
            }
        }
    };

    @SuppressLint("MissingPermission")
    public  void disCovery(){
        deviceIndex = 0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        listDevices.clear();
        LogUtils.error("start discovery...");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        listDevices.addAll(pairedDevices);

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                LogUtils.error("PAIR:"+device.getName() + "\n" + device.getAddress());
            }
        } else {
            LogUtils.error("No pair device found");
        }
        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

        for(BluetoothDevice device:listDevices){
            if(device.getName() != null){
                list.add(device);
            }
        }

//        if(listener != null) listener.onDiscoveryDone(list);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(discoveryFinishReceiver, filter);

    }

    BTController controller;
    public void startListen(){
        BluetoothHelper.Ins().enableVisibility(300);
//        controller = new BTController(context,handler);
    }

    public void connect(BluetoothDevice device){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        } else {
//            chatController = new BTController(context, handler);
        }

        chatController.connect(device);
    }

    @SuppressLint("MissingPermission")
    public void enableVisibility(int duration){
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        context.startActivity(enableIntent);
    }

    public void onDestroy(){
        if(discoveryFinishReceiver != null){
            context.unregisterReceiver(discoveryFinishReceiver);
        }
//        context.unregisterReceiver(discoveryFinishReceiver);
    }
}
