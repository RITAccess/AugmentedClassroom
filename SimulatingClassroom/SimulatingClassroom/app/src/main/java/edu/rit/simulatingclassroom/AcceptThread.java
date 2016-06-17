package edu.rit.simulatingclassroom;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//This is the server thread.
public class AcceptThread extends Thread {
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mBluetoothSocket = null;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    private final Handler mHandler;

    public AcceptThread(Handler handler) {
        mHandler = handler;
        try {
            mServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("Simulating",
                            MainActivity.uuid);
        } catch (IOException e) {
        }
    }

    public void run() {
        while (true) {
            try {
                mBluetoothSocket = mServerSocket.accept();
                manageConnectedSocket();
                Log.d("AcceptThread", "Always listening...");
            } catch (IOException e1) {
                if (mBluetoothSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    private void manageConnectedSocket() {
        ConnectionThread conn = new ConnectionThread(mBluetoothSocket, mHandler);
        mHandler.obtainMessage(1, conn)
                .sendToTarget();
        conn.start();
    }

    public void cancel() {
        try {
            if (null != mServerSocket)
                mServerSocket.close();
        } catch (IOException e) {
        }
    }
}
