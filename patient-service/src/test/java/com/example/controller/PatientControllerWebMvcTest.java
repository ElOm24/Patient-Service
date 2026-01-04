package com.example.controller;

import com.example.controller.PatientController;
import com.example.model.Patient;
import com.example.service.PatientService;
import com.example.web.GlobalExceptionHandler;
import com.example.web.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@Import(GlobalExceptionHandler.class)
class PatientControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PatientService patientService;

    @Test
    void getAll_shouldReturn200_andList() throws Exception {
        Patient p = new Patient();
        LocalDate dob = LocalDate.of(2000, 1, 1);
        p.setId("abc123");
        p.setFirstName("Anna");
        p.setLastName("Ivanova");
        p.setGender("F");
        p.setDateOfBirth(dob);
        p.setEmail("anna@test.com");
        p.setContactNumber("+431234567");

        when(patientService.getAllPatients()).thenReturn(List.of(p));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("abc123"))
                .andExpect(jsonPath("$[0].firstName").value("Anna"));

        verify(patientService).getAllPatients();
    }

    @Test
    void getOne_shouldReturn200_whenExists() throws Exception {
        Patient p = new Patient();
        p.setId("abc123");
        p.setFirstName("Anna");

        when(patientService.getPatientById("abc123")).thenReturn(p);

        mockMvc.perform(get("/patients/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void getOne_shouldReturn404_whenNotFound() throws Exception {
        when(patientService.getPatientById("missing"))
                .thenThrow(new NotFoundException("Patient not found: missing"));

        mockMvc.perform(get("/patients/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_shouldReturn201_andBody() throws Exception {
        Patient input = new Patient();
        input.setFirstName("Anna");
        input.setLastName("Ivanova");
        input.setGender("F");
        input.setDateOfBirth(LocalDate.of(2000, 1, 1));
        input.setEmail("anna@test.com");
        input.setContactNumber("+431234567");

        Patient created = new Patient();
        created.setId("newid");
        created.setFirstName("Anna");

        when(patientService.createPatient(ArgumentMatchers.any(Patient.class))).thenReturn(created);

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("newid"))
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(patientService).deletePatient("abc123");

        mockMvc.perform(delete("/patients/abc123"))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatient("abc123");
    }
}
