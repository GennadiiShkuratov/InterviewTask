package com.test.devicestore.controller;

import com.test.devicestore.dto.request.DeviceBookingRequest;
import com.test.devicestore.dto.request.DeviceUnbookRequest;
import com.test.devicestore.domain.Phone;
import com.test.devicestore.dto.request.NewDeviceRequest;
import com.test.devicestore.dto.response.PhoneView;
import com.test.devicestore.exception.DeviceAlreadyBookedException;
import com.test.devicestore.exception.DeviceAlreadyExistException;
import com.test.devicestore.exception.DeviceNotFoundException;
import com.test.devicestore.service.PhoneBookingManager;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparing;

@RestController
@RequestMapping("/v1/api/devices/phones")
public class PhoneBookingController {
    private final PhoneBookingManager phoneBookingManager;

    public PhoneBookingController(PhoneBookingManager phoneBookingManager) {
        this.phoneBookingManager = phoneBookingManager;
    }

    @PostMapping()
    public ResponseEntity<PhoneView> addPhone(@RequestBody NewDeviceRequest newPhoneRequest) {
        try {
            Phone newPhone = phoneBookingManager.add(new Phone(newPhoneRequest.serialNumber(), newPhoneRequest.model()));
            return ResponseEntity.ok(PhoneView.from(newPhone));

        } catch (DeviceAlreadyExistException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<PhoneView> deletePhone(@PathVariable @NotEmpty String serialNumber) {
        try {
            phoneBookingManager.deleteBySerialNumber(serialNumber);
            return ResponseEntity.noContent().build();

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @GetMapping()
    public List<PhoneView> getAllPhones() {
        try {
            return phoneBookingManager.getAllInStock()
                    .stream()
                    .filter(Objects::nonNull)
                    .sorted(comparing(Phone::getSerialNumber))
                    .map(PhoneView::from)
                    .toList();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{serialNumber}/book")
    public ResponseEntity<PhoneView> bookPhone(@PathVariable @NotEmpty String serialNumber, @RequestBody String bookedBy) {
        try {
            Phone phone = phoneBookingManager.book(new DeviceBookingRequest(serialNumber, bookedBy));
            return ResponseEntity.ok(PhoneView.from(phone));

        } catch (DeviceNotFoundException | DeviceAlreadyBookedException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{serialNumber}/unbook")
    public ResponseEntity<PhoneView> unbookPhone(@PathVariable @NotEmpty String serialNumber) {
        try {
            Phone phone = phoneBookingManager.unbook(new DeviceUnbookRequest(serialNumber));
            return ResponseEntity.ok(PhoneView.from(phone));

        } catch (DeviceNotFoundException  exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
