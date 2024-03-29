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

import java.util.List;

public class ScanFragment extends Fragment {
    RecyclerView recyclerView;
    BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter();
    String lastDeviceConnected = "";
    boolean discoveryFinish = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBT();
        view.findViewById(R.id.btScan).setOnClickListener(view1 ->{
            adapter.clear();
            discoveryFinish = false;
            BTController.getInstance().scanDevice();

        });
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.itemConnect = device -> BTController.getInstance().connect(device);
        lastDeviceConnected = AppPref.getIns().getLastDeviceConnected();
        Logger.log("last device:"+lastDeviceConnected);
    }

    private void initBT() {
        BTController.getInstance().discoveryListener = new BTDiscoveryListener() {
            @Override
            public void onDiscoveryDone() {
                discoveryFinish = true;
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFound(BluetoothDevice device) {
                getActivity().runOnUiThread(() -> {
                    adapter.addDevices(device);

                    if(device.getName().contains(lastDeviceConnected) && !lastDeviceConnected.isEmpty()){
                        Logger.log("found recent device:"+device.getName());
                        BTController.getInstance().connect(device);
                    }
                });
            }
            @Override
            public void onGetPairDevices(List<BluetoothDevice> devices) {
                Logger.log("pair:"+devices.size());
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
                        AppPref.getIns().saveLastDeviceConnected(device.getName());
                        getActivity().finish();
//                        BTController.getInstance().sendString("sms::hey");
//                        ActivitySingleFragment.show(getActivity(),new FileSendFragment());
                        ActivitySingleFragment.show(getActivity(),new ClientFragment());
                    }else {
                        Logger.log("Device lost connection.");
                    }
                });
            }

            @Override
            public void onLostConnect(BluetoothDevice device) {}
        };
        BTController.getInstance().scanDevice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BTController.getInstance().connectListener = null;
        BTController.getInstance().discoveryListener = null;
    }
}
