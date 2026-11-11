package com.his.medcenter.clinical.domain.repository;

import com.his.medcenter.clinical.domain.model.Encounter;
import com.his.medcenter.clinical.domain.model.EncounterId;
import com.his.medcenter.clinical.domain.model.EncounterStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository interface (Outbound Port) for Encounter aggregates.
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface EncounterRepository {

    Encounter save(Encounter encounter);

    Optional<Encounter> findById(EncounterId id);

    List<Encounter> findAll();

    List<Encounter> findByPatientId(UUID patientId);

    List<Encounter> findByPractitionerId(UUID practitionerId);

    List<Encounter> findByStatus(EncounterStatus status);

    void deleteById(EncounterId id);
}

