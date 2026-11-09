package com.his.medcenter.clinical.infrastructure.persistence.repository;

import com.his.medcenter.clinical.infrastructure.persistence.entity.PrescriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaPrescriptionRepository extends JpaRepository<PrescriptionJpaEntity, UUID> {

    List<PrescriptionJpaEntity> findByPatientId(UUID patientId);

    List<PrescriptionJpaEntity> findByEncounterId(UUID encounterId);

    List<PrescriptionJpaEntity> findByPractitionerId(UUID practitionerId);

    List<PrescriptionJpaEntity> findByStatus(String status);
}

