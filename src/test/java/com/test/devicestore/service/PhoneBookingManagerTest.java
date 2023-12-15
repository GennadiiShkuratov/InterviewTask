package com.test.devicestore.service;

import com.test.devicestore.domain.Device;
import com.test.devicestore.domain.Phone;
import com.test.devicestore.dto.request.DeviceBookingRequest;
import com.test.devicestore.dto.request.DeviceUnbookRequest;
import com.test.devicestore.exception.*;
import com.test.devicestore.repository.DeviceStorage;
import com.test.devicestore.repository.PhoneRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PhoneBookingManagerTest {

    private DeviceStorage phoneRepository;
    private class DeviceStorageForTesting implements DeviceStorage {
        private Map<String, Device> map = new HashMap<>();

        @Override
        public Device addOrUpdate(Device device) throws Exception {
            map.put(device.getSerialNumber(), device);
            return device;
        }

        @Override
        public List getAll() {
            return map.values().stream().toList();
        }

        @Override
        public Integer deleteBySerialNumber(String serialNumber) {
            return Optional.ofNullable(map.remove(serialNumber)).isPresent() ? 1 : 0;
        }

        @Override
        public Optional getBySerialNumber(String serialNumber) {
            return Optional.ofNullable(map.get(serialNumber));
        }
    };

    private String sn1 = "SN3242333";
    private String sn2 = "SN5654643";
    private String sn3 = "SN3453443";
    private String sn4 = "SN3768678";

    @BeforeEach
    public void init() throws Exception {
        phoneRepository = new DeviceStorageForTesting();
        phoneRepository.addOrUpdate(new Phone(sn1, "Iphone 1"));
        phoneRepository.addOrUpdate(new Phone(sn2, "Iphone 1"));
        phoneRepository.addOrUpdate(new Phone(sn3, "Xiaomi"));
        phoneRepository.addOrUpdate(new Phone(sn4, "Iphone 1"));
    }

    @Test
    public void success_onBookingFreePhone() throws Exception {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = sn1;
        String bookedBy = "customer1";

        //When
        Phone phone = phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));

        //Then
        Assertions.assertEquals(phone.getSerialNumber(), bookingSerialNumber);
        Assertions.assertTrue(phone.getBooked());
        Assertions.assertEquals(phone.getBookedBy(), bookedBy);
        Assertions.assertNotNull(phone.getBookedAt());
    }

    @Test
    public void getException_onBookingPhone_whichNotExists(){
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = "UnknownSN";
        String bookedBy = "customer1";

        //When-Then
        assertThatThrownBy(() -> {
            phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));

        }).isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("No phone found");
    }

    @Test
    public void getException_onBookingPhone_whichAlreadyBooked() throws DeviceBookingException {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = sn2;
        String bookedBy = "customer1";

        //When-Then
        phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));
        assertThatThrownBy(() -> {
            phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));
        }).isInstanceOf(DeviceAlreadyBookedException.class)
                .hasMessageContaining("Phone already booked by another user");

        assertThatThrownBy(() -> {
            phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));
        }).isInstanceOf(DeviceAlreadyBookedException.class)
                .hasMessageContaining("Phone already booked by another user");
    }

    @Test
    public void successfully_rebookPhone() throws DeviceBookingException {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = sn1;
        String bookedBy = "customer1";

        //When
        phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));
        phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));
        Phone phone = phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));

        //Then
        Assertions.assertEquals(phone.getSerialNumber(), bookingSerialNumber);
        Assertions.assertTrue(phone.getBooked());
        Assertions.assertEquals(phone.getBookedBy(), bookedBy);
        Assertions.assertTrue(nonNull(phone.getBookedAt()));
    }

    @Test
    public void getException_onBookingPhone_whenUnexpectedException_isOnDeviceStorage() {
        //Given
        String bookingSerialNumber = sn2;
        String bookedBy = "customer1";
        Phone phone = new Phone(bookingSerialNumber, "");

        PhoneRepository phoneRepository = Mockito.mock(PhoneRepository.class);
        Mockito.when(phoneRepository.getBySerialNumber(bookingSerialNumber)).thenReturn(Optional.ofNullable(phone));
        Mockito.when(phoneRepository.addOrUpdate(phone)).thenThrow(RuntimeException.class);
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        //When-Then
        assertThatThrownBy(() -> {
            phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));
        }).isInstanceOf(DeviceBookingException.class)
                .hasMessageContaining("Failed to book phone");
    }

    @Test
    public void success_onUnbookingPhone_whichAlreadyBooked() throws DeviceBookingException {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = sn1;
        String bookedBy = "customer1";
        phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));

        //When
        Phone phone = phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));

        //Then
        Assertions.assertEquals(phone.getSerialNumber(), bookingSerialNumber);
        Assertions.assertFalse(phone.getBooked());
        Assertions.assertNull(phone.getBookedBy());
        Assertions.assertNull(phone.getBookedAt());
    }

    @Test
    public void safe_unbookingPhone_whichAlreadyFree() throws DeviceBookingException {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String bookingSerialNumber = sn1;
        String bookedBy = "customer1";
        phoneBookingManager.book(new DeviceBookingRequest(bookingSerialNumber, bookedBy));

        //When-Then
        Phone phone1 = phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));
        Assertions.assertEquals(phone1.getSerialNumber(), bookingSerialNumber);
        Assertions.assertFalse(phone1.getBooked());
        Assertions.assertNull(phone1.getBookedBy());
        Assertions.assertNull(phone1.getBookedAt());

        Phone phone2 = phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));
        Assertions.assertEquals(phone2.getSerialNumber(), bookingSerialNumber);
        Assertions.assertFalse(phone2.getBooked());
        Assertions.assertNull(phone2.getBookedBy());
        Assertions.assertNull(phone2.getBookedAt());
    }

    @Test
    public void getException_onUnbookingPhone_whichNotExists(){
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);
        String bookingSerialNumber = "UnknownSN";

        //When-Then
        assertThatThrownBy(() -> {
            phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));

        }).isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("No phone found");
    }

    @Test
    public void getException_onUnbookingPhone_whenUnexpectedException_isOnDeviceStorage(){
        //Given
        String bookingSerialNumber = sn2;
        String bookedBy = "customer1";
        Phone phone = new Phone(bookingSerialNumber, "");
        phone.setBooked(true);

        PhoneRepository phoneRepository = Mockito.mock(PhoneRepository.class);
        Mockito.when(phoneRepository.getBySerialNumber(bookingSerialNumber)).thenReturn(Optional.ofNullable(phone));
        Mockito.when(phoneRepository.addOrUpdate(phone)).thenThrow(RuntimeException.class);
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        //When-Then
        assertThatThrownBy(() -> {
            phoneBookingManager.unbook(new DeviceUnbookRequest(bookingSerialNumber));
        }).isInstanceOf(DeviceBookingException.class)
                .hasMessageContaining("Failed to unbook phone");
    }

    @Test
    public void success_onAddNewPhone() throws DeviceAlreadyExistException, AddNewDeviceException {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String newDeviceSerialNumber = "SN4534567";
        String model = "Model 1";

        //When
        Phone phone = phoneBookingManager.add(new Phone(newDeviceSerialNumber, model));

        //Then
        Optional<Phone> addedPhone = phoneRepository.getBySerialNumber(newDeviceSerialNumber);
        Assertions.assertTrue(addedPhone.isPresent());
        Assertions.assertEquals(phone.getSerialNumber(), addedPhone.get().getSerialNumber());
        Assertions.assertEquals(model, addedPhone.get().getModel());
    }

    @Test
    public void success_onDeletePhone() throws Exception {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String newDeviceSerialNumber = "SN4534567";
        String model = "Model 1";
        phoneRepository.addOrUpdate(new Phone(newDeviceSerialNumber, model));

        //When
        boolean removed = phoneBookingManager.deleteBySerialNumber(newDeviceSerialNumber);

        //Then
        Assertions.assertTrue(removed);
        Optional<Phone> removedPhone = phoneRepository.getBySerialNumber(newDeviceSerialNumber);
        Assertions.assertFalse(removedPhone.isPresent());
    }

    @Test
    public void success_readAllPhonesInStock() throws Exception {
        //Given
        PhoneBookingManager phoneBookingManager = new PhoneBookingManager(phoneRepository);

        String model = "Model 1";
        phoneRepository.addOrUpdate(new Phone("sn5", model));
        phoneRepository.addOrUpdate(new Phone("sn6", model));
        phoneRepository.addOrUpdate(new Phone("sn7", model));
        phoneRepository.addOrUpdate(new Phone("sn8", model));

        //When
        List<Phone> allInStock = phoneBookingManager.getAllInStock();

        //Then
        Assertions.assertFalse(allInStock.isEmpty());
        Assertions.assertEquals(8, allInStock.size());
    }

}