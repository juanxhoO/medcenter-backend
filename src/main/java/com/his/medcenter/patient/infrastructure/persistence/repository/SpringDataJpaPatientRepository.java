package com.his.medcenter.patient.infrastructure.persistence.repository;

import com.his.medcenter.patient.infrastructure.persistence.entity.PatientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaPatientRepository extends JpaRepository<PatientJpaEntity, UUID> {

    Optional<PatientJpaEntity> findByUserId(UUID userId);

    Optional<PatientJpaEntity> findByMedicalRecordNumber(String medicalRecordNumber);

    Optional<PatientJpaEntity> findByNationalId(String nationalId);

    boolean existsByUserId(UUID userId);

    boolean existsByMedicalRecordNumber(String medicalRecordNumber);
}

