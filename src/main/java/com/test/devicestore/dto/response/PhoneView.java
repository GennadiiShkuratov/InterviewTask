package com.test.devicestore.dto.response;

import com.test.devicestore.domain.Phone;

import java.util.Objects;
import java.util.Optional;

public record PhoneView (String serialNumber,
                        String model,
                        String booked,
                        String bookedBy,
                        String bookedAt) {
    public static PhoneView from(Phone phone) {
        return new PhoneView(phone.getSerialNumber(),
                phone.getModel(),
                (Objects.nonNull(phone.getBooked()) && phone.getBooked()) ? "Booked" : "Available",
                Optional.ofNullable(phone.getBookedBy()).orElse(""),
                Optional.ofNullable(phone.getBookedAt())
                        .map(v -> v.toString())
                        .orElse("")
        );
    }

}
