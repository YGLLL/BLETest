package com.example.administrator.bletest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class BlueDeviceViewHolder extends RecyclerView.ViewHolder {

    TextView mTvName;
    TextView mTvMac;

    public BlueDeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        mTvName=itemView.findViewById(R.id.tv_name);
        mTvMac=itemView.findViewById(R.id.tv_mac);
    }
}
