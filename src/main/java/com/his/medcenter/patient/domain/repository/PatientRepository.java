package com.his.medcenter.patient.domain.repository;

import com.his.medcenter.patient.domain.model.Patient;
import com.his.medcenter.patient.domain.model.PatientId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository interface (Outbound Port) for Patient aggregates.
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(PatientId id);

    Optional<Patient> findByUserId(UUID userId);

    Optional<Patient> findByMedicalRecordNumber(String mrn);

    Optional<Patient> findByNationalId(String nationalId);

    List<Patient> findAll();

    boolean existsByUserId(UUID userId);

    boolean existsByMedicalRecordNumber(String mrn);

    void deleteById(PatientId id);
}

