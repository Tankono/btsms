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
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.SmsHepler;
import com.mcuhq.simplebluetooth.helper.SynData;
import com.mcuhq.simplebluetooth.helper.SynDataThread;

public class ClientFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    TextView tvStatus,tvDeviceName;
    String lastDeviceConnected = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client, null);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initBT();
        AppPref.messageList = SmsHepler.Instance().getSmsByThread();
        adapter.data.addAll(AppPref.messageList);

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

    @Override
    public void onResume() {
        super.onResume();
        updateStatus();
        initBT();
    }


    @SuppressLint("MissingPermission")
    private void updateStatus(){
        if(AppPref.currentPair != null){
            tvDeviceName.setText(""+ AppPref.currentPair.getName());
            tvStatus.setText("Connected");
            synMessageWithHost();
        }else{
            tvDeviceName.setText("N/A");
            tvStatus.setText("No Device Connected");
        }
    }

    private void initBT() {
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                if(!isAdded()) return;
                getActivity().runOnUiThread(() -> {
                    Logger.log(data);
                    try {
                        String[] arr = data.split("::");
                        String cmd = arr[0];
                        if(cmd.equalsIgnoreCase("cmd")){
                            String threadId = arr[1];
                            synThread(threadId);
                        }else if (cmd.equalsIgnoreCase("reply")){
                            MessagEntity sms = new MessagEntity(data);
                            sendSms(sms);
                        }
                    }catch (Exception e){}

                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
        BTController.getInstance().connectListener = new BTConnectListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onConnect(BluetoothDevice device, int status) {
                if(!isAdded()) return;
                getActivity().runOnUiThread(() -> {
                    if(status == 0){
                        AppPref.currentPair = device;
                    }else {
                        Logger.log("Device lost connection.");
                        AppPref.currentPair = null;
                    }
                    updateStatus();
                });
            }

            @Override
            public void onLostConnect(BluetoothDevice device) {}
        };
    }

    private void sendSms(MessagEntity sms){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sms.sender, null, sms.body, null, null);
    }

    private void autoConnect(){

    }

    private void synThread(String threadId){
        new SynDataThread(threadId).start();
    }
    private void synMessageWithHost(){
        new SynData().start();
    }
}
