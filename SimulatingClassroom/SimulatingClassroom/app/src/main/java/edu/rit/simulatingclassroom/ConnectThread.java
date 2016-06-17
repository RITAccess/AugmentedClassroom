package edu.rit.simulatingclassroom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ConnectThread extends Thread {
    private BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    private final Handler mHandler;

    public ConnectThread(String deviceID, Handler handler) {
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceID);
        mHandler = handler;
        try {
            mBluetoothSocket = mDevice
                    .createRfcommSocketToServiceRecord(MainActivity.uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            Log.d("ConnectThread", "trying to connect to socket!");
            mBluetoothSocket.connect();
            Log.d("ConnectThread", "got socket!" + mBluetoothSocket.toString());
            manageConnectedSocket();
        } catch (IOException connectException) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void manageConnectedSocket() {
        ConnectionThread conn = new ConnectionThread(
                mBluetoothSocket, mHandler);

        mHandler.obtainMessage(
                1, conn)
                .sendToTarget();
        Log.d("ConnectThread", "Connect thread is starting!");
        conn.start();
    }

    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
        }
    }
}
