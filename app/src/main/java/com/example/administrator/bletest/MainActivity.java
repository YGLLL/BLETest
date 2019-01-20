package com.example.administrator.bletest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BLE应该是特指使用蓝牙4.0的智能硬件
 */
public class MainActivity extends AppCompatActivity {

    private static final int SCAN_TIME = 1000*20;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private BluetoothAdapter mAdapter;

    BluetoothManager manager;

    private List<BluetoothDevice> mData=new ArrayList<>();

    private RecyclerView mRv;
    private BlueDeviceAdapter mRvAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter=manager.getAdapter();

        mProgressBar=findViewById(R.id.progress_bar);
        disLoding();
        mRvAdapter=new BlueDeviceAdapter(mData);
        mRvAdapter.setCallBack(itemCallBack);
        mRv=findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mRvAdapter);
        
        checkBlueOpen();

        getTheFuckPermission();
//        startScan();
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
                    disLoding();
                    scanner.stopScan(scanCallback);
                }
            },SCAN_TIME);
            Log.e("BLE","run scan");
            showLoding();
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

    BlueDeviceAdapter.ItemCallBack itemCallBack=new BlueDeviceAdapter.ItemCallBack() {
        @Override
        public void onItem(int pos) {
            Log.e("itemCallBack","itemCallBack:"+mData.get(pos).getName());
            showLoding();
            mData.get(pos).connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    disLoding();
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }
            });
//            new AcceptThread(
//                    mData.get(pos).getName(),
//                    mData.get(pos).getUuids()[0].getUuid()
//            ).start();
        }
    };

    private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void showLoding(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void disLoding(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(String name,UUID uuid) {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        private void manageConnectedSocket(BluetoothSocket socket) {
            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }
}
