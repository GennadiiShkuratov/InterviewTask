package com.test.devicestore.exception;

public class DeviceAlreadyBookedException extends DeviceBookingException {
    public DeviceAlreadyBookedException(String message) {
        super(message);
    }
}
