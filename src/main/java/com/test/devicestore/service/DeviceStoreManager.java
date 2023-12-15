package com.test.devicestore.service;

import com.test.devicestore.domain.Device;
import com.test.devicestore.domain.Phone;
import com.test.devicestore.exception.AddNewDeviceException;
import com.test.devicestore.exception.DeviceAlreadyExistException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DeviceStoreManager <D extends Device> {
    Phone add(Phone phone) throws AddNewDeviceException, DeviceAlreadyExistException;

    List<D> getAllInStock();

    boolean deleteBySerialNumber(String serialNumber);
}
