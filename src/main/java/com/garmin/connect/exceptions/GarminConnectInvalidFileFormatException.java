package com.garmin.connect.exceptions;


/**
 * Exception thrown when invalid file format is provided
 */
class GarminConnectInvalidFileFormatException extends GarminConnectException {
    public GarminConnectInvalidFileFormatException(String message) {
        super(message);
    }
}
