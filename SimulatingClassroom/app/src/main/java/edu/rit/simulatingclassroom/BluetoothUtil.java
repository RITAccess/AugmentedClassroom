package edu.rit.simulatingclassroom;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.rit.simulatingclassroom.exceptions.BluetoothAdapterException;
import edu.rit.simulatingclassroom.exceptions.DiscoveryStartException;
import edu.rit.simulatingclassroom.exceptions.PairedDevicesNullException;

/**
 * Created by Stefan on 2/19/2015.
 */
public class BluetoothUtil {
    private static BroadcastReceiver deviceBroadcastReceiver;
    public static ArrayList<BluetoothDevice> allDevices = new ArrayList<>();


    /**
     * @param bluetoothAdapter The global Android BluetoothAdapter.
     * @return Both the names of the devices as well as the addresses.
     */
    public static List<String> getAllDeviceNamesInfo(@NotNull BluetoothAdapter bluetoothAdapter) {
        final boolean shouldGetName = true;
        final boolean shouldGetAddress = true;
        List<String> deviceNames = getPairedDeviceInformation(bluetoothAdapter, shouldGetName, shouldGetAddress);
        return deviceNames;
    }

    /**
     * @param bluetoothAdapter The global Android BluetoothAdapter.
     * @return A list of device names represented as Strings.
     */
    public static List<String> getPairedDeviceNames(@NotNull BluetoothAdapter bluetoothAdapter) {
        final boolean shouldGetName = true;
        final boolean shouldGetAddress = false;
        List<String> deviceNames = getPairedDeviceInformation(bluetoothAdapter, shouldGetName, shouldGetAddress);
        return deviceNames;
    }

    /**
     * @param bluetoothAdapter The global Android BluetoothAdapter.
     * @return A list of device addresses represented as Strings.
     */
    public static List<String> getPairedDeviceAddresses(@NotNull BluetoothAdapter bluetoothAdapter) {
        final boolean shouldGetName = false;
        final boolean shouldGetAddress = true;
        List<String> deviceNames = getPairedDeviceInformation(bluetoothAdapter, shouldGetName, shouldGetAddress);
        return deviceNames;
    }

    /**
     * @param bluetoothAdapter The global Android BluetoothAdapter
     * @param name             Whether a person would like to get the names of the devices.
     * @param address          Whether a person would like to get the addresses of the bluetooth devices.
     * @return A list of device information for each device the Android phone is currently paired with.
     */
    private static List<String> getPairedDeviceInformation(@NotNull BluetoothAdapter bluetoothAdapter, boolean name, boolean address) {
        if (name == address == false) {
            throw new IllegalArgumentException("Arguments \"name\" and \"address\" cannot both be false.");
        }
        if (bluetoothAdapter == null) {
            throw new BluetoothAdapterException("The BluetoothAdapter cannot be null.");
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices == null) {
            throw new PairedDevicesNullException("Getting bonded devices from bluetooth adapter returned an error!");
        }

        List<String> deviceInfo = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                StringBuffer currentStringBuffer = new StringBuffer();
                if (name) {
                    currentStringBuffer.append(device.getName() + "\n");
                }
                if (address) {
                    currentStringBuffer.append(device.getAddress() + "\n");
                }
                deviceInfo.add(currentStringBuffer.toString());
            }
        }
        return deviceInfo;
    }


    /**
     * Destorys the broadcast receiver. Should call this method in the onDestroy method of activity.
     *
     * @param context The current Android context.
     */
    public static void destroyBroadcastReceiver(@NotNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }
        if (deviceBroadcastReceiver != null) {
            context.unregisterReceiver(deviceBroadcastReceiver);
//        } else {
//            throw new IllegalStateException("deviceBroadcastReceiver is null!");
        }
    }

    /**
     * Creates a Broadcast Receiver that listens for when it finds another bluetooth device.
     *
     * @param context The current Android context.
     */
    public static void createBroadcastReceiver(@NotNull Context context, final ArrayAdapter<String> discoverableDevices) {

        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }


        // Create a BroadcastReceiver for ACTION_FOUND
        deviceBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    allDevices.add(device);
                    // Add the name and address to an array adapter to show in a ListView
                    discoverableDevices.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(deviceBroadcastReceiver, filter); // Don't forget to unregister during onDestroy
    }

    /**
     * Starts the bluetooth discovery so that this device can find other devices.
     *
     * @param bluetoothAdapter The global Android BluetoothAdapter.
     */
    public static void startBluetoothDiscovery(@NotNull BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null) {
            startBluetoothDiscovery(bluetoothAdapter, 0);
        } else {
            throw new BluetoothAdapterException("Bluetooth adapter cannot be null.");
        }

    }

    private static void startBluetoothDiscovery(@NotNull BluetoothAdapter bluetoothAdapter, int amountTries) {
        boolean hasDiscoveryStarted = bluetoothAdapter.startDiscovery();
        final int MAX_TRIES = 3;
        if (amountTries > MAX_TRIES) {
            throw new DiscoveryStartException();
        }
    }


    /**
     * Allows the device to be seen by other bluetooth enabled devices in the area.
     * Uses default 120 seconds as duration.
     *
     * @param context The current Android context.
     */
    public static void makeSelfDiscoverable(@NotNull Activity context) {
        final int DEFAULT_DISCOVERABLE_SECONDS = 120;
        makeSelfDiscoverable(context, DEFAULT_DISCOVERABLE_SECONDS);
    }

    /**
     * Allows the device to be seen by other bluetooth enabled devices in the area.
     *
     * @param context The current Android context.
     * @param seconds The amount of seconds this device should be discoverable for.
     */
    public static void makeSelfDiscoverable(@NotNull Activity context, int seconds) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }
        Intent i = new Intent();
        i.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        context.startActivityForResult(i, 42);
    }

}
