package com.garmin.connect.exceptions;


/**
 * Exception thrown when connection to Garmin Connect fails
 */
class GarminConnectConnectionException extends GarminConnectException {
    public GarminConnectConnectionException(String message) {
        super(message);
    }
    
    public GarminConnectConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
