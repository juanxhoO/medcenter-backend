package com.his.medcenter.clinical.domain.repository;

import com.his.medcenter.clinical.domain.model.Diagnoses;
import com.his.medcenter.clinical.domain.model.DiagnosesId;
import com.his.medcenter.clinical.domain.model.DiagnosesType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository interface (Outbound Port) for Diagnoses aggregates.
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface DiagnosesRepository {

    Diagnoses save(Diagnoses diagnosis);

    Optional<Diagnoses> findById(DiagnosesId id);

    List<Diagnoses> findAll();

    List<Diagnoses> findByPatientId(UUID patientId);

    List<Diagnoses> findByPractitionerId(UUID practitionerId);

    List<Diagnoses> findByEncounterId(UUID encounterId);

    List<Diagnoses> findByType(DiagnosesType type);

    void deleteById(DiagnosesId id);
}