package com.mcuhq.simplebluetooth.ui;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcuhq.simplebluetooth.R;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BTViewHoder> {
    interface OnItemConnect{
        void connect(BluetoothDevice device);
    }
    ArrayList<BluetoothDevice> devices = new ArrayList<>();
    OnItemConnect itemConnect;

    @NonNull
    @Override
    public BTViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
        return new BTViewHoder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BTViewHoder holder, int position) {
        holder.bindItem(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevices(BluetoothDevice device){
        devices.add(device);
        notifyDataSetChanged();
    }

    public void clear(){
        devices.clear();
        notifyDataSetChanged();
    }

    class BTViewHoder extends RecyclerView.ViewHolder{

        public BTViewHoder(@NonNull View itemView) {
            super(itemView);
        }
        public void bindItem(BluetoothDevice device){
            TextView tv = itemView.findViewById(R.id.tvDeviceName);
            tv.setText(device.getName());
            itemView.findViewById(R.id.btConnect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemConnect != null) itemConnect.connect(device);
                }
            });
        }
    }
}
