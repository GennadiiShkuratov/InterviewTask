package com.test.devicestore.dto.request;

import java.util.Objects;

public record NewDeviceRequest(String serialNumber, String model){
    public NewDeviceRequest {
        Objects.requireNonNull(serialNumber);
        Objects.requireNonNull(model);
    }
}
