package com.test.devicestore.service;

import com.test.devicestore.domain.Device;
import com.test.devicestore.dto.request.DeviceBookingRequest;
import com.test.devicestore.dto.request.DeviceUnbookRequest;
import com.test.devicestore.exception.DeviceBookingException;

public interface DeviceBookingManager<D extends Device, B extends DeviceBookingRequest, R extends DeviceUnbookRequest> {

        D book(B bookingRequest ) throws DeviceBookingException;

        D unbook(R returnRequest) throws DeviceBookingException;
}
