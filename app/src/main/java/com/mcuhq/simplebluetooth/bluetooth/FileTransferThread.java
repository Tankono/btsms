package com.mcuhq.simplebluetooth.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FileTransferThread extends Thread{

    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    String filePath;
    public FileTransferThread(BluetoothSocket socket,String filePath){
        this.bluetoothSocket = socket;
        this.filePath = filePath;
    }
    @Override
    public void run() {
        super.run();
        try {
            File file = new File(filePath);
            outputStream = this.bluetoothSocket.getOutputStream();
            inputStream = this.bluetoothSocket.getInputStream();


            byte[] fileBytes = new byte[(int) file.length()];
            int bytes = inputStream.read(fileBytes);

            ByteBuffer fileNameSize = ByteBuffer.allocate(4);
            fileNameSize.putInt(file.getName().getBytes().length);

            ByteBuffer fileSize = ByteBuffer.allocate(4);
            fileSize.putInt(fileBytes.length);

            outputStream.write(fileNameSize.array());
            outputStream.write(file.getName().getBytes());
            outputStream.write(fileSize.array());
            outputStream.write(fileBytes);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            outputStream.close();
            inputStream.close();
            this.bluetoothSocket.close();

        } catch (IOException ignored) {
        }
    }
    public void send(){

    }
}
