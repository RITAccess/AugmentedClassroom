package edu.rit.simulatingclassroom;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import edu.rit.simulatingclassroom.exceptions.BluetoothAdapterException;
import edu.rit.simulatingclassroom.exceptions.BluetoothNotAvailableException;

public class MainActivity extends ActionBarActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    public static UUID uuid;
    private BluetoothAdapter bluetoothAdapter;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //The following shows the IP of the android phone!
        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.d("stuff","Ip is " + ip);
            }
        }).start();




        //This is the UUID of the glasses.
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int REQUEST_DISCOVERABLE_CODE = 42;
        if (requestCode == REQUEST_DISCOVERABLE_CODE) {
            // Bluetooth Discoverable Mode does not return the standard
            // Activity result codes.
            // Instead, the result code is the duration (seconds) of
            // discoverability or a negative number if the user answered "NO".
            if (resultCode < 0) {
                Toast.makeText(this,"Something is wrong!",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Discoverable mode enabled.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    public void clickGlasses(View v){
        setupBluetooth();
        Intent intent = new Intent(MainActivity.this, GlassesActivity.class);
        intent.putExtra("ip",((EditText)findViewById(R.id.ipAddressText)).getText().toString());
        intent.putExtra("port", ((EditText) findViewById(R.id.portText)).getText().toString());
        startActivity(intent);
    }

    public void clickPhone(View v) {
        setupBluetooth();
        Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
        intent.putExtra("ip",((EditText)findViewById(R.id.ipAddressText)).getText().toString());
        intent.putExtra("port",((EditText)findViewById(R.id.portText)).getText().toString());
        Log.d("port", "port is " + ((EditText)findViewById(R.id.portText)).getText().toString());
        Log.d("ip is", "ip is " + ((EditText)findViewById(R.id.ipAddressText)).getText().toString());

        startActivity(intent);
    }
    private void setupBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //If this is the case, the device does not support bluetooth so it cannot use this service.
            throw new BluetoothNotAvailableException();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothUtil.destroyBroadcastReceiver(this);

    }

}
