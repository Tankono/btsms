package com.example.btsms.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btsms.AppPref;
import com.example.btsms.R;
import com.example.btsms.bluetooth.BTConnectListener;
import com.example.btsms.bluetooth.BTController;
import com.example.btsms.bluetooth.BTDiscoveryListener;
import com.example.btsms.bluetooth.LogUtils;

import java.util.List;

public class ClientFragment extends Fragment {
    RecyclerView recyclerView;
    BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter();

    TextView tvStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.client_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBT();
        view.findViewById(R.id.btScan).setOnClickListener(view1 ->{
            adapter.clear();
            BTController.getInstance().scanDevice();
        });
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        tvStatus = view.findViewById(R.id.tvStatus);
        adapter.itemConnect = device -> BTController.getInstance().connect(device);

    }

    private void initBT() {
        BTController.getInstance().discoveryListener = new BTDiscoveryListener() {
            @Override
            public void onDiscoveryDone() {

            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFound(BluetoothDevice device) {
                getActivity().runOnUiThread(() -> {
                    adapter.addDevices(device);
                    if(device.getName().contains(AppPref.getIns().getLastDeviceConnected())){
                        LogUtils.error("found recent device:"+device.getName());
                    }
                });
            }

            @Override
            public void onGetPairDevices(List<BluetoothDevice> devices) {

            }
        };

        BTController.getInstance().connectListener = new BTConnectListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onConnect(BluetoothDevice device, int status) {
                getActivity().runOnUiThread(() -> {
                    if(status == 0){
                        tvStatus.setText("Connected:"+device.getName());
                        AppPref.getIns().saveLastDeviceConnected(device.getName());
                        BTController.getInstance().sendString("btsms");
                    }else {
                        tvStatus.setText("No Device Connected.");
                    }
                });

            }

            @Override
            public void onLostConnect(BluetoothDevice device) {

            }
        };
        BTController.getInstance().scanDevice();
    }

    private void sendSms(){

    }
}
