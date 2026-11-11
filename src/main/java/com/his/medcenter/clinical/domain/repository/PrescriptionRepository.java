package com.his.medcenter.clinical.domain.repository;

import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.Prescription;
import com.his.medcenter.clinical.domain.model.PrescriptionId;
import com.his.medcenter.clinical.domain.model.PrescriptionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository interface (Outbound Port) for Prescription aggregates.
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface PrescriptionRepository {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(PrescriptionId id);

    List<Prescription> findAll();

    List<Prescription> findByPatientId(UUID patientId);

    List<Prescription> findByEncounterId(EncounterId encounterId);

    List<Prescription> findByPractitionerId(UUID practitionerId);

    List<Prescription> findByStatus(PrescriptionStatus status);

    void deleteById(PrescriptionId id);
}

