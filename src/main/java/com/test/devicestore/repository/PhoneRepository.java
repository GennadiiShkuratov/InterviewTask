package com.test.devicestore.repository;

import com.test.devicestore.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PhoneRepository extends JpaRepository<Phone, Long>, DeviceStorage<Phone>{

    Optional<Phone> getBySerialNumber(String serialNumber);

    @Transactional
    Integer deleteBySerialNumber(String serialNumber);

    @Override
    default Phone addOrUpdate(Phone phone) {
        return this.save(phone);
    }

    @Override
    default List<Phone> getAll() {
        return this.findAll();
    }


}
