package com.mcuhq.simplebluetooth.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcuhq.simplebluetooth.AppPref;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.base.ActivitySingleFragment;
import com.mcuhq.simplebluetooth.bluetooth.BTConnectListener;
import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.bluetooth.BTDataArrivedListener;
import com.mcuhq.simplebluetooth.bluetooth.BTDiscoveryListener;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.SmsHepler;

import java.util.List;

public class ClientFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    TextView tvStatus,tvDeviceName;
    String lastDeviceConnected = "";
    boolean discoveryFinish = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBT();
        adapter.data.addAll(SmsHepler.Instance().getSmsForThread());

        view.findViewById(R.id.btScan).setOnClickListener(view1 ->{
            ActivitySingleFragment.show(getActivity(),new ScanFragment());
        });
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        tvStatus = view.findViewById(R.id.tvDeviceStatus);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);

        adapter.listener = (itemView, entity, pos) ->
                ActivitySingleFragment.show(getActivity(),new ConversationFragment(entity));

        lastDeviceConnected = AppPref.getIns().getLastDeviceConnected();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if(AppPref.currentPair != null){
            tvDeviceName.setText(""+ AppPref.currentPair.getName());
            tvStatus.setText("Connected");
            synMessageWithHost();
        }else{
            tvDeviceName.setText("");
            tvStatus.setText("No Device Connected");
        }
    }

    private void initBT() {
//        BTController.getInstance().discoveryListener = new BTDiscoveryListener() {
//            @Override
//            public void onDiscoveryDone() {
//                discoveryFinish = true;
//            }
//
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onFound(BluetoothDevice device) {
//                getActivity().runOnUiThread(() -> {
////                    adapter.addDevices(device);
//
//                    if(device.getName().contains(lastDeviceConnected)){
//                        Logger.log("found recent device:"+device.getName());
//                        BTController.getInstance().connect(device);
//                    }
//                });
//            }
//            @Override
//            public void onGetPairDevices(List<BluetoothDevice> devices) {}
//        };

//        BTController.getInstance().connectListener = new BTConnectListener() {
//
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onConnect(BluetoothDevice device, int status) {
//                getActivity().runOnUiThread(() -> {
//                    if(status == 0){
//                        tvStatus.setText("Connected:"+device.getName());
//
//                        BTController.getInstance().sendString("handshake::btsms::Demo");
//
//                    }else {
//                        tvStatus.setText("No Device Connected.");
//                        Logger.log("Device lost connection.");
//                    }
//                });
//
//            }
//
//            @Override
//            public void onLostConnect(BluetoothDevice device) {}
//        };
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                getActivity().runOnUiThread(() -> {
                    Logger.log(data);
                    MessagEntity sms = new MessagEntity(data);
                    sendSms(sms);
                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
    }

    private void sendSms(MessagEntity sms){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sms.sender, null, sms.body, null, null);
    }

    private void autoConnect(){

    }

    private void synMessageWithHost(){
        for (MessagEntity msg:adapter.data){
            BTController.getInstance().sendString(msg.toString());
        }
    }
}
