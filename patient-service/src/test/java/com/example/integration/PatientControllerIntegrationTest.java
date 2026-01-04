package com.example.integration;

import com.example.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientControllerIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    void createThenGetAll_shouldContainCreated() {
        Patient p = new Patient();
        p.setFirstName("Anna");
        p.setLastName("Ivanova");
        p.setGender("F");
        p.setDateOfBirth(LocalDate.of(2000, 1, 1));
        p.setEmail("anna@test.com");
        p.setContactNumber("+431234567");

        ResponseEntity<Patient> createdResp = rest.postForEntity("/patients", p, Patient.class);
        assertThat(createdResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdResp.getBody()).isNotNull();
        assertThat(createdResp.getBody().getId()).isNotBlank();

        ResponseEntity<Patient[]> allResp = rest.getForEntity("/patients", Patient[].class);
        assertThat(allResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allResp.getBody()).isNotNull();
        assertThat(allResp.getBody().length).isGreaterThan(0);
    }

    @Test
    void deleteThenGet_shouldReturn404() {
        Patient p = new Patient();
        p.setFirstName("Delete");
        p.setLastName("Me");
        p.setGender("F");
        p.setDateOfBirth(LocalDate.of(2000, 1, 1));
        p.setEmail("del@test.com");
        p.setContactNumber("+431111111");

        Patient created = rest.postForEntity("/patients", p, Patient.class).getBody();
        assertThat(created).isNotNull();

        rest.delete("/patients/" + created.getId());

        ResponseEntity<String> getAfterDelete = rest.getForEntity("/patients/" + created.getId(), String.class);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
