package com.shcheglov.task.registrationservice;

import com.shcheglov.task.registrationservice.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyList;

/**
 * @author Anton Shcheglov
 */
@SpringBootApplication
@Slf4j
public class RegistrationServiceApplication {

    @Value("classpath:blocked_iins.txt")
    private Resource resource;

    public static void main(String[] args) {
        SpringApplication.run(RegistrationServiceApplication.class, args);
    }

    @Bean("accountsByUsername")
    public Map<String, Account> accountsByUsername() {
        return new ConcurrentHashMap<>();
    }

    @Bean("blockedIINs")
    public List<String> blockedIINs() {
        try {
            final List<String> blockedIINs = Files.readAllLines(resource.getFile().toPath());
            log.info("The following issuer identification numbers found in blocked_iins.txt: [" + String.join(", ", blockedIINs) + "]");
            return blockedIINs;
        } catch (IOException e) {
            log.warn("Could not read the list of issuer identification numbers!", e);
            return emptyList();
        }
    }

}
