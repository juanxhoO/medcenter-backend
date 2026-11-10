package com.his.medcenter.clinical.domain.repository;

import com.his.medcenter.clinical.domain.model.VitalSigns;
import com.his.medcenter.clinical.domain.model.VitalSignsId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository interface (Outbound Port) for VitalSigns aggregates.
 * Defines persistence contracts without coupling to database frameworks.
 */
public interface VitalSignsRepository {

    VitalSigns save(VitalSigns vitalSigns);

    Optional<VitalSigns> findById(VitalSignsId id);

    List<VitalSigns> findAll();

    List<VitalSigns> findByPatientId(UUID patientId);

    List<VitalSigns> findByEncounterId(UUID encounterId);

    List<VitalSigns> findByPractitionerId(UUID practitionerId);

    void deleteById(VitalSignsId id);
}