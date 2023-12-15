package com.test.devicestore.dto.request;

import java.util.Objects;

public class DeviceBookingRequest {
    private String serialNumber;

    private String bookedBy;

    public DeviceBookingRequest(String serialNumber, String bookedBy) {
        this.serialNumber = Objects.requireNonNull(serialNumber);
        this.bookedBy = Objects.requireNonNull(bookedBy);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBookedBy() {
        return bookedBy;
    }
}
