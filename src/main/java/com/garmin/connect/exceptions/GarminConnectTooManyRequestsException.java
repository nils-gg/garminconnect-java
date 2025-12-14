package com.garmin.connect.exceptions;


/**
 * Exception thrown when rate limit is exceeded
 */
class GarminConnectTooManyRequestsException extends GarminConnectException {
    public GarminConnectTooManyRequestsException(String message) {
        super(message);
    }
}
