package com.mcuhq.simplebluetooth.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.base.ActivitySingleFragment;
import com.mcuhq.simplebluetooth.bluetooth.BTConnectListener;
import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.bluetooth.BTDataArrivedListener;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.FilePickerHelper;
import com.mcuhq.simplebluetooth.helper.SmsHepler;

import java.util.ArrayList;

public class FileSendFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    MessagEntity sms = new MessagEntity();
    public FileSendFragment(){}
    public FileSendFragment(MessagEntity sms){
        this.sms = sms;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_send, null);
    }

    private void filePicker(){
        String[] mimeTypes = {"image/*", "video/*", "application/pdf", "audio/*"};

        Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), 111);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
//                selectedFile = data?.data;
                if(data != null){
                    Uri selectedFile = data.getData();
                    String selectedFilePath = FilePickerHelper.getPath(getActivity(), selectedFile);
                    MessagEntity msg = new MessagEntity();
                    msg.imageFilePath = selectedFilePath;
                    msg.type = "sent";
                    adapter.addItem(msg);

                    BTController.getInstance().sendFile(selectedFilePath);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "File choosing cancelled", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Error while choosing this file", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.data.size()-1);

        view.findViewById(R.id.btFile).setOnClickListener(view1 -> filePicker());
        initBT();
    }
    private void initBT() {
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                getActivity().runOnUiThread(() -> {
                    Logger.log(data);
                    try {
                        String[] arr = data.split("::");
                        String cmd = arr[0];
                        if(cmd.equalsIgnoreCase("cmd")){
                            String threadId = arr[1];
//                            synThread(threadId);
                        }else if (cmd.equalsIgnoreCase("reply")){
                            MessagEntity sms = new MessagEntity(data);
//                            sendSms(sms);
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
                });
            }

            @Override
            public void onLostConnect(BluetoothDevice device) {}
        };
    }
}
