package com.example.administrator.bletest;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BlueDeviceAdapter extends RecyclerView.Adapter<BlueDeviceViewHolder> {

    private List<BluetoothDevice> mData;

    public BlueDeviceAdapter(List<BluetoothDevice> list){
        mData=list;
    }

    @NonNull
    @Override
    public BlueDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blue_device,viewGroup,false);
        return new BlueDeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlueDeviceViewHolder blueDeviceViewHolder, int i) {
        blueDeviceViewHolder.mTvMac.setText(mData.get(i).getAddress());
        blueDeviceViewHolder.mTvName.setText(mData.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
