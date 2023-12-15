package com.test.devicestore.service;

import com.test.devicestore.domain.Phone;
import com.test.devicestore.dto.request.DeviceBookingRequest;
import com.test.devicestore.dto.request.DeviceUnbookRequest;
import com.test.devicestore.exception.*;
import com.test.devicestore.repository.DeviceStorage;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class PhoneBookingManager implements DeviceBookingManager<Phone, DeviceBookingRequest, DeviceUnbookRequest>, DeviceStoreManager<Phone>{

    private DeviceStorage<Phone> phoneStorage;

    public PhoneBookingManager(DeviceStorage<Phone> phoneStorage) {
        this.phoneStorage = phoneStorage;
    }

    @Override
    @Transactional
    public Phone book(DeviceBookingRequest bookingRequest) throws DeviceBookingException {
        Phone phone = phoneStorage.getBySerialNumber(bookingRequest.getSerialNumber())
                .orElseThrow(() -> new DeviceNotFoundException("No phone found"));

        if(phone.getBooked()) throw new DeviceAlreadyBookedException("Phone already booked by another user");

        phone.setBooked(true);
        phone.setBookedBy(bookingRequest.getBookedBy());
        phone.setBookedAt(LocalDateTime.now());

        try {
            return Phone.clone(phoneStorage.addOrUpdate(phone));
        } catch (Exception e) {
            throw new DeviceBookingException("Failed to book phone");
        }

    }

    @Override
    @Transactional
    public Phone unbook(DeviceUnbookRequest returnRequest) throws DeviceBookingException {
        Phone phone = phoneStorage.getBySerialNumber(returnRequest.getSerialNumber())
                .orElseThrow(() -> new DeviceNotFoundException("No phone found"));

        if(!phone.getBooked()) return phone;

        phone.setBooked(false);
        phone.setBookedBy(null);
        phone.setBookedAt(null);

        try {
            return Phone.clone(phoneStorage.addOrUpdate(phone));
        } catch (Exception e) {
            throw new DeviceBookingException("Failed to unbook phone");
        }

    }

    @Transactional
    public Phone add(Phone phone) throws AddNewDeviceException, DeviceAlreadyExistException {
        try {
            if (phoneStorage.getBySerialNumber(phone.getSerialNumber())
                    .isPresent()) throw new DeviceAlreadyExistException("Device " + phone.getSerialNumber() + " already exist");

            return Phone.clone(phoneStorage.addOrUpdate(phone));

        } catch (Exception e) {
            throw new AddNewDeviceException("Failed to add new phone: " + phone.getSerialNumber(), e);
        }
    }

    public List<Phone> getAllInStock() {
        return phoneStorage.getAll()
                .stream()
                .map(Phone::clone)
                .toList();
    }

    @Transactional
    public boolean deleteBySerialNumber(String serialNumber) {
        return phoneStorage.deleteBySerialNumber(serialNumber) > 0;
    }

}
