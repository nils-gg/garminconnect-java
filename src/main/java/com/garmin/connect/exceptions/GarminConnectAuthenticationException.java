package com.garmin.connect.exceptions;


/**
 * Exception thrown when authentication fails
 */
class GarminConnectAuthenticationException extends GarminConnectException {
    public GarminConnectAuthenticationException(String message) {
        super(message);
    }
    
    public GarminConnectAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
