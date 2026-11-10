package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.infrastructure.persistence.entity.DiagnosesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaDiagnosesRepository
        extends JpaRepository<DiagnosesJpaEntity, UUID> {

    List<DiagnosesJpaEntity> findByPatientId(UUID patientId);

    List<DiagnosesJpaEntity> findByPractitionerId(UUID practitionerId);

    List<DiagnosesJpaEntity> findByEncounterId(UUID encounterId);

    List<DiagnosesJpaEntity> findByType(String type);
}