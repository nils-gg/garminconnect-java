package com.garmin.connect.exceptions;


/**
 * Base exception for all Garmin Connect errors
 */
public class GarminConnectException extends Exception {
    public GarminConnectException(String message) {
        super(message);
    }
    
    public GarminConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
