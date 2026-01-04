package com.example.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class PatientClient {

    private static final String BASE_URL = "http://localhost:8080/patients";

    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();

        // creating new patient
        Map<String, Object> newPatient = Map.of(
                "firstName", "Elina",
                "lastName", "Omurkulova",
                "gender", "FEMALE",
                "dateOfBirth", "2003-08-24",
                "email", "elina.omurkulova@test.com",
                "contactNumber", "+49123456789");

        ResponseEntity<String> created = restTemplate.postForEntity(BASE_URL, newPatient, String.class);

        System.out.println("CREATE RESPONSE:");
        System.out.println(created.getBody());

        // getting all patients
        String allPatients = restTemplate.getForObject(BASE_URL, String.class);

        System.out.println("\nALL PATIENTS:");
        System.out.println(allPatients);
    }
}
