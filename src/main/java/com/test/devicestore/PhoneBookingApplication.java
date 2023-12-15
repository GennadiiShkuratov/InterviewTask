package com.test.devicestore;

import com.test.devicestore.repository.PhoneRepository;
import com.test.devicestore.service.PhoneBookingManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PhoneBookingApplication {


	@Bean
	public PhoneBookingManager phoneBookingManager(PhoneRepository phoneRepository){
		return new PhoneBookingManager(phoneRepository);
	}

	public static void main(String[] args) {
		SpringApplication.run(PhoneBookingApplication.class, args);
	}

}
