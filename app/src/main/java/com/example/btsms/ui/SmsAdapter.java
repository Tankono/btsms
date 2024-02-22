package com.example.btsms.ui;

import android.bluetooth.BluetoothDevice;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btsms.R;
import com.example.btsms.SmsEntity;

import java.util.ArrayList;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHoder> {
    interface OnItemConnect{
        void connect(BluetoothDevice device);
    }
    ArrayList<SmsEntity> devices = new ArrayList<>();
    OnItemConnect itemConnect;

    @NonNull
    @Override
    public SmsViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms,parent,false);
        return new SmsViewHoder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHoder holder, int position) {
        holder.bindItem(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addItem(SmsEntity smsMessage){
        devices.add(smsMessage);
        notifyDataSetChanged();
    }

    public void clear(){
        devices.clear();
        notifyDataSetChanged();
    }

    class SmsViewHoder extends RecyclerView.ViewHolder{

        public SmsViewHoder(@NonNull View itemView) {
            super(itemView);
        }
        public void bindItem(SmsEntity device){
            TextView tv = itemView.findViewById(R.id.tvSms);
            tv.setText(device.content);
            itemView.findViewById(R.id.btReply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(itemConnect != null) itemConnect.connect(device);
                }
            });
        }
    }
}
