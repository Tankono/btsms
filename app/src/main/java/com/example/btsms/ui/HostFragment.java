package com.example.btsms.ui;

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

import com.example.btsms.R;
import com.example.btsms.SmsEntity;
import com.example.btsms.bluetooth.BTConnectListener;
import com.example.btsms.bluetooth.BTController;
import com.example.btsms.bluetooth.BTDataArrivedListener;
import com.example.btsms.bluetooth.Logger;
import com.example.btsms.helper.AutoEnableDiscovery;

public class HostFragment extends Fragment {
    TextView tvStatus;
    RecyclerView rv;
    SmsAdapter adapter = new SmsAdapter();
    boolean hasConnectedDevice = false;

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
        adapter.listener = sms -> showReplyDialog(sms);
        enableDiscovery();
    }

    private void initBluetooth(){
        BTController.getInstance().connectListener = new BTConnectListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnect(BluetoothDevice device, int status) {
                getActivity().runOnUiThread(() -> {
                    if(status == 0) {
                        tvStatus.setText("Connected:"+device.getName());
                        hasConnectedDevice = true;
                        auto.isStop = true;
                        auto.interrupt();
                    }else {
                        tvStatus.setText("No device connected, auto enable for discovery.");
                        hasConnectedDevice = false;
                        enableDiscovery();
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
                    SmsEntity sms = new SmsEntity(data);
                    adapter.addItem(sms);
                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
    }

    private void showReplyDialog(SmsEntity sms){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Reply:"+sms.owner);
        alertDialog.setMessage("Message");
        final EditText input = new EditText(getContext());
        alertDialog.setView(input);
        alertDialog.setPositiveButton("YES",
                (dialog, which) -> {
                    Logger.log("message:"+input.getText());
                    sms.isReply = true;
                    sms.content = input.getText().toString().trim();
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