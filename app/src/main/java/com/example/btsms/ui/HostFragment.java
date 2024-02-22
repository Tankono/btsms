package com.example.btsms.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btsms.R;
import com.example.btsms.SmsEntity;
import com.example.btsms.bluetooth.BTConnectListener;
import com.example.btsms.bluetooth.BTController;
import com.example.btsms.bluetooth.BTDataArrivedListener;

public class HostFragment extends Fragment {
    TextView tvStatus;
    RecyclerView rv;
    SmsAdapter adapter = new SmsAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.host_fragment,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBluetooth();
        tvStatus = view.findViewById(R.id.tvStatus);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        getView().findViewById(R.id.btAllowDiscovery).setOnClickListener(view1 -> BTController.getInstance().enableVisibility(300));
    }

    private void initBluetooth(){
        BTController.getInstance().connectListener = new BTConnectListener() {

            @Override
            public void onConnect(BluetoothDevice device, int status) {
                getActivity().runOnUiThread(() -> {
                    if(status == 0) {
                        tvStatus.setText("Connected:"+device.getName());
                    }
                });
            }

            @Override
            public void onLostConnect(BluetoothDevice device) {

            }
        };
        BTController.getInstance().enableVisibility(300);
        BTController.getInstance().start();
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmsEntity sms = new SmsEntity();
                        sms.owner = "";
                        sms.content = data;
                        adapter.addItem(sms);
                    }
                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
    }
}
