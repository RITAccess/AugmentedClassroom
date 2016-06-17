package edu.rit.simulatingclassroom;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class PhoneActivity extends ActionBarActivity {


    private ConnectionThread mBluetoothConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        //This string here is the bluetooth UID of the glasses.
        connectToBluetoothServer("88:33:14:5F:10:5A");
        final TextView captionView = (TextView)findViewById(R.id.captionText);
        captionView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mBluetoothConnection != null){
                    //This shows how you can write stuff to the glasses.
                    mBluetoothConnection.write(captionView.getText().toString().getBytes());
                }
            }
        });

        Bundle extras = this.getIntent().getExtras();
        String sPort = extras.getString("port");
        String sIp = extras.getString("ip");
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.connectToSocket(sIp,Integer.parseInt(sPort));
    }

    public void playPause(View v){
        if(mBluetoothConnection != null){
            mBluetoothConnection.write("P::".getBytes());
        }
        SocketManager.getInstance().sendData(3, true);
    }

    private void connectToBluetoothServer(String id) {
        new ConnectThread(id, mHandler).start();
    }
    public void clickedOne(View v){
        SocketManager.getInstance().sendData(3, true);
        if(mBluetoothConnection != null){
            mBluetoothConnection.write("V:@@0".getBytes());
        }
    }
    public void clickedTwo(View v){
        if(mBluetoothConnection != null){
            mBluetoothConnection.write("V:@@1".getBytes());
        }
    }
    private String data = "";

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    mBluetoothConnection = (ConnectionThread) msg.obj;
                    break;
                }
                case 2: {
                    data = (String) msg.obj;

                    Log.d("data: " , data);
                }
                default:
                    break;
            }
        }
    };

}
