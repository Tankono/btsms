package com.mcuhq.simplebluetooth.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        switch (viewType){
            case 0:
                itemViewId = R.layout.item_sms;
                break;
            case 1:
                itemViewId = R.layout.item_sms_sent;
                break;
            case 2:
                itemViewId = R.layout.item_sms_short;
                break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(itemViewId,parent,false);
        return new SmsViewHoder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).type.equalsIgnoreCase("sent")) return 1;
        if(data.get(position).type.equalsIgnoreCase("short")) return 2;
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
        if(smsMessage.id != null && !smsMessage.id.isEmpty()){
            for (MessagEntity sms : data){
                if(smsMessage.id.equalsIgnoreCase(sms.id)){
                    sms.imageFilePath = smsMessage.imageFilePath;
                    notifyDataSetChanged();
                    return;
                }
            }
        }

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
            if(sms.type.equalsIgnoreCase("short")){
                tvSender.setText(sms.sender);
//                tvSender.setText(sms.sender+"[id "+sms.id+"]");
            }else {
                tvSender.setVisibility(View.GONE);
            }
            tvSms.setText(sms.body);
            if(sms.body == null){
                tvSms.setVisibility(View.GONE);
            }
            String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(sms.dateTime);
            tvDatetime.setText(date);
            if(!sms.isSMS){
                if(sms.bitmap != null && iv != null){
                    iv.setImageBitmap(sms.bitmap);
                }
                if(sms.imageFilePath != null){
                    Bitmap bmp = BitmapFactory.decodeFile(sms.imageFilePath);
                    iv.setImageBitmap(bmp);
                }
            }
        }
    }
}
