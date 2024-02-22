package com.example.btsms.bluetooth;

//
//class AcceptThread extends Thread {
//    private final BluetoothServerSocket serverSocket;
//    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//    @SuppressLint("MissingPermission")
//    public AcceptThread() {
//        BluetoothServerSocket tmp = null;
//        try {
//            tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, MY_UUID);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        serverSocket = tmp;
//    }
//
//    public void run() {
//        setName("AcceptThread");
//        BluetoothSocket socket;
//        while (state != BTController.STATE_CONNECTED) {
//            try {
//                socket = serverSocket.accept();
//            } catch (IOException e) {
//                break;
//            }
//
//            // If a connection was accepted
//            if (socket != null) {
//                synchronized (BTController.this) {
//                    switch (state) {
//                        case STATE_LISTEN:
//                        case STATE_CONNECTING:
//                            // start the connected thread.
//                            connected(socket, socket.getRemoteDevice());
//                            break;
//                        case STATE_NONE:
//                        case STATE_CONNECTED:
//                            // Either not ready or already connected. Terminate
//                            // new socket.
//                            try {
//                                socket.close();
//                            } catch (IOException e) {
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//    }
//
//    public void cancel() {
//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//        }
//    }
//}
