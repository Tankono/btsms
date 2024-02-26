package com.mcuhq.simplebluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SuppressLint("MissingPermission")
public class BTController {
    public static final int BLUE_TOOTH_DIALOG = 0x111;
    public static final int BLUE_TOOTH_TOAST = 0x123;
    public static final int BLUE_TOOTH_WRAITE = 0X222;
    public static final int BLUE_TOOTH_READ = 0X333;
    public static final int BLUE_TOOTH_WRAITE_FILE_NOW = 0X511;
    public static final int BLUE_TOOTH_READ_FILE_NOW = 0X996;
    public static final int BLUE_TOOTH_WRAITE_FILE = 0X555;
    public static final int BLUE_TOOTH_READ_FILE = 0X888;
    public static final int BLUE_TOOTH_SUCCESS = 0x444;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int STATE_TRANSFER = 3;
    public static final String DEVICE_OBJECT = "device_name";

    private static final String APP_NAME = "Btsms";
    private static final UUID MY_UUID = UUID.fromString("45e255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private AcceptThread acceptThread;
    private ConnectThread connectingThread;
    private ReadWriteThread readWriteThread;
    private int state;
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BTIMBluetooth/";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BTConnectListener connectListener;
    public BTDataArrivedListener dataArrivedListener;
    public BTDiscoveryListener discoveryListener;

    private final Context context;
    private static BTController instance;

    public static BTController getInstance() { return instance; }

    public static void init(Context context) { instance = new BTController(context); }

    private BluetoothDevice connectingDevice;

    private static final int FLAG_MSG = 0;
    private static final int FLAG_FILE = 1;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private BTController(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BTController.STATE_CONNECTED:
                        Logger.log("STATE_CONNECTED....");
                        if (connectListener != null) {
                            connectListener.onConnect(connectingDevice,0);
                        }
                        break;
                    case STATE_CONNECTING:
                        Logger.log("STATE_CONNECTING....");
                        break;
                    case STATE_LISTEN:
                        Logger.log("STATE_CONNECTING....");
                        break;
                    case STATE_NONE:
                        Logger.log("STATE_NONE....");
                        break;
                }
                break;
            case MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
                Logger.log("MESSAGE_WRITE....");
//                String writeMessage = new String(writeBuf);
                String write = (String) msg.obj;
                if (dataArrivedListener != null) dataArrivedListener.onSendData(write);
                break;
            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
                Logger.log("MESSAGE_READ....");
//                String readMessage = new String(readBuf, 0, msg.arg1);
                String read = (String) msg.obj;
                if (dataArrivedListener != null)
                    dataArrivedListener.onReceivedData(connectingDevice, read);

                break;
            case BLUE_TOOTH_WRAITE_FILE:
                Log.i("file", "transfer file finish:"+msg.obj + "");
                if (dataArrivedListener != null)
                    dataArrivedListener.onReceivedData(connectingDevice, "file::"+msg.obj);
                break;
            case BLUE_TOOTH_WRAITE_FILE_NOW:
                Logger.log("start transfer file"+msg.obj);
                break;
            case BLUE_TOOTH_READ_FILE_NOW:
                Log.i("file", msg.obj + " file done.");
                if (dataArrivedListener != null)
                    dataArrivedListener.onReceivedData(connectingDevice, "file::"+msg.obj);
                break;
            case MESSAGE_DEVICE_OBJECT:
                Logger.log("MESSAGE_DEVICE_OBJECT....");
                connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                break;
            case BLUE_TOOTH_READ_FILE:
                Log.i("file", "文件接收完成(" + msg.obj + ")");
                break;
            case MESSAGE_TOAST:
                if (connectListener != null) {
                    connectListener.onConnect(null,1);
                }
                break;
        }
        return false;
    });


    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Logger.log("found devices !");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Logger.log("- device bonded:"+device.getName()+ "\n" + device.getAddress());
                }
                if(device.getName() != null) {
                    if(discoveryListener != null) discoveryListener.onFound(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if(discoveryListener != null) discoveryListener.onDiscoveryDone();
            }
        }
    };

    private void sendMessageToUi(int what, Object s) {
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = s;
        handler.sendMessage(message);
    }
    public void scanDevice() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> pairs = new ArrayList<>();
        pairs.addAll(pairedDevices);
        if(discoveryListener != null) discoveryListener.onGetPairDevices(pairs);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(discoveryFinishReceiver, filter);
    }

    public void enableVisibility(int duration){
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        context.startActivity(enableIntent);
    }

    private synchronized void setState(int state) {
        this.state = state;
        handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return state;
    }

    public synchronized void start() {
        if (connectingThread != null) {
            connectingThread.cancel();
            connectingThread = null;
        }

        if (readWriteThread != null) {
            readWriteThread.cancel();
            readWriteThread = null;
        }

        setState(STATE_LISTEN);
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        if (state == STATE_CONNECTING) {
            if (connectingThread != null) {
                connectingThread.cancel();
                connectingThread = null;
            }
        }

        if (readWriteThread != null) {
            readWriteThread.cancel();
            readWriteThread = null;
        }

        connectingThread = new ConnectThread(device);
        connectingThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread
        if (connectingThread != null) {
            connectingThread.cancel();
            connectingThread = null;
        }

        // Cancel running thread
        if (readWriteThread != null) {
            readWriteThread.cancel();
            readWriteThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        readWriteThread = new ReadWriteThread(socket);
        readWriteThread.start();

        Message msg = handler.obtainMessage(MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DEVICE_OBJECT, device);
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (connectingThread != null) {
            connectingThread.cancel();
            connectingThread = null;
        }

        if (readWriteThread != null) {
            readWriteThread.cancel();
            readWriteThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(STATE_NONE);
    }

    private void write(byte[] out) {
        ReadWriteThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED)
                return;
            r = readWriteThread;
        }

        r.write(out);
    }

    public void onDesTroy(){
        if(discoveryFinishReceiver != null){
            context.unregisterReceiver(discoveryFinishReceiver);
        }
        stop();
    }

    public String getBTDeviceName(BluetoothDevice device){
        return device.getName();
    }

    public String getBTDeviceAddress(BluetoothDevice device){
        return device.getAddress();
    }

    public String getBTDeviceDescription(BluetoothDevice device){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return device.getAddress()+","+device.getAlias();
        }
        return device.getAddress();
    }

    public void sendString(String msg){
        if (getState() != STATE_CONNECTED) {
            Logger.log("Connection was lost!");
            return;
        }

        if (msg.length() > 0) {
//            byte[] send = msg.getBytes();
//            write(send);

            ReadWriteThread r;
            synchronized (this) {
                if (state != STATE_CONNECTED)
                    return;
                r = readWriteThread;
            }

            r.writeText(msg);

//            TransferThread r;
//            synchronized (this) {
//                if (mState != STATE_TRANSFER) return;
//                r = mTransferThread;
//            }
//            r.write(msg);
        }

    }

    public void sendFile(String filePath){
        ReadWriteThread r;
        synchronized (this) {
            if (getState() != STATE_TRANSFER) return;
            r = readWriteThread;
        }
        r.writeFile(filePath);

    }

    public void sendBitmap(Bitmap bitmap){
        String filePath = savebitmap(bitmap);
        if(filePath == null) return;

        ReadWriteThread r;
        synchronized (this) {
            if (getState() != STATE_TRANSFER) return;
            r = readWriteThread;
        }
        r.writeFile(filePath);

    }

    public String savebitmap(Bitmap bmp) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "mms_"+System.currentTimeMillis()+".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            return f.getAbsolutePath();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);

        start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket;
            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BTController.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {
            setName("ConnectThread");
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                connectionFailed();
                return;
            }

            synchronized (BTController.this) {
                connectingThread = null;
            }

            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadWriteThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final DataOutputStream OutData;
        private final DataInputStream inData;


        public ReadWriteThread(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
            OutData = new DataOutputStream(outputStream);
            inData = new DataInputStream(inputStream);
        }

        public void run() {
//            byte[] buffer = new byte[1024];
//            readScan();

//            byte[] buffer = new byte[1024*4];
//            int bytes;
            while (true) {
                try {
                    switch (inData.readInt()){
                        case FLAG_MSG:
                            String msg = inData.readUTF();
                            sendMessageToUi(MESSAGE_READ, msg);
                            break;
                        case FLAG_FILE:
                            File destDir = new File(FILE_PATH);
                            if (!destDir.exists())
                                destDir.mkdirs();
                            String fileName = inData.readUTF(); //文件名
                            long fileLen = inData.readLong(); //文件长度
//                            sendMessageToUi(BLUE_TOOTH_READ_FILE_NOW, "received : (" + fileName + ")");

                            long len = 0;
                            int r;
                            byte[] b = new byte[4 * 1024];
                            FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);
                            while ((r = inputStream.read(b)) != -1) {
                                out.write(b, 0, r);
                                len += r;
                                if (len >= fileLen)
                                    break;
                            }
                            Logger.log("save file:"+FILE_PATH);
                            sendMessageToUi(BLUE_TOOTH_READ_FILE_NOW, FILE_PATH+ fileName );

                            break;
                    }
                } catch (IOException e) {
                    connectionLost();
                    e.printStackTrace();
                    BTController.this.start();
                    break;
                }
            }
        }

        private void readScan(){
            byte[] buffer = new byte[1024*4];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    e.printStackTrace();
                    BTController.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                handler.obtainMessage(MESSAGE_WRITE, -1, -1,
                        buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void writeText(final String msg){
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        OutData.writeInt(FLAG_MSG);
                        OutData.writeUTF(msg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    sendMessageToUi(MESSAGE_WRITE, msg);
                }
            });
        }
        public void writeFile(final String filePath) {
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE_NOW, "start send file:(" + filePath + ")");
                        FileInputStream in = new FileInputStream(filePath);
                        File file = new File(filePath);
                        OutData.writeInt(FLAG_FILE);
                        OutData.writeUTF(file.getName());
                        OutData.writeLong(file.length());
                        int r;
                        byte[] b = new byte[4 * 1024];
                        while ((r = in.read(b)) != -1) {
                            OutData.write(b, 0, r);
                        }
                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE, filePath);
                    } catch (Throwable e) {
                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE_NOW, "write file error..");
                    }
                }
            });
        }
//
//        public void writeBitmap(final Bitmap bitmap) {
//            executorService.execute(new Runnable() {
//                public void run() {
//                    try {
//                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE_NOW, "start send file:(" + filePath + ")");
//                        FileInputStream in = new FileInputStream(filePath);
//                        File file = new File(filePath);
//                        OutData.writeInt(FLAG_FILE);
//                        OutData.writeUTF(file.getName());
//                        OutData.writeLong(file.length());
//                        int r;
//                        byte[] b = new byte[4 * 1024];
//                        while ((r = in.read(b)) != -1) {
//                            OutData.write(b, 0, r);
//                        }
//                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE, filePath);
//                    } catch (Throwable e) {
//                        sendMessageToUi(BLUE_TOOTH_WRAITE_FILE_NOW, "write file error..");
//                    }
//                }
//            });
//        }
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
