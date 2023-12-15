package com.test.devicestore.controller;

import com.test.devicestore.domain.Phone;
import com.test.devicestore.repository.PhoneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Heavy component test. Should not be part of Unit tests")
class PhoneBookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PhoneRepository phoneRepository;

    private String serialNumber1 = "SN34343";
    private String serialNumber2 = "SN768768";
    private String serialNumber3 = "SN992323";

    @BeforeEach
    @Transactional
    public void initializeState(){
        phoneRepository.getAll().forEach(v -> System.out.println(v.toString()));

        phoneRepository.save(new Phone(serialNumber1, "Iphone 15"));
        phoneRepository.save(new Phone(serialNumber2, "Iphone 15 Pro"));
        phoneRepository.save(new Phone(serialNumber3, "Samsung X1"));
        phoneRepository.save(new Phone("SNFGDF45ASDF", "Xiaomi Viu"));

    }

    @AfterEach
    @Transactional
    public void cleanData(){
        phoneRepository.deleteAll();
    }

    @Test
    public void bookPhoneForUser_whenSuchAvailable() throws Exception {
        //Given
        String customer1 = "user1";

        //When
        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/book", serialNumber1))
                .accept(MediaType.APPLICATION_JSON)
                .content(customer1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(serialNumber1));

        //Then
        Phone phone1 = phoneRepository.getBySerialNumber(serialNumber1).get();
        Assertions.assertThat(phone1.getBooked()).isTrue();
        Assertions.assertThat(phone1.getBookedBy()).isEqualTo(customer1);
        Assertions.assertThat(phone1.getBookedAt()).isNotNull();
    }

    @Test
    public void failToBookPhoneForUser_whenPhoneNoAvailableForReservation() throws Exception {
        //Given
        String customer1 = "customer1";
        String bookedBy = "AnotherCustomer";
        LocalDateTime bookedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        //When
        phoneRepository.getBySerialNumber(serialNumber2)
                .map(v -> {
                    v.setBooked(true);
                    v.setBookedBy(bookedBy);
                    v.setBookedAt(bookedAt);
                    return v;
                })
                .ifPresent(v -> phoneRepository.saveAndFlush(v));

        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/book", serialNumber2))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(customer1))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Phone already booked by another user\"", result.getResolvedException().getMessage()));

        //Then
        Phone phone1 = phoneRepository.getBySerialNumber(serialNumber2).get();
        Assertions.assertThat(phone1.getBooked()).isTrue();
        Assertions.assertThat(phone1.getBookedBy()).isEqualTo(bookedBy);
        Assertions.assertThat(phone1.getBookedAt()).isEqualTo(bookedAt);
    }

    @Test
    public void unbookPhone_whenSuchBookedAlready() throws Exception {
        //Given
        String bookedBy = "bookedBy";
        LocalDateTime bookedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        //When
        phoneRepository.getBySerialNumber(serialNumber2)
                .map(v -> {
                    v.setBooked(true);
                    v.setBookedBy(bookedBy);
                    v.setBookedAt(bookedAt);
                    return v;
                })
                .ifPresent(v -> phoneRepository.saveAndFlush(v));

        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/unbook", serialNumber2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(serialNumber2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booked").value("Available"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedBy").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedAt").isEmpty());


        //Then
        Phone phone1 = phoneRepository.getBySerialNumber(serialNumber2).get();
        Assertions.assertThat(phone1.getBooked()).isFalse();
        Assertions.assertThat(phone1.getBookedBy()).isNull();
        Assertions.assertThat(phone1.getBookedAt()).isNull();
    }

    @Test
    public void repeatedAttemptToUnbookPhone_isSafe() throws Exception {
        //Given
        String bookedBy = "bookedBy";
        LocalDateTime bookedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        //When
        phoneRepository.getBySerialNumber(serialNumber2)
                .map(v -> {
                    v.setBooked(true);
                    v.setBookedBy(bookedBy);
                    v.setBookedAt(bookedAt);
                    return v;
                })
                .ifPresent(v -> phoneRepository.saveAndFlush(v));

        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/unbook", serialNumber2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(serialNumber2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booked").value("Available"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedBy").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedAt").isEmpty());

        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/unbook", serialNumber2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(serialNumber2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booked").value("Available"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedBy").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedAt").isEmpty());

        mvc.perform(MockMvcRequestBuilders.patch(String.format("/v1/api/devices/phones/%s/unbook", serialNumber2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.serialNumber").value(serialNumber2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booked").value("Available"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedBy").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookedAt").isEmpty());


        //Then
        Phone phone1 = phoneRepository.getBySerialNumber(serialNumber2).get();
        Assertions.assertThat(phone1.getBooked()).isFalse();
        Assertions.assertThat(phone1.getBookedBy()).isNull();;
        Assertions.assertThat(phone1.getBookedAt()).isNull();
    }


}