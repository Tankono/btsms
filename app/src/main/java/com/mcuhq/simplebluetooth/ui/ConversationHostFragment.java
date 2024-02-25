package com.mcuhq.simplebluetooth.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.bluetooth.BTController;
import com.mcuhq.simplebluetooth.bluetooth.BTDataArrivedListener;
import com.mcuhq.simplebluetooth.bluetooth.Logger;
import com.mcuhq.simplebluetooth.helper.SmsHepler;

public class ConversationHostFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    MessagEntity sms = new MessagEntity();
    EditText edtReply;
    public ConversationHostFragment(){}
    public ConversationHostFragment(MessagEntity sms){
        this.sms = sms;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.data.size()-1);

        edtReply = view.findViewById(R.id.edtReply);
        view.findViewById(R.id.btSend).setOnClickListener(view1 -> {
            String reply = edtReply.getText().toString();
            sendReply(reply);
        });
        TextView tv = view.findViewById(R.id.tvSender);
        tv.setText(sms.sender);
        setUpBT();
    }

    private void setUpBT(){
        BTController.getInstance().dataArrivedListener = new BTDataArrivedListener() {
            @Override
            public void onReceivedData(BluetoothDevice device, String data) {
                getActivity().runOnUiThread(() -> {
                    MessagEntity sms = new MessagEntity(data);
                    Logger.log(""+data);
                    adapter.addItem(sms);
                });
            }

            @Override
            public void onSendData(String data) {

            }
        };
        BTController.getInstance().sendString("cmd::"+sms.threadId);
    }

    private void sendReply(String reply){
        MessagEntity msg = new MessagEntity();
        msg.isReply = true;
        msg.body = reply;
        msg.sender = sms.sender;
        msg.type = "sent";
        adapter.addItem(msg);

        edtReply.setText("");
        BTController.getInstance().sendString(msg.toString());
        recyclerView.scrollToPosition(adapter.data.size()-1);
    }
}
