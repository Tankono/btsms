package com.mcuhq.simplebluetooth.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcuhq.simplebluetooth.R;
import com.mcuhq.simplebluetooth.MessagEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHoder> {

    ArrayList<MessagEntity> data = new ArrayList<>();
    ItemClick<MessagEntity> listener;

    @NonNull
    @Override
    public SmsViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int itemViewId = R.layout.item_sms;
        if(viewType == 1){
            itemViewId = R.layout.item_sms_sent;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(itemViewId,parent,false);
        return new SmsViewHoder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).type.equalsIgnoreCase("sent")) return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHoder holder, int position) {
        holder.bindItem(data.get(position));
        if(listener != null) {
            holder.itemView.setOnClickListener(view -> listener.onClickItem(view, data.get(position),position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(MessagEntity smsMessage){
        data.add(smsMessage);
        notifyDataSetChanged();
    }

    public void clear(){
        data.clear();
        notifyDataSetChanged();
    }

    class SmsViewHoder extends RecyclerView.ViewHolder{
        TextView tvSms;
        TextView tvSender;
        TextView tvDatetime;
        ImageView iv;

        public SmsViewHoder(@NonNull View itemView) {
            super(itemView);
            tvSms = itemView.findViewById(R.id.tvSms);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvDatetime = itemView.findViewById(R.id.tvDate);
            iv = itemView.findViewById(R.id.iv);
        }
        public void bindItem(MessagEntity sms){
            tvSms.setText(sms.body);
            tvSender.setText(sms.sender);
            String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(sms.dateTime);
            tvDatetime.setText(date);
            if(sms.bitmap != null){
                iv.setImageBitmap(sms.bitmap);
            }
        }
    }
}
