package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.infrastructure.persistence.entity.VitalSignsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaVitalSignsRepository
        extends JpaRepository<VitalSignsJpaEntity, UUID> {

    List<VitalSignsJpaEntity> findByPatientId(UUID patientId);

    List<VitalSignsJpaEntity> findByPractitionerId(UUID practitionerId);

    List<VitalSignsJpaEntity> findByEncounterId(UUID encounterId);
    
}