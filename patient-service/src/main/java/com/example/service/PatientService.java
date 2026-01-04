package com.example.service;

import com.example.model.Patient;
import com.example.repository.PatientRepository;
import com.example.web.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(String id) {
        return patientRepository.findById(id).orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(String id, Patient updatedPatient) {
        Patient existing = getPatientById(id);

        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setGender(updatedPatient.getGender());
        existing.setDateOfBirth(updatedPatient.getDateOfBirth());
        existing.setEmail(updatedPatient.getEmail());
        existing.setContactNumber(updatedPatient.getContactNumber());

        return patientRepository.save(existing);
    }

    public void deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            throw new NotFoundException("Patient not found, womp womp: " + id);
        }
        patientRepository.deleteById(id);
    }

}
