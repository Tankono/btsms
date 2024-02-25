package com.mcuhq.simplebluetooth.ui;

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

import com.mcuhq.simplebluetooth.MessagEntity;
import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.helper.SmsHepler;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {
    RecyclerView recyclerView;
    SmsAdapter adapter = new SmsAdapter();
    MessagEntity sms = new MessagEntity();
    public ConversationFragment(){}
    public ConversationFragment(MessagEntity sms){
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
        ArrayList<MessagEntity> data = new ArrayList<>();
        data.addAll(SmsHepler.Instance().getMMS(sms.threadId));
        data.addAll(SmsHepler.Instance().getSmsByThread(sms.threadId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.sort((t2, t1) -> {
                if(t2.dateTime.getTime() > t1.dateTime.getTime()){
                    return 1;
                }else {
                    return -1;
                }
            });
        }
        adapter.data.addAll(data);

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.data.size()-1);

        TextView tv = view.findViewById(R.id.tvSender);
        tv.setText(sms.sender);
    }

    private void sendSms(MessagEntity sms){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sms.sender, null, sms.body, null, null);
    }
}
