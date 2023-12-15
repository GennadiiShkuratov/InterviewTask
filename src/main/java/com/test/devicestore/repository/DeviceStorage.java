package com.test.devicestore.repository;

import com.test.devicestore.domain.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceStorage<T extends Device> {
    T addOrUpdate(T device) throws Exception;

    List<T> getAll();

    Integer deleteBySerialNumber(String serialNumber);

    Optional<T> getBySerialNumber(String serialNumber);
}
