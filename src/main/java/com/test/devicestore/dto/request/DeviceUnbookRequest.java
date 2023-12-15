package com.test.devicestore.dto.request;

import java.util.Objects;

public class DeviceUnbookRequest {
    private String serialNumber;

    public DeviceUnbookRequest(String serialNumber) {
        this.serialNumber = Objects.requireNonNull(serialNumber);
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
