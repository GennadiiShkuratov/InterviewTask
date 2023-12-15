package com.test.devicestore.config;

import com.test.devicestore.domain.Phone;
import com.test.devicestore.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
public class DefaultDataInitializer implements CommandLineRunner {

    @Value("${phone-store.prefill:false}")
    private boolean prefillStore;

    private final PhoneRepository phoneRepository;

    public DefaultDataInitializer(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(!prefillStore) return;

        Path filePath = Paths.get(DefaultDataInitializer.class.getClassLoader().getResource("AvailablePhones.csv").toURI());

        try (Stream<String> lines = Files.lines(filePath)) {
            lines
                    .filter(Objects::nonNull)
                    .filter(v -> !v.isBlank())
                    .map(v -> v.split(","))
                    .filter(v -> v.length == 2)
                    .forEach(v -> {
                        if(phoneRepository.getBySerialNumber(v[0]).isPresent()) return;

                        try {
                            phoneRepository.save(new Phone(v[0].trim(), v[1].trim()));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    });
        }

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Path filePath = Paths.get(DefaultDataInitializer.class.getClassLoader().getResource("AvailablePhones.csv").toURI());

       Files.lines(filePath).forEach(System.out::println);
    }
}
