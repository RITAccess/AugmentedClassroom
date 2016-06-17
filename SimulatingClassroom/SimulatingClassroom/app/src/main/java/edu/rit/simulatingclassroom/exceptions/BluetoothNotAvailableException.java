package edu.rit.simulatingclassroom.exceptions;

/**
 * Created by Stefan on 2/19/2015.
 */
public class BluetoothNotAvailableException extends RuntimeException{
    public BluetoothNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    public BluetoothNotAvailableException() {
        super("The use of a bluetooth appears to not be available for this device.");
    }
}
