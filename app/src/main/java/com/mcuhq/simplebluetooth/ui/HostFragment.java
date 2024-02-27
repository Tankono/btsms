package com.mcuhq.simplebluetooth.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.base.ActivitySingleFragment;
import com.mcuhq.simplebluetooth.bluetooth.BTConnectListener;
import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.bluetooth.BTDataArrivedListener;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.AutoEnableDiscovery;

public class HostFragment extends Fragment {
    RecyclerView rv;
    SmsAdapter adapter = new SmsAdapter();
    boolean hasConnectedDevice = false;
    TextView tvStatus,tvDeviceName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        tvStatus = view.findViewById(R.id.tvDeviceStatus);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);

        adapter.listener = (itemView, entity, pos) -> ActivitySingleFragment.show(getActivity(), new ConversationHostFragment(entity));
        getView().findViewById(R.id.btAllowDiscovery).setOnClickListener(view1 -> BTController.getInstance().enableVisibility(300));
        enableDiscovery();
        view.findViewById(R.id.btBack).setOnClickListener(view12 -> getActivity().onBackPressed());

    }

    @Override
    public void onResume() {
        super.onResume();
        initBluetooth();
    }

    private void initBluetooth(){
        BTController.getInstance().connectListener = new BTConnectListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnect(BluetoothDevice device, int status) {
                if(!isAdded()) return;
                getActivity().runOnUiThread(() -> {
                    if(status == 0) {
                        tvStatus.setText("Connected");
                        tvDeviceName.setText(device.getName());

                        hasConnectedDevice = true;
                        auto.isStop = true;

                    }else {
                        tvStatus.setText("No device connected.");
                        tvDeviceName.setText("N/A");
                        hasConnectedDevice = false;
                        enableDiscovery();
                        adapter.clear();
                    }
                });
            }
            @Override
            public void onLostConnect(BluetoothDevice device) {
            }
        };
        BTController.getInstance().start();
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                handlerMessage(data);
            }

            @Override
            public void onSendData(String data) {

            }
        };
    }

    private void handlerMessage(String data){
        if(!isAdded()) return;

        getActivity().runOnUiThread(() -> {
            String[] arr = data.split("::");
            if(arr[0].equalsIgnoreCase("cmd")){
                return;
            }
            MessagEntity sms = new MessagEntity(data);
            Logger.log(""+data);
            adapter.addItem(sms);
        });
    }

    AutoEnableDiscovery auto = new AutoEnableDiscovery();

    private void enableDiscovery(){
        tvStatus.setText("Waiting device for connected...");
        auto = new AutoEnableDiscovery();
        auto.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            auto.interrupt();
            BTController.getInstance().stop();
        }catch (Exception ex){ex.printStackTrace();}
    }
}