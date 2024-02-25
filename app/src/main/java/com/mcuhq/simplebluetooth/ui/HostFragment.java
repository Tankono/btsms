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
        return inflater.inflate(R.layout.host_fragment,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBluetooth();
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        tvStatus = view.findViewById(R.id.tvDeviceStatus);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);

        getView().findViewById(R.id.btAllowDiscovery).setOnClickListener(view1 -> BTController.getInstance().enableVisibility(300));
        enableDiscovery();
    }

    private void initBluetooth(){
        BTController.getInstance().connectListener = new BTConnectListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnect(BluetoothDevice device, int status) {
                getActivity().runOnUiThread(() -> {
                    if(status == 0) {
                        tvStatus.setText("Connected");
                        tvDeviceName.setText(device.getName());

                        hasConnectedDevice = true;
                        auto.isStop = true;
                        auto.interrupt();
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
                getActivity().runOnUiThread(() -> {
                    MessagEntity sms = new MessagEntity(data);
                    adapter.addItem(sms);
                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
    }

    private void showReplyDialog(MessagEntity sms){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Reply:"+sms.sender);
        alertDialog.setMessage("Message");
        final EditText input = new EditText(getContext());
        alertDialog.setView(input);
        alertDialog.setPositiveButton("YES",
                (dialog, which) -> {
                    Logger.log("message:"+input.getText());
                    sms.isReply = true;
                    sms.body = input.getText().toString().trim();
                    BTController.getInstance().sendString(sms.toString());
                });
        alertDialog.setNegativeButton("NO",
                (dialog, which) -> {
                    dialog.cancel();
                });
        alertDialog.show();

    }

    private void sendHandShake(){

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
        }catch (Exception ex){ex.printStackTrace();}
    }
}