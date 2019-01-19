package com.example.administrator.bletest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SCAN_TIME = 1000*20;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private BluetoothAdapter mAdapter;

    BluetoothManager manager;

    private List<BluetoothDevice> mData=new ArrayList<>();

    private RecyclerView mRv;
    private BlueDeviceAdapter mRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter=manager.getAdapter();

        mRvAdapter=new BlueDeviceAdapter(mData);
        mRv=findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mRvAdapter);
        
        checkBlueOpen();

        getTheFuckPermission();
        startScan();
    }

    private void getTheFuckPermission() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }else {
                startScan();
            }
        }else {
            startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    showToast("获取权限失败");
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startScan() {
        final BluetoothLeScanner scanner = mAdapter.getBluetoothLeScanner();
        if (mAdapter!=null&&mAdapter.isEnabled()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.stopScan(scanCallback);
                }
            },SCAN_TIME);
            Log.e("BLE","run scan");
            scanner.startScan(scanCallback);
        }else {
            Log.e("BLE", "BLE not open，and wait open");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScan();
                }
            }, 1000);
        }
    }

    ScanCallback scanCallback =new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothDevice device = result.getDevice();
            Log.e("run scan","onLeScan");
            if (!haveThisDevice(device.getAddress())){
                mData.add(device);
                mRvAdapter.notifyDataSetChanged();
                Log.e("run scan","device Name"+device.getName());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    BluetoothAdapter.LeScanCallback callback =new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e("run scan","onLeScan");
            if (!haveThisDevice(device.getAddress())){
                mData.add(device);
                mRvAdapter.notifyDataSetChanged();
                Log.e("run scan","device Name"+device.getName());
            }
        }
    };

    private boolean haveThisDevice(String s) {
        for (BluetoothDevice mDatum : mData) {
            if(mDatum.getAddress().equals(s)){
                Log.e("run scan","repetition device");
                return true;
            }
        }
        return false;
    }

    public final static int  EQ_BLUE_ENABLE=128;
    private void checkBlueOpen() {
        if (mAdapter==null||!mAdapter.isEnabled()){
            Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,EQ_BLUE_ENABLE);
        }
    }

    private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
