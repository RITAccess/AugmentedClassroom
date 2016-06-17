package edu.rit.simulatingclassroom.exceptions;

/**
 * Created by Stefan on 2/19/2015.
 */
public class DiscoveryStartException extends RuntimeException {
    public DiscoveryStartException() {
        super("Bluetooth discovery failed to start!");
    }
}
