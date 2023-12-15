package com.test.devicestore.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(indexes = {
        @Index(columnList = "serialNumber")
})
public class Phone implements Device{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty(message = "Serial number cannot be empty")
    @Column(unique = true)
    private String serialNumber;
    @NotEmpty(message = "Model cannot be empty")
    private String model;

    private boolean booked;
    private String bookedBy;

    private LocalDateTime bookedAt;


    public Phone() {
    }

    public Phone(String serialNumber, String model) {
        this.serialNumber = serialNumber;
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(serialNumber, phone.serialNumber) && Objects.equals(model, phone.model) && Objects.equals(booked, phone.booked) && Objects.equals(bookedBy, phone.bookedBy) && Objects.equals(bookedAt, phone.bookedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, model, booked, bookedBy, bookedAt);
    }

    private Phone(long id, String serialNumber, String model, boolean booked, String bookedBy, LocalDateTime bookedAt) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.model = model;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.bookedAt = bookedAt;
    }

    public static Phone clone(Phone phone){
        if(Objects.isNull(phone)) return null;

        return new Phone(
                phone.id,
                phone.serialNumber,
                phone.model,
                phone.booked,
                phone.bookedBy,
                phone.bookedAt);
    }
}
